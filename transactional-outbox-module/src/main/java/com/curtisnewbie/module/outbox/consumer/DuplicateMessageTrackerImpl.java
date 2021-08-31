package com.curtisnewbie.module.outbox.consumer;

import com.curtisnewbie.module.outbox.common.MessageHeaderUtil;
import com.curtisnewbie.module.outbox.dao.ConsumedMessageEntity;
import com.curtisnewbie.module.outbox.dao.ConsumedMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author yongjie.zhuang
 */
@Service
@Transactional
public class DuplicateMessageTrackerImpl implements DuplicateMessageTracker {

    @Autowired
    private ConsumedMessageMapper consumedMessageMapper;

    @Override
    public boolean isDuplicateMessage(Message<?> message) {
        String messageId = MessageHeaderUtil.getMessageId(message);
        if (messageId == null)
            return false;

        return exists(messageId);
    }

    @Override
    public boolean isDuplicateMessage(@NotNull org.springframework.amqp.core.Message message) {
        String messageId = MessageHeaderUtil.getMessageId(message);
        if (messageId == null)
            return false;
        return exists(messageId);
    }

    @Override
    public void markAsConsumed(Message<?> message, Date consumeTime) {
        String messageId = MessageHeaderUtil.getMessageId(message);
        if (messageId == null)
            return;

        save(messageId, consumeTime);
    }

    @Override
    public void markAsConsumed(@NotNull org.springframework.amqp.core.Message message, @NotNull Date consumeTime) {
        String messageId = MessageHeaderUtil.getMessageId(message);
        if (messageId == null)
            return;

        save(messageId, consumeTime);
    }

    private void save(String messageId, Date consumeTime) {
        ConsumedMessageEntity e = new ConsumedMessageEntity();
        e.setMessageId(messageId);
        e.setConsumeTime(consumeTime);
        consumedMessageMapper.insert(e);
    }

    private boolean exists(String messageId) {
        return consumedMessageMapper.selectByPrimaryKey(messageId) != null;
    }
}
