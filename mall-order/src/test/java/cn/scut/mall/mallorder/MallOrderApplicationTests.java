package cn.scut.mall.mallorder;

import cn.scut.mall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class MallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 1 如何 传接 Exchange ，Queue ，Binding
     * 1）使用AmqpAdmin进行创建
     * 2 如果 收发消息
     */
    @Test
    void createExchange() {
        //public DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments) {
        DirectExchange directExchange = new DirectExchange("hello-java", true, false);
        amqpAdmin.declareExchange(directExchange);
        log.info("交换机 创建完成！");
    }

    @Test
    void createQueue() {
        Queue queue = new Queue("hello-java", true, false, false);
        amqpAdmin.declareQueue(queue);
        log.info("队列 创建完成！");
    }

    @Test
    void createBinding() {
        //public Binding(
        // String destination【目的地】,
        // DestinationType【目的地类型】
        // String exchange【交换机】,
        // String routingKey,【路由键】
        //将exchange指定的交换机 和 destination 进行绑定
        Binding binding = new Binding("hello-java", Binding.DestinationType.QUEUE, "hello-java", "hello.java", null);
        amqpAdmin.declareBinding(binding);
        log.info("绑定 创建完成！");
    }

    @Test
    void sendMessage() {
        // 如果发送的消息是个对象，必须使用序列化机制，将对象写出去。对象必须实现Serializable
        //2 发送的对象类型消息，可以也可使使用JSON
        for (int i = 0; i < 100; i++) {
            OrderReturnReasonEntity entity = new OrderReturnReasonEntity();
            entity.setId(1L);
            entity.setCreateTime(new Date());
            entity.setName("方凯" + i);
            entity.setSort(i);
            rabbitTemplate.convertAndSend("hello-java", "hello.java", entity,new CorrelationData(UUID.randomUUID().toString()));
        }
        log.info("消息发送完成");
    }
}
