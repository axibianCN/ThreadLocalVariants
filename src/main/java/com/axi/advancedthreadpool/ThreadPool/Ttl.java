package com.axi.advancedthreadpool.ThreadPool;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class Ttl {
    public static void main(String[] args) throws InterruptedException {
        TransmittableThreadLocal<String> userId = new TransmittableThreadLocal<>();
        InheritableThreadLocal<String> inheriUserId = new InheritableThreadLocal<>();
        ThreadLocal<String> normalUserId = new ThreadLocal<>();
        ExecutorService executors = Executors.newSingleThreadExecutor();
        //创建新线程情况:ttl和inheritableThreadLocal可以传递，而普通ThreadLocal不行
        try{
            userId.set("ttl axi");
            inheriUserId.set("inheri axi");
            normalUserId.set("normal axi");
            new Thread(() -> {
                System.out.println("new Thread ttl " + userId.get());
            }).start();
            new Thread(() -> {
                System.out.println("new Thread inheri " + inheriUserId.get());
            }).start();
            new Thread(() -> {
                System.out.println("new Thread normal " + normalUserId.get());
            }).start();
            //因为是异步的，所以需要main线程等待异步线程执行完毕，才可以看到结果（但是相当于就不是异步了 而是同步）
            CompletableFuture.runAsync(() -> {
                System.out.println("completable"+userId.get());
            }, TtlExecutors.getTtlExecutorService(ForkJoinPool.commonPool()));
            Thread.sleep(1000);
        }finally {
            userId.remove();
            inheriUserId.remove();
            normalUserId.remove();
            System.out.println("after remove");
        }
        //线程池情况
        try{
            userId.set("ttl pool axi");

            normalUserId.set("normal pool axi");
            executors.execute(new Thread(() -> {
                System.out.println("new Thread normal " + normalUserId.get());
            }));
            inheriUserId.set("inherit pool axi");
            //executor会复用第一次创建的线程，导致inheri的线程变量实际是无法传递的，因为inheri只支持新创建的线程传递
            for(int i = 0 ; i < 2; i++) {
                executors.execute(new Thread(() -> {
                    System.out.println(Thread.currentThread().getName() + "new Thread inheri " + inheriUserId.get());
                }));
            }

            executors.execute(new Thread(() -> {
                System.out.println("new Thread ttl " + userId.get());
            }));
            Thread.sleep(1000);
        }finally {
            userId.remove();
            inheriUserId.remove();
            normalUserId.remove();
            executors.shutdown();
            System.out.println("after remove" + userId.get());
        }

    }
}
