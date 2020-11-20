package cn.scut.mall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 *  核心 原理
 *  1） @EnableRedisHttpSession  导入了 RedisHttpSessionConfiguration配置
 *         1 给 容器添加了一个组件
 *              SessionRepository=》》RedisIndexedSessionRepository==》redis操作session，session的
 *         2 SessionRepositoryFilter: session过滤器---》Filter：每一个请求过来 经过filter
 *                1 创建的时候，就自动从容器中获取到了SessionRepository
 *                2 每一个 请求 过来
 *
 */
@EnableRedisHttpSession //整合 redis作为 session
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class MallAuthServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallAuthServerApplication.class,args);
    }
}
