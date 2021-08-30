package com.curtisnewbie.module.outbox.publisher;

import com.curtisnewbie.module.outbox.dao.MessageEntity;
import com.curtisnewbie.module.outbox.service.MessageOutboxService;
import lombok.extern.slf4j.Slf4j;

/**
 * Worker for actually sending messages to broker
 *
 * @author yongjie.zhuang
 */
@Slf4j
public class PublishingWorker implements Runnable {

    private final MessagePoller poller;
    private final MessageOutboxService messageOutboxService;

    public PublishingWorker(BlockingMessagePoller poller, MessageOutboxService messageOutboxService) {
        this.poller = poller;
        this.messageOutboxService = messageOutboxService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                MessageEntity me = poller.poll();

                // publish the message to broker
                messageOutboxService.publishAndUpdate(me);
            } catch (Exception e) {
                log.info("Error occurred while sending message", e);
            }
        }
    }

}
