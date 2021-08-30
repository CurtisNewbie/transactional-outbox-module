package com.curtisnewbie.module.outbox.publisher;

import com.curtisnewbie.common.util.JsonUtils;
import com.curtisnewbie.module.outbox.common.MessageIsPublished;
import com.curtisnewbie.module.outbox.dao.MessageEntity;
import com.curtisnewbie.module.outbox.dao.MessageMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

/**
 * @author yongjie.zhuang
 */
@Service
@Transactional
public class TransactionalMessagingPublisherImpl implements TransactionalMessagePublisher {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public void publish(@NotNull @Valid PublishingParam param) {
        try {
            messageMapper.insert(MessageEntity.builder()
                    .messageId(UUID.randomUUID().toString())
                    .payloadTypeInfer(getTypeHint(param.getPayload()))
                    .payload(JsonUtils.writeValueAsString(param.getPayload()))
                    .routingKey(param.getRoutingKey())
                    .exchange(param.getExchange())
                    .isPublished(MessageIsPublished.NO.getValue())
                    .createTime(new Date())
                    .build());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to serialise payload object to json string");
        }
    }

    // for using Jackson message converter
    private String getTypeHint(Object o) {
        return JsonUtils.constructType(o.getClass()).getRawClass().getName();
    }
}
