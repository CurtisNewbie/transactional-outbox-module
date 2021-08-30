package com.curtisnewbie.module.outbox.service;

import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.module.messaging.service.MessagingParam;
import com.curtisnewbie.module.messaging.service.MessagingService;
import com.curtisnewbie.module.outbox.common.MessageIsPublished;
import com.curtisnewbie.module.outbox.dao.MessageEntity;
import com.curtisnewbie.module.outbox.dao.MessageMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author yongjie.zhuang
 */
@Service
@Transactional
public class MessageOutboxServiceImpl implements MessageOutboxService {

    @Autowired
    private MessageMapper mapper;

    @Autowired
    private MessagingService messagingService;

    @Override
    public PageInfo<MessageEntity> findUnpublishedInPage(@NotNull PagingVo pagingVo) {
        PageHelper.startPage(pagingVo.getPage(), pagingVo.getLimit());
        return PageInfo.of(mapper.selectUnpublished());
    }

    @Override
    public void publishAndUpdate(MessageEntity msg) {
        // only publish if it's not yet being published, if the errors occurred while sending, rollback
        if (mapper.updateIsPublished(msg.getMessageId(), MessageIsPublished.NO.val, MessageIsPublished.YES.val) == 1) {
            messagingService.send(MessagingParam.builder()
                    .payload(msg.getPayload())
                    .exchange(msg.getExchange())
                    .routingKey(msg.getRoutingKey())
                    .deliveryMode(MessageDeliveryMode.NON_PERSISTENT)
                    .messagePostProcessor(new TypeInferMessagePostProcessor(msg))
                    .build());
        }
    }

    private static class TypeInferMessagePostProcessor implements MessagePostProcessor {

        private final MessageEntity me;

        public TypeInferMessagePostProcessor(MessageEntity me) {
            this.me = me;
        }

        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            // for jackson to deserialize messages based on the type info
            Map<String, Object> headers = message.getMessageProperties().getHeaders();
            headers.put(AbstractJavaTypeMapper.DEFAULT_CLASSID_FIELD_NAME, me.getPayloadTypeInfer());
            return message;
        }
    }

}
