package com.armin.work;

import com.armin.constant.RabbitmqConstant;
import com.armin.utils.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

/**
 * 工作队列模型
 *       一个生产者   多个消费者
 */
public class Sender {
    public static void main(String[] args) throws Exception {
        // 获取连接
        Connection conn = ConnectionUtil.getConnection();
        // 创建通道
        Channel channel = conn.createChannel();
        // 创建队列
        channel.queueDeclare(RabbitmqConstant.QUEEN_NAME_WORK, true, false, false, null);
        // 创建消息
        String msg = "work-产品-日期: " + System.currentTimeMillis();
        // 发送消息
        /**
         * @param exchange 将消息发布到指定的交换机
         * @param routingKey 队列名称
         * @param props 消息的其他属性-路由标头等 ( MessageProperties.PERSISTENT_TEXT_PLAIN ==> 消息持久化 )
         * @param body 消息体
         */
        channel.basicPublish("", RabbitmqConstant.QUEEN_NAME_WORK, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
        // 关闭通道
        channel.close();
    }
}
