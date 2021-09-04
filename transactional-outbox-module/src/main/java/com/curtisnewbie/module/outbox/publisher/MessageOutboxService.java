package com.curtisnewbie.module.outbox.publisher;

import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.module.outbox.dao.MessageEntity;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * Service for message table
 * </p>
 * <p>
 * Don't use this service directly, use {@link TransactionalMessagePublisher#publish(PublishingParam)}
 * </p>
 *
 * @author yongjie.zhuang
 */
@Validated
public interface MessageOutboxService {

    /**
     * Find unpublished messages in pages
     */
    PageInfo<MessageEntity> findUnpublishedInPage(@NotNull PagingVo pagingVo);

    /**
     * <p>
     * Publish the message to broker and update it as published within a single transaction
     * </p>
     *
     * @see PublishingWorker
     * @see TransactionalMessagePublisher
     */
    void publishAndUpdate(@NotNull MessageEntity msg);

}
