package com.axi.advancedthreadpool.ThreadPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

public class BaseThreadPool {
    private static final Logger logger = LoggerFactory.getLogger(SimpleMDC.class);
    public static final String REQ_ID = "REQ_ID";

    public static void main(String[] args) {
        MDC.put(REQ_ID, UUID.randomUUID().toString());
        logger.info("开始调用服务A，进行业务处理");
        logger.info("业务处理完毕，可以释放空间了，避免内存泄露");
        MDC.remove(REQ_ID);
        logger.info("REQ_ID 还有吗？{}", MDC.get(REQ_ID) != null);
        ThreadLocal<String> t1 = new ThreadLocal<>();
        //仅支持创建新线程使用 而不支持线程池场景
        InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();
        t1.set("main");
        inheritableThreadLocal.set("inheri main");
        new Thread(()->{
            MDC.put(REQ_ID, UUID.randomUUID().toString());
            logger.info("new Thread 开始调用服务A，进行业务处理");
            logger.info("new Thread 业务处理完毕，可以释放空间了，避免内存泄露");
            System.out.println("new Thread new thread:" + t1.get());
            System.out.println("new Thread inheri main" + inheritableThreadLocal.get());
            MDC.remove(REQ_ID);
            logger.info("new Thread REQ_ID 还有吗？{}", MDC.get(REQ_ID) != null);
        }).start();
        System.out.println(t1.get());
        System.out.println(inheritableThreadLocal.get());

    }

}



