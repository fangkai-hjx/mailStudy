package cn.scut.mall.ware.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyRabbitConfig {

    /**
     * 使用JSON序列化机制，进行消息转化
     *
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange stockEventExchange(){
        //(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments) {
        //		super(name, durable, autoDelete, arguments);
        return new TopicExchange("stock-event-exchange",true,false,null);
    }

    @Bean
    public Queue stockReleaseStockQueue(){
        //	public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete,
        //			@Nullable Map<String, Object> arguments) {
        return new Queue("stock.release.stock.queue",true,false,false);
    }

    @Bean
    public Queue stockDelayQueue(){
        //	public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete,
        //			@Nullable Map<String, Object> arguments) {
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","stock-event-exchange");
        arguments.put("x-dead-letter-routing-key","stock.release");
        arguments.put("x-message-ttl",120000);
        return new Queue("stock.delay.queue",true,false,false,arguments);
    }

    @Bean
    public Binding stockLockedBinding(){
        //	public Binding(String destination, DestinationType destinationType, String exchange, String routingKey,
        //			@Nullable Map<String, Object> arguments) {
        return new Binding("stock.delay.queue", Binding.DestinationType.QUEUE,"stock-event-exchange","stock.locked",null);
    }
    @Bean
    public Binding stockReleaseBinding(){
        //	public Binding(String destination, DestinationType destinationType, String exchange, String routingKey,
        //			@Nullable Map<String, Object> arguments) {
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,"stock-event-exchange","stock.release.#",null);
    }

}
