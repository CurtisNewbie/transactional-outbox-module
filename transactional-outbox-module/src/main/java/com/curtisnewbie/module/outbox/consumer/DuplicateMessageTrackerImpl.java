package com.curtisnewbie.module.outbox.consumer;

import com.curtisnewbie.module.outbox.common.MessageHeaderUtil;
import com.curtisnewbie.module.outbox.dao.ConsumedMessageEntity;
import com.curtisnewbie.module.outbox.dao.ConsumedMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return consumedMessageMapper.selectByPrimaryKey(messageId) != null;
    }

    @Override
    public void markAsConsumed(Message<?> message, Date consumeTime) {
        String messageId = MessageHeaderUtil.getMessageId(message);
        if (messageId == null)
            return;

        ConsumedMessageEntity e = new ConsumedMessageEntity();
        e.setMessageId(messageId);
        e.setConsumeTime(consumeTime);
        consumedMessageMapper.insert(e);
    }
}
