package com.curtisnewbie.module.outbox.publisher;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * Param for {@link com.curtisnewbie.module.outbox.publisher.TransactionalMessagePublisher}
 * </p>
 *
 * @author yongjie.zhuang
 */
@Data
public class PublishingParam {

    /**
     * Message payload, will be serialised as JSON string
     */
    @NotNull
    private final Object payload;

    /** Exchange name */
    @NotEmpty
    private final String exchange;

    /** Routing key */
    @NotEmpty
    private final String routingKey;

    @Builder
    public PublishingParam(@NotNull Object payload, @NotEmpty String exchange, @NotEmpty String routingKey) {
        this.payload = payload;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }
}
