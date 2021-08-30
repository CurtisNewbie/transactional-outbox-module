package com.curtisnewbie.module.outbox.dao;

import java.util.Date;

/** Messages that are consumed */
public class ConsumedMessageEntity {
    /** message id */
    private String messageId;

    /** time when the message was consumed */
    private Date consumeTime;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId == null ? null : messageId.trim();
    }

    public Date getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(Date consumeTime) {
        this.consumeTime = consumeTime;
    }
}