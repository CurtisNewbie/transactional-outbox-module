package com.curtisnewbie.module.outbox.service;

import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.module.messaging.service.MessagingParam;
import com.curtisnewbie.module.messaging.service.MessagingService;
import com.curtisnewbie.module.outbox.common.MessageHeaderUtil;
import com.curtisnewbie.module.outbox.common.MessageIsPublished;
import com.curtisnewbie.module.outbox.dao.MessageEntity;
import com.curtisnewbie.module.outbox.dao.MessageMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

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
                    .messagePostProcessor(new HeaderMessagePostProcessor(msg))
                    .build());
        }
    }

    private static class HeaderMessagePostProcessor implements MessagePostProcessor {

        private final MessageEntity me;

        public HeaderMessagePostProcessor(MessageEntity me) {
            this.me = me;
        }

        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            MessageProperties prop = message.getMessageProperties();
            Map<String, Object> headers = prop.getHeaders();
            // message_id
            headers.put(MessageHeaderUtil.MESSAGE_ID_HEADER_NAME, me.getMessageId());
            return message;
        }
    }

}
