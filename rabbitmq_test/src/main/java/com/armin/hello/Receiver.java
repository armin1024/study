package com.armin.hello;

import com.armin.constant.RabbitmqConstant;
import com.armin.utils.ConnectionUtil;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * 接收者（消费）
 */
public class Receiver {
    public static void main(String[] args) throws Exception {
        // 创建连接
        Connection conn = ConnectionUtil.getConnection();
        // 创建通道
        Channel channel = conn.createChannel();
        // 创建一个消费者  用来消费产品
        Consumer consumer = new DefaultConsumer(channel){
            /**
             * 消费者需要手动确认，如果消费者在消费过程中出现问题而中断，则该消费为不成功，Ack不能为true
             * @param consumerTag
             * @param envelope
             * @param properties
             * @param body
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                // 问题
                // int i = 1/0;
                // 获取信封中信息
                System.out.println("交换机名称： " + envelope.getExchange());
                System.out.println("路由名称： " + envelope.getRoutingKey());
                System.out.println("消息id： " + envelope.getDeliveryTag());
                // 打印消息
                System.out.println(new String(body));
                // 手动确认(Ack)
                channel.basicAck(envelope.getDeliveryTag(), true);
            }
        };
        /**
         * @param queue 队列名称
         * @param autoAck 如果服务器应考虑一旦传递已确认的消息，则为true；否则为true。如果服务器应该期望显式确认，则返回false
         * @param callback 消费者对象的接口
         */
        // 监听队列  获取消息    队列名称                    确认消费(true 自动确认)  消费者
        channel.basicConsume(RabbitmqConstant.QUEEN_NAME_HELLO, false, consumer);
    }
}
