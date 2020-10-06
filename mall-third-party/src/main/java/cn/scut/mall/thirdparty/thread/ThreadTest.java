package cn.scut.mall.thirdparty.thread;

import java.util.concurrent.*;

/**
 * 初始化线程的四种方式：
 * 1.继承Thread
 * 2.实现Runnable接口
 * 3.实现Callable接口 + FutureTask
 * 4.线程池
 */
public class ThreadTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main....start");
//        new Thread01().start();

//        new Thread().start();

//        Runnable01 runnable01 = new Runnable01();
//        new Thread(runnable01).start();

//        FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
//        new Thread(futureTask).start();
//        Integer integer = futureTask.get();//等待异步任务执行完，获取返回结果--------------线程会阻塞在这里
//        System.out.println(integer);

//        FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
//        Thread thread = new Thread(futureTask,"AAA");
//        thread.start();
//
//        Integer result01 = 100;
//        Integer integer = futureTask.get();//这个 最好 放在 最后，因为 会导致阻塞
//        System.out.println(result01+ integer);
//        System.out.println("main....end");
        //当前系统中池只有一两个，每个异步任务，提交给线程池让他自己去执行
//        ExecutorService service = Executors.newFixedThreadPool();
//        ThreadPoolExecutor  threadPoolExecutor = new ThreadPoolExecutor()
//        service.execute(new Runnable01());
//        System.out.println("main....end");
    }

    public static class Thread01 extends Thread {
        @Override
        public void run() {
            int i = 10 / 2;
            System.out.println("当前线程" + Thread.currentThread().getId());
            System.out.println("运行结果" + i);
        }
    }
    public static class Runnable01 implements Runnable{

        @Override
        public void run() {
            int i = 10 / 2;
            System.out.println("当前线程" + Thread.currentThread().getId());
            System.out.println("运行结果" + i);
        }
    }
    public static class Callable01 implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {
            int i = 10 / 2;
            System.out.println("当前线程" + Thread.currentThread().getId());
            System.out.println("运行结果" + i);
            return i;
        }
    }
}
