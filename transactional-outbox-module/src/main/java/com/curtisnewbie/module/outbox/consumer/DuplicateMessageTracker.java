package com.curtisnewbie.module.outbox.consumer;

import org.springframework.messaging.Message;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <p>
 * Tracker of duplicate messages
 * </p>
 *
 * @author yongjie.zhuang
 */
@Validated
public interface DuplicateMessageTracker {

    /**
     * Check if the message is a duplicate
     */
    boolean isDuplicateMessage(@NotNull Message<?> message);

    /**
     * Check if the message is a duplicate
     */
    boolean isDuplicateMessage(@NotNull org.springframework.amqp.core.Message message);

    /**
     * Mark the message as consumed
     */
    void markAsConsumed(@NotNull Message<?> message, @NotNull Date consumeTime);

    /**
     * Mark the message as consumed
     */
    void markAsConsumed(@NotNull org.springframework.amqp.core.Message message, @NotNull Date consumeTime);
}
