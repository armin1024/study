package com.armin.fanout;

import com.armin.constant.RabbitmqConstant;
import com.armin.utils.ConnectionUtil;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * 接收者（消费）
 */
public class Receiver_1 {
    public static void main(String[] args) throws Exception {
        // 创建连接
        Connection conn = ConnectionUtil.getConnection();
        // 创建通道
        Channel channel = conn.createChannel();
        // 创建队列
        channel.queueDeclare(RabbitmqConstant.QUEEN_NAME_Q1, true, false, false, null);
        // 队列绑定交换机
        channel.queueBind(RabbitmqConstant.QUEEN_NAME_Q1, RabbitmqConstant.EXCHANGE_NAME_FANOUT, "");
        // 创建一个消费者  用来消费产品
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("1号队列");
                // 获取信封中信息
                System.out.println("交换机名称： " + envelope.getExchange());
                System.out.println("路由名称： " + envelope.getRoutingKey());
                System.out.println("消息id： " + envelope.getDeliveryTag());
                // 打印消息
                System.out.println(new String(body));
                channel.basicAck(envelope.getDeliveryTag(), true);
            }
        };
        // 监听队列  获取消息    队列名称                    确认消费(true 自动确认)  消费者
        channel.basicConsume(RabbitmqConstant.QUEEN_NAME_Q1, false, consumer);
    }
}
