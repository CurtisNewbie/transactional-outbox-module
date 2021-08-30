package com.curtisnewbie.module.outbox.consumer;

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
    boolean isDuplicateMessage(@NotEmpty String messageId);

    /**
     * Mark the message as consumed
     */
    void markAsConsumed(@NotEmpty String messageId, @NotNull Date consumeTime);
}
