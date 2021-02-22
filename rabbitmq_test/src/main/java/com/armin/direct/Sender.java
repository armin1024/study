package com.armin.direct;

import com.armin.constant.RabbitmqConstant;
import com.armin.utils.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

/**
 * 订阅模型-Direct
 */
public class Sender {
    public static void main(String[] args) throws Exception {
        // 获取连接
        Connection conn = ConnectionUtil.getConnection();
        // 创建通道
        Channel channel = conn.createChannel();
        // 创建交换机(durable持久化)
        channel.exchangeDeclare(RabbitmqConstant.EXCHANGE_NAME_DIRECT, "direct", true);
        // 创建消息
        String msg = "direct-产品-日期: " + System.currentTimeMillis();
        // 发送消息
        /**
         * @param exchange 将消息发布到指定的交换机
         * @param routingKey 队列名称
         * @param props 消息的其他属性-路由标头等 ( MessageProperties.PERSISTENT_TEXT_PLAIN ==> 消息持久化 )
         * @param body 消息体
         */
        channel.basicPublish(RabbitmqConstant.EXCHANGE_NAME_DIRECT , "armin", MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
        // 关闭通道
        channel.close();
    }
}
