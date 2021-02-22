package com.armin.receive;

import com.armin.constant.RabbitMqConstant;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class Receiver {
    // 监听队列获取消息
    @RabbitListener(queues = RabbitMqConstant.QUEUE_NAME_LOG)
    public void receive(String msgStr, Message msg, Channel channel) throws IOException, TimeoutException {
        // 打印消息
        System.err.println("[RabbitListener-StringMessage]: " + msgStr);
        System.err.println("[RabbitListener-ObjectMessage]: " + new String(msg.getBody()));
        // 手动签收
        channel.basicAck(msg.getMessageProperties().getDeliveryTag(), true);
//        channel.close();
    }
}
