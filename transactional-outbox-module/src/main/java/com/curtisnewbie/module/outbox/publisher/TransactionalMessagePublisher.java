package com.curtisnewbie.module.outbox.publisher;

import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * Transactional Publisher of message, only work for direct exchange
 * </p>
 *
 * @author yongjie.zhuang
 */
@Validated
public interface TransactionalMessagePublisher {

    /**
     * <p>
     * Publish message
     * </p>
     * <p>
     * Message is not published immediately to message broker, message is persisted in database first, then polled out
     * and sent to the broker.
     * </p>
     * <p>
     * The invocation of this method should be wrapped inside transaction for your business code, so that messages are
     * only sent when transaction commits.
     * </p>
     */
    void publish(@NotNull @Valid PublishingParam param);

}
