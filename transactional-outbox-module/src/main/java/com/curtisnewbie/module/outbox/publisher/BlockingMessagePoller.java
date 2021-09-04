package com.curtisnewbie.module.outbox.publisher;

import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.module.outbox.config.ModuleConfig;
import com.curtisnewbie.module.outbox.dao.MessageEntity;
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
        final int waitTimeInMilliSec = moduleConfig.getMessagePollingWaitTime();

        bg = new Thread(() -> {
            final PagingVo p = new PagingVo().ofLimit(moduleConfig.getMessagePollingPageSize()).ofPage(1);
            while (true) {
                if (isClosing.get())
                    return;
                try {
                    int lastRemaining = messageBlockingQueue.size();
                    if (lastRemaining > 0) {
                        log.info("Message queues not empty ({} remaining), publishing works are unable to " +
                                "publish polled messages, you may consider to increase " +
                                "the number of PublishingWorker", lastRemaining);
                    }
                    PageInfo<MessageEntity> unpublishedInPage = messageOutboxService.findUnpublishedInPage(p);
                    for (MessageEntity me : unpublishedInPage.getList()) {
                        messageBlockingQueue.put(me);
                    }

                    if (waitTimeInMilliSec > 0) {
                        log.info("Wait {} milliseconds before next message polling", waitTimeInMilliSec);
                        Thread.sleep(waitTimeInMilliSec);
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
