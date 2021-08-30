package com.curtisnewbie.module.outbox.consumer;

import org.springframework.messaging.Message;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
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
    boolean isDuplicateMessage(@NotEmpty Message<?> message);

    /**
     * Mark the message as consumed
     */
    void markAsConsumed(@NotEmpty Message<?> message, @NotNull Date consumeTime);
}
