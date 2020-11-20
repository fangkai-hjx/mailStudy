package cn.scut.mall.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
//@EnableConfigurationProperties(ThreadPoolConfigProperties.class)//如果用这种方式 ThreadPoolConfigProperties就不写@Component,还有一种方式，ThreadPoolConfigProperties加了@Component，注入到容器了，直接用
public class MyThreadConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties pool){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(pool.getCoreSize(),
                pool.getMaxSize(),
                pool.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
        return threadPoolExecutor;
    }
}
