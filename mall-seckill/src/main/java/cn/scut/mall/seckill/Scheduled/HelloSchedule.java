package cn.scut.mall.seckill.Scheduled;

import com.mysql.cj.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务： TaskSchedulingAutoConfiguration
 *      1 @EnableScheduling 开启定时任务
 *      2 @Scheduled 开启一个定时任务
 *      spirng 整合的定时任务不是quart
 * 异步任务  TaskExecutionAutoConfiguration
 *      1 @EnableAsync 开启异步任务功能
 *      2 @Async 给希望异步执行的方法标注
 */
@Component
public class HelloSchedule {
    /**
     * 注意 corn 和 quart 有点不一样
     * 1 spring中的 6 位 组成 不允许第七位的年
     * 2 在 周几的位置， 1-7代表周一到周日： MON-SUN
     * 3 定时任务不应该阻塞 默认是阻塞的
     *      1） 可以 让业务运行以 异步的 方式，自己提交到线程池
     *        CompletableFuture.runAsync(()->{
     *             xxxxService.hello();
     *         },executor);
     *      2) 支持定时任务线程池
     *      spring.task.scheduling.pool.size=5
     *      3) 让定时任务 异步执行
     *          异步任务 默认 配置 TaskExecutionAutoConfiguration
     *      解决：使用异步+定时任务 来完成 定时任务不阻塞的功能
     */
//    @Async
//    @Scheduled(cron = "* * * ? * 3")
//    public void hello() throws InterruptedException {
//        TimeUnit.SECONDS.sleep(5);
//        log.info("hello...");
//    }
}
