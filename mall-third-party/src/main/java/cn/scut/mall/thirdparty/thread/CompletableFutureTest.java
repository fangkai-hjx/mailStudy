package cn.scut.mall.thirdparty.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 1 创建异步对象
 *
 */
public class CompletableFutureTest {

    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main....start");
//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果" + i);
//        }, executor);
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果" + i);
//            return i;
//        }, executor).whenCompleteAsync((result,throwable)->{
//            //虽然能得到异常信息，但是没法修改返回结构（类似监听器，只能感知，不能修改）
//            System.out.println("异步任务成功完成了。。。。结果是："+result+"；异常是："+throwable);
//        }).exceptionally(throwable -> {
//            //可以 感知异常，同时返回默认值
//            return 10;
//        });

//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("运行结果" + i);
//            return i;
//        }, executor).handle((result,thrower)->{
//            if(result != null){
//                return result*2;
//            }
//            if(thrower!=null){
//                return 0;
//            }
//            return 0;
//        });
        /**
         * 线程串行话：
         *      1）thenRun：不能获取上一部的执行结果
         *      .thenRunAsync(()->{
         *             System.out.println("任务二启动了");
         *         },executor);
         *      2) thenAcceptAsync;能接收上一部的结果，但无返回值
         *      3)
         */
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("运行结果" + i);
//            return i;
//        }, executor).thenApplyAsync(result -> {
//            System.out.println("任务二启动了.." + result);
//            return 10;
//        }, executor);
//        Integer integer = future.get();
//        System.out.println("最后执行的结果："+integer);
//        System.out.println("main....end");

        CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的图片信息");
            return "hello.jpg";
        }, executor);
        CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的属性信息");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "黑色 256G";
        }, executor);
        CompletableFuture<Object> future3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的介绍");
            return "华为";
        }, executor);
        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(future1, future2, future3);
        System.out.println(anyOf.get());
        System.out.println("main....end");
    }
}
