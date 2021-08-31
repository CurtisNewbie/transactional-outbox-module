package com.curtisnewbie.module.outbox.publisher;

import com.curtisnewbie.module.outbox.dao.MessageEntity;

/**
 * <p>
 * Message Poller
 * </p>
 * <p>
 * Objects that poll messages from database, think of it as a task producer.
 * </p>
 *
 * @author yongjie.zhuang
 * @see PublishingWorker
 */
public interface MessagePoller {

    /**
     * <p>
     * Poll message
     * </p>
     * <p>
     * This method may blocks when there is no message
     * </p>
     */
    MessageEntity take() throws InterruptedException;

}
