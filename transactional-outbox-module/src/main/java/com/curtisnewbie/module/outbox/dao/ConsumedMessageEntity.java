package com.curtisnewbie.module.outbox.dao;

import java.util.Date;

/**
 * Messages that are consumed
 *
 * @author yongjie.zhuang
 */
public class ConsumedMessageEntity {
    /** message id */
    private String messageId;

    /** time when the message was published/created */
    private Date createTime;

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
}