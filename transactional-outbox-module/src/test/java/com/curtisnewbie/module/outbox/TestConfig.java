package com.curtisnewbie.module.outbox;

import com.curtisnewbie.module.messaging.config.SimpleConnectionFactoryBeanFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * @author yongjie.zhuang
 */
@SpringBootApplication(scanBasePackages = "com.curtisnewbie")
@RabbitListenerTest
@PropertySource("classpath:application.properties")
public class TestConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory containerFactoryBean(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(connectionFactory);
        return f;
    }

    @Bean
    public ConnectionFactory connectionFactory(Environment environment) {
        return SimpleConnectionFactoryBeanFactory.createByProperties(environment);
    }

}
