package com.curtisnewbie.module.outbox.publisher;

import com.curtisnewbie.module.outbox.dao.MessageEntity;

/**
 * @author yongjie.zhuang
 */
public interface MessagePoller {

    /**
     * <p>
     * Poll message
     * </p>
     * <p>
     * This method may blocks when there is no message
     * </p>
     */
    MessageEntity poll();

}
