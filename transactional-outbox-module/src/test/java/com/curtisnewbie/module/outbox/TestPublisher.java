package com.curtisnewbie.module.outbox;

import com.curtisnewbie.common.util.JsonUtils;
import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.module.outbox.consumer.DuplicateMessageTracker;
import com.curtisnewbie.module.outbox.publisher.PublishingParam;
import com.curtisnewbie.module.outbox.publisher.TransactionalMessagePublisher;
import com.curtisnewbie.module.outbox.service.MessageOutboxService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.mockito.LatchCountDownAndCallRealMethodAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author yongjie.zhuang
 */
@Configuration
@Slf4j
@SpringBootTest(classes = {TestConfig.class, TestPublisher.class})
public class TestPublisher {

    public static final String MOCKED_LISTENER_ID = "demoBeanListener";
    public static final String TEST_EXCHANGE = "test-exchange";
    public static final String TEST_QUEUE = "test-queue";
    public static final String DEMO_BEAN_NAME = "Monkas";

    @Autowired
    private TransactionalMessagePublisher transactionalMessagePublisher;

    @Autowired
    private RabbitListenerTestHarness harness;

    @Captor
    ArgumentCaptor<Message<String>> msgCaptor;

    @Test
    public void shouldSend() throws InterruptedException, JsonProcessingException {
        // spied listener
        Listener spiedListener = harness.getSpy(MOCKED_LISTENER_ID);
        Assertions.assertNotNull(spiedListener);

        // listener should only receive one message
        LatchCountDownAndCallRealMethodAnswer answer = this.harness.getLatchAnswerFor(MOCKED_LISTENER_ID,
                1);
        Mockito.doAnswer(answer).when(spiedListener).handle(Mockito.any());

        DemoBean payload = new DemoBean(DEMO_BEAN_NAME);
        Assertions.assertDoesNotThrow(() -> {
            // should be able to send the message
            transactionalMessagePublisher.publish(PublishingParam.builder()
                    .payload(payload)
                    .exchange(TEST_EXCHANGE)
                    .routingKey(TEST_QUEUE)
                    .build());
        });

        // should receive message before timeout
        Assertions.assertTrue(answer.await(10));

        // verify that the listener is invoked
        Mockito.verify(spiedListener).handle(msgCaptor.capture());

        // transactional-outbox-module can only send String messages, these can't be automatically deserialized
        // deserialize it manually
        DemoBean capturedPayload = JsonUtils.readValueAsObject(msgCaptor.getValue().getPayload(), DemoBean.class);

        // verify that the payload received matches the one sent
        Assertions.assertTrue(capturedPayload.equals(payload));
    }

    /**
     * @author yongjie.zhuang
     */
    @Slf4j
    @Component
    public static class Listener {

        @Autowired
        private DuplicateMessageTracker duplicateMessageTracker;

        @RabbitListener(
                id = MOCKED_LISTENER_ID,
                queues = TEST_QUEUE
        )
        public void handle(Message<String> me) throws JsonProcessingException {
            if (duplicateMessageTracker.isDuplicateMessage(me)) {
                log.info("Drop duplicate message");
                return;
            }

            duplicateMessageTracker.markAsConsumed(me, new Date());
            DemoBean demoBean = JsonUtils.readValueAsObject(me.getPayload(), DemoBean.class);
            log.info("Received {}", demoBean);
        }

    }

    /**
     * @author yongjie.zhuang
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DemoBean {

        private String name;

    }

    @Bean
    public Queue queue() {
        return new Queue(TestPublisher.TEST_QUEUE, false, false, false);
    }

    @Bean
    public Exchange exchange() {
        return new DirectExchange(TestPublisher.TEST_EXCHANGE, false, false);
    }

    @Bean
    public Binding binding() {
        return new Binding(TestPublisher.TEST_QUEUE, Binding.DestinationType.QUEUE, TestPublisher.TEST_EXCHANGE,
                TestPublisher.TEST_QUEUE, null);
    }
}
