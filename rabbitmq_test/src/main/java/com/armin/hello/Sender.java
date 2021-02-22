package com.armin.hello;

import com.armin.constant.RabbitmqConstant;
import com.armin.utils.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

/**
 * 发送者（生产） ======  先有生产者再有消费者（消费者依赖生产者中创建的队列）
 *   基本模型
 *      一个生产者   对应一个消费者
 *      步骤：
 *        创建连接
 *        建立通道
 *        创建交换机
 *        创建队列
 *        准备消息
 *        发送消息
 */
public class Sender {
    public static void main(String[] args) throws Exception {
        // 获取连接
        Connection conn = ConnectionUtil.getConnection();
        // 创建通道
        Channel channel = conn.createChannel();
        // 创建队列
        /**
         *  @param queue 队列名称
         *  @param durable 如果我们声明一个持久队列，则为true（该队列将在服务器重启后保留下来）
         *  @param exclusive 如果我们声明一个独占队列，则为true（仅限此连接）
         *  @param autoDelete 如果我们声明一个自动删除队列，则为true（服务器将在不再使用它时将其删除）
         *  @param arguments 队列的其他属性（构造参数）
         */
        channel.queueDeclare(RabbitmqConstant.QUEEN_NAME_HELLO, true, false, false, null);
        // 创建消息
        String msg = "hello-产品-日期: " + System.currentTimeMillis();
        // 发送消息
        /**
         * @param exchange 将消息发布到指定的交换机
         * @param routingKey 队列名称
         * @param props 消息的其他属性-路由标头等 ( MessageProperties.PERSISTENT_TEXT_PLAIN ==> 消息持久化 )
         * @param body 消息体
         */
        channel.basicPublish("", RabbitmqConstant.QUEEN_NAME_HELLO, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
        // 关闭通道
        channel.close();
    }
}
