package com.curtisnewbie.module.outbox.dao;

import java.util.Date;

/**
 * Messages that will be published
 *
 * @author yongjie.zhuang
 */
public class MessageEntity {
    /** whether the message is published, 0-not published, 1-published */
    private Short isPublished;

    /** message id */
    private String messageId;

    /** time when the message was published/created */
    private Date createTime;

    /** exchange name */
    private String exchange;

    /** queue */
    private String queue;

    public Short getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Short isPublished) {
        this.isPublished = isPublished;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId == null ? null : messageId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange == null ? null : exchange.trim();
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue == null ? null : queue.trim();
    }
}