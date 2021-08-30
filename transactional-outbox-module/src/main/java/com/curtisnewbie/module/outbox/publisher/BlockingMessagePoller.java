package com.curtisnewbie.module.outbox.publisher;

import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.module.outbox.dao.MessageEntity;
import com.curtisnewbie.module.outbox.service.MessageOutboxService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Polling messages for publishing
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Component
public class BlockingMessagePoller implements MessagePoller {

    private final BlockingQueue<MessageEntity> messages = new ArrayBlockingQueue<>(200);
    private volatile Thread bg;
    private final AtomicBoolean isClosing = new AtomicBoolean(false);

    @Autowired
    private MessageOutboxService messageOutboxService;


    @PostConstruct
    public void onInit() {
        bg = new Thread(() -> {
            final PagingVo p = new PagingVo().ofLimit(30).ofPage(1);
            while (true) {
                if (isClosing.get()) {
                    log.info("Application shutting down, terminating message poller");
                    return;
                }
                try {
                    PageInfo<MessageEntity> unpublishedInPage = messageOutboxService.findUnpublishedInPage(p);
                    for (MessageEntity me : unpublishedInPage.getList())
                        messages.put(me);
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

    @PreDestroy
    public void preDestroy() {
        isClosing.set(true);
        if (bg != null && bg.isAlive())
            bg.interrupt();
    }

    @Override
    public MessageEntity poll() {
        return messages.poll();
    }

}
