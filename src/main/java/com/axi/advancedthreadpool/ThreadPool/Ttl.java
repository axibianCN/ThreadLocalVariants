package com.axi.advancedthreadpool.ThreadPool;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class Ttl {
    public static void main(String[] args) throws InterruptedException {
        TransmittableThreadLocal<String> userId = new TransmittableThreadLocal<>();
        try{
            userId.set("axi");
            new Thread(() -> {
                System.out.println("new Thread" + userId.get());
            }).start();
            //因为是异步的，所以需要main线程等待异步线程执行完毕，才可以看到结果（但是相当于就不是异步了 而是同步）
            CompletableFuture.runAsync(() -> {
                System.out.println("completable"+userId.get());
            }, TtlExecutors.getTtlExecutorService(ForkJoinPool.commonPool()));
            Thread.sleep(1000);
        }finally {
            userId.remove();
            System.out.println("after remove" + userId.get());
        }

    }
}
