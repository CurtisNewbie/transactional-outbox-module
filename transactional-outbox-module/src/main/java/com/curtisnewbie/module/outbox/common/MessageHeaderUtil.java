package com.curtisnewbie.module.outbox.common;


import org.springframework.messaging.Message;

/**
 * Util for message headers
 *
 * @author yongjie.zhuang
 */
public final class MessageHeaderUtil {

    private MessageHeaderUtil() {
    }

    public static final String MESSAGE_ID_HEADER_NAME = "mfrom-message-id";

    /**
     * Get message_id from header
     */
    public static String getMessageId(Message message) {
        if (message == null)
            return null;
        Object v = message.getHeaders().get(MESSAGE_ID_HEADER_NAME);
        if (v == null)
            return null;
        return String.valueOf(v);
    }
}
