package com.armin.config;

import com.armin.constant.RabbitMqConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMq 配置类
 */
@Configuration
public class MQConfig {
    // 创建交换机
    @Bean
    public Exchange createExchange() {
        return ExchangeBuilder.topicExchange(RabbitMqConstant.EXCHANGE_NAME_LOG).build();
    }
    // 创建队列
    @Bean
    public Queue createQueue() {
        return new Queue(RabbitMqConstant.QUEUE_NAME_LOG, true);
    }
    // 队列绑定交换机
    @Bean
    public Binding queueBindingExchange() {
        return BindingBuilder.bind(createQueue()).to(createExchange()).with("log.#").noargs();
    }
}
