package com.curtisnewbie.module.outbox.dao;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Messages that will be published
 *
 * @author yongjie.zhuang
 */
@Data
@NoArgsConstructor
public class MessageEntity {

    /** message id */
    private String messageId;

    /** whether the message is published, 0-not published, 1-published */
    private Integer isPublished;

    /** time when the message was published/created */
    private Date createTime;

    /** exchange name */
    private String exchange;

    /** routing key */
    private String routingKey;

    /** queue */
    private String queue;

    /** message body */
    private String payload;

    /** inferred type of payload */
    private String payloadTypeInfer;

    @Builder
    public MessageEntity(String messageId, Integer isPublished, Date createTime, String exchange, String routingKey,
                         String queue, String payload, String payloadTypeInfer) {
        this.messageId = messageId;
        this.isPublished = isPublished;
        this.createTime = createTime;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.queue = queue;
        this.payload = payload;
        this.payloadTypeInfer = payloadTypeInfer;
    }
}