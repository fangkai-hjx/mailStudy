package cn.scut.mall.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 1 整合sentinel
 *      1）导入依赖 spring-cloud-starter-alibaba-sentinel
 *      2)下载sentinel的控制台
 *      3）配置 sentinel 控制台 配置信息
 *      4）在控制台调整参数。【默认所有的流控设置保存在内存中，每次重启就没了】
 * 2 每一个微服务都导入actuator：并配合management.endpoints.web.exposure.include=*
 * 3 自定义sentinel流控返回数据
 * 4 使用sentinel来保护feign远程调用：熔断
 *      1）调用方的熔断保护，feign.sentinel.enabled=true
 *      1）调用方手动指定远程服务的降级策略。
 *      3) 超大浏览到时候，必须牺牲一些远程服务，在服务的提供方，触发我们熔断回调方法
 *      4）提供方是在运行，但是不允许自己的业务逻辑，返回的是默认的降级数据（限流的数据 ）
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients
@EnableRedisHttpSession
public class SeckillApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class,args);
    }
}
