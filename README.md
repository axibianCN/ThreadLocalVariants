# ThreadLocalVariants
ThreadLocal、InheritableThreadLocal、TransmittableThreadLocal学习记录
## ThreadLocal、InheritableThreadLocal、TransmittableThreadLocal是什么？
用于在线程之间实现变量隔离的一种方法。
在JAVA中，ThreadLocal变量会与当前对其set值的线程绑定，例如在main线程中，调用某个ThreadLocal变量的.set("value")，那么该变量就和main绑定了。每个线程包括main都有一个ThreadlocalMap的内部变量，该变量即存储Threadlocal。其中Threadlocal对象本身为key，"value"值为value。所以一个线程可以绑定若干Threadlocal变量。
## 原生ThreadLocal的缺点
无法在线程之间传播ThreadLocal的值，例如链路ID或一些全局参数。原因：每个TL对象和线程绑定，切换线程后，则无法访问到。
## 解决方式
1. InheritableThreadLocal：在创建新线程时，将父线程的threadlocalmap进行复制，复制到子线程中，这样子线程就可以访问了，底层是通过childValue实现，但是该方法只能在创建新线程时生效，如果是线程池的线程复用场景，无法生效。
2. TransmittableThreadLocal：阿里巴巴开源的解决方案。即可以兼容InheritableThreadLocal，也可以在线程池复用场景下生效。
实现原理：capture/replay/restore为核心流程：
a. capture：捕获当前线程的ThreadlocalMap中所有变量 b. replay：将捕获的当前线程变量重放到执行线程（子线程），子线程调用run方法前需要保存当前上下文 c. restore：子线程业务结束后，将之前保存的上下文恢复，因为子线程执行过程中可能会改变其值。
## src/main/java/com/axi/advancedthreadpool/ThreadPool/Ttl.java 查看三者区别和具体的示例
