package com.curtisnewbie.module.outbox.common;


import org.springframework.messaging.Message;

import java.util.Map;

/**
 * Util for message headers
 *
 * @author yongjie.zhuang
 */
public final class MessageHeaderUtil {

    private MessageHeaderUtil() {
    }

    public static final String MESSAGE_ID_HEADER_NAME = "transactional-outbox-message-id";

    /**
     * Get message_id from header
     */
    public static String getMessageId(Message message) {
        if (message == null)
            return null;
        return getMessageIdFromHeaders(message.getHeaders());
    }

    /**
     * Get message_id from header
     */
    public static String getMessageId(org.springframework.amqp.core.Message message) {
        if (message == null)
            return null;

        return getMessageIdFromHeaders(message.getMessageProperties().getHeaders());
    }

    public static String getMessageIdFromHeaders(Map<String, Object> headers) {
        Object v = headers.get(MESSAGE_ID_HEADER_NAME);
        if (v == null)
            return null;
        return String.valueOf(v);
    }
}
