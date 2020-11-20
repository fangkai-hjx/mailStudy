package cn.scut.mall.order.config;

import cn.scut.mall.order.entity.OrderEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyMQConfig {
    //@Bean Binding Queue Exchange

    /**
     * 容器中的 Binding Queu Exchange 都会自动创建（RabbitMQ没有的情况）
     *
     * @return
     */
    @Bean
    public Queue orderDelayQueue() {
        //public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete,@Nullable Map<String, Object> arguments)
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","order-event-exchange");
        arguments.put("x-dead-letter-routing-key","order.release.order");
        arguments.put("x-message-ttl",60000);
        Queue queue = new Queue("order.delay.queue", true, false, false,arguments);
        return queue;
    }

    @Bean
    public Queue orderReleaseOrderQueue() {
        Queue queue = new Queue("order.release.order.queue", true, false, false);
        return queue;
    }
    @Bean
    public Queue orderSeckillOrderQueue() {
        Queue queue = new Queue("order.seckill.order.queue", true, false, false);
        return queue;
    }

    @Bean
    public Exchange orderEventExchange() {
        //TopicExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
        TopicExchange exchange = new TopicExchange("order-event-exchange",true,false);
        return exchange;
    }
    @Bean
    public Binding orderSeckillOrderBinding() {
        //Binding(String destination, DestinationType destinationType, String exchange, String routingKey,
        //			@Nullable Map<String, Object> arguments)
        Binding binding = new Binding("order.seckill.order.queue", Binding.DestinationType.QUEUE, "order-event-exchange", "order.seckill.order",null);
        return binding;
    }
    @Bean
    public Binding orderCreateOrderBinding() {
        //Binding(String destination, DestinationType destinationType, String exchange, String routingKey,
        //			@Nullable Map<String, Object> arguments)
        Binding binding = new Binding("order.delay.queue", Binding.DestinationType.QUEUE, "order-event-exchange", "order.create.order",null);
        return binding;
    }

    @Bean
    public Binding orderReleaseOrderBinding() {
        Binding binding = new Binding("order.release.order.queue", Binding.DestinationType.QUEUE, "order-event-exchange", "order.release.order",null);
        return binding;
    }

    /**
     * 订单关单---》库存队列
     * @return
     */
    @Bean
    public Binding orderReleaseOtherBinding() {
        Binding binding = new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE, "order-event-exchange", "order.release.other.#",null);
        return binding;
    }
}
