package cn.scut.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 使用rabbitMq
 * 1 引入ampq：RabbitAutoConfiguration 就会自动生效
 * 2 给容器中配置了
 *      RabbitTemplate AmqpAdmin achingConnectionFactory  RabbitMessagingTemplate
 *      所有的属性都是
 *      @ConfigurationProperties(prefix = "spring.rabbitmq")
 *      public class RabbitProperties
 * 3 给配置文件 配置spring.rabbitmq 消息
 * 4 @EnableRabbit：开启功能
 * 5 监听消息：使用@RabbitListener 必须使用 @EnableRabbit
 * @RabbitListener：类＋方法
 * @RabbitHandler：方法
 *   @RabbitListener标在类上表示监听那些队列，@RabbitHandler标在方法上，重载区分不同的消息
 *
 */
@EnableRedisHttpSession
@EnableFeignClients
@EnableRabbit
@SpringBootApplication
@MapperScan("cn.scut.mall.order.dao")
@EnableDiscoveryClient
public class MallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallOrderApplication.class, args);
    }

}
