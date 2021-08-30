package com.curtisnewbie.module.outbox.publisher;

import com.curtisnewbie.module.outbox.dao.MessageEntity;
import com.curtisnewbie.module.outbox.service.MessageOutboxService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Worker for actually sending messages to broker
 *
 * @author yongjie.zhuang
 */
@Slf4j
public class PublishingWorker implements Runnable {

    private static final AtomicBoolean isClosing = new AtomicBoolean(false);
    private final MessagePoller poller;
    private final MessageOutboxService messageOutboxService;

    public PublishingWorker(MessagePoller poller, MessageOutboxService messageOutboxService) {
        this.poller = poller;
        this.messageOutboxService = messageOutboxService;
    }

    @Override
    public void run() {
        while (true) {
            if (isClosing.get())
                return;

            try {
                MessageEntity me = poller.poll();

                // publish the message to broker
                messageOutboxService.publishAndUpdate(me);
            } catch (InterruptedException e1) {
                if (!isClosing.get())
                    Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.info("Error occurred while sending message", e);
                if (isClosing.get())
                    return;
            }
        }
    }

    /**
     * Notify all worker that the application is shutting down
     */
    public static void notifyApplicationShutdown() {
        isClosing.set(true);
    }

}
