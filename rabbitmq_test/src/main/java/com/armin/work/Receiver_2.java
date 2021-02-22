package com.armin.work;

import com.armin.constant.RabbitmqConstant;
import com.armin.utils.ConnectionUtil;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * 接收者（消费）
 */
public class Receiver_2 {
    public static void main(String[] args) throws Exception {
        // 创建连接
        Connection conn = ConnectionUtil.getConnection();
        // 创建通道
        Channel channel = conn.createChannel();
        //设置同时只能处理一个消息
        channel.basicQos(1);
        // 创建一个消费者  用来消费产品
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                // 问题
                // int i = 1/0;
                System.out.println("2号消费者");
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
        // 监听队列  获取消息    队列名称                    确认消费(true 自动确认)  消费者
        channel.basicConsume(RabbitmqConstant.QUEEN_NAME_WORK, false, consumer);
    }
}
