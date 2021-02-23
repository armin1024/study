package com.armin.send;

import com.armin.AppRun;
import com.armin.constant.RabbitMqConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 回调主要就是发送失败的时候 业务处理
 *   1.将错误信息保存到日志中
 *   2.如果是发送到队列失败   此时可以选择重发
 */
@SpringBootTest(classes = AppRun.class)
@RunWith(SpringRunner.class)
public class Sender {
    @Autowired
    private RabbitTemplate template;
    // 重发消息次数
    int num = 3;

    @Test
    public void testSend() {
        // 消息发送到交换机的回调
        template.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (correlationData != null) {
                    System.out.println("[ConfirmCallback-CorrelationData]: " + correlationData.toString());
                }
                // ack = true 消息发送到队列成功，否则失败
                System.err.println("[ConfirmCallback-MsgExchangeToQueue]: " + ack);
                System.err.println("[ConfirmCallback-Reason]: " + cause);
            }
        });
        // 消息发送到队列失败的回调
        template.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                // 消息体
                System.err.println("[ReturnCallback-Message]: " + message.getBody());
                // 消息状态
                System.err.println("[ReturnCallback-ReplyCode]: " + replyCode);
                // 消息状态解释
                System.err.println("[ReturnCallback-ReplyText]: " + replyText);
                // 对应发送消息的 exchange
                System.err.println("[ReturnCallback-Exchange]: " + exchange);
                // 对应发送消息的 routingKey
                System.err.println("[ReturnCallback-RoutingKey]: " + routingKey);
                // 判断重发条件
                if (replyCode==0 && num>0) {
                    num--;
                    // 消息重发
                    template.convertAndSend(RabbitMqConstant.EXCHANGE_NAME_LOG, "log.first", "[Msg]: SUMMER IS COMING!");
                }
            }
        });
        // 发送消息(交换机除指定名称外还有默认名称为""【空串】)
        template.convertAndSend(RabbitMqConstant.EXCHANGE_NAME_LOG, "log.first", "[Msg]: SUMMER IS COMING!");
    }
}
