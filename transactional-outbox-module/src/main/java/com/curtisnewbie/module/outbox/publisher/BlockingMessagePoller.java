package com.curtisnewbie.module.outbox.publisher;

import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.module.outbox.config.ModuleConfig;
import com.curtisnewbie.module.outbox.dao.MessageEntity;
import com.curtisnewbie.module.outbox.service.MessageOutboxService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Polling messages for publishing
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Component
public class BlockingMessagePoller implements MessagePoller {

    private BlockingQueue<MessageEntity> messageBlockingQueue;
    private volatile Thread bg;
    private final AtomicBoolean isClosing = new AtomicBoolean(false);

    @Autowired
    private MessageOutboxService messageOutboxService;
    @Autowired
    private ModuleConfig moduleConfig;

    @PostConstruct
    public void _onInit() {
        this.messageBlockingQueue = new ArrayBlockingQueue<>(moduleConfig.getMessagePollingTotalLimit());
        _startPollingThread();
    }

    @PreDestroy
    public void _preDestroy() {
        log.info("Application shutting down, terminating message poller");
        isClosing.set(true);
        if (bg != null && bg.isAlive())
            bg.interrupt();
    }

    @Override
    public MessageEntity take() throws InterruptedException {
        return messageBlockingQueue.take();
    }

    private void _startPollingThread() {
        bg = new Thread(() -> {
            final PagingVo p = new PagingVo().ofLimit(30).ofPage(1);
            while (true) {
                if (isClosing.get())
                    return;
                try {
                    PageInfo<MessageEntity> unpublishedInPage = messageOutboxService.findUnpublishedInPage(p);
                    for (MessageEntity me : unpublishedInPage.getList()) {
                        messageBlockingQueue.put(me);
                    }
                } catch (InterruptedException e) {
                    // never stop unless the application is shutting down
                    if (!isClosing.get()) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        bg.setDaemon(true);
        bg.start();
    }

}
