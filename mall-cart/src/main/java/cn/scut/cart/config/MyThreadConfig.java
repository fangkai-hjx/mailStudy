package cn.scut.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class MyThreadConfig {
    /**
     * public ThreadPoolExecutor(int corePoolSize,
     * int maximumPoolSize,
     * long keepAliveTime,
     * TimeUnit unit,
     * BlockingQueue<Runnable> workQueue) {
     * this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
     * Executors.defaultThreadFactory(), defaultHandler);
     * }
     *
     * @param pool
     * @return
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties pool) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(pool.getCoreSize(),
                pool.getMaxSize(),
                pool.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<> (100000), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
        return threadPoolExecutor;
    }
}
