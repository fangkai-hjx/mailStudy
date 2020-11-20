package cn.scut.mall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MyRabbitConfig {
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 使用JSON序列化机制，进行消息转化
     *
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制RabbitTemplate
     * 1 服务器收到消息就回调
     *      1 spring.rabbitmq.publisher-confirm-type=correlated
     *      2 设置确入回调 ConfirmCallback
     * 2 消息正确抵达队列进行回调
     *      spring.rabbitmq.publisher-returns=true
     *      spring.rabbitmq.template.mandatory=true :只要抵达队列，以异步发送优先回调我们这个returnconfirm
     * 3 消费端确入（保证每个消息被正确消费，此时才可以从broker删除这个消息）
     *      1 默认是自动确入的，只要消息接收到，客户端自动确认，服务端就会移除这个消息
     */
    @PostConstruct//在MyRabbitConfig创建完成后调用
    public void initRabbitTemplate() {
        //设置确入回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * @param correlationData 当前消息的唯一关联数据（这个是消息的唯一id）
             * @param ack 消息是否成功收到
             * @param cause 失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("confirm:" + correlationData + ":ack:" + ack + ":cause:" + cause);
            }
        });
        //设置消息抵达队列的消息回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 只要消息没有投递给指定的队列，就触发这个失败回调
             * @param message  投递失败的消息详细信息
             * @param replyCode 回复的状态码
             * @param replyText 回复的文本内容
             * @param exchange 当时这个消息发送哪个交换机
             * @param routingKey 当时这个消息用哪个路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("Fail Message:" + message + ":replyCode:" + replyCode + ":replyText:" + replyText+":routingKey"+routingKey);
            }
        });
    }

}
