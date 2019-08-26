package com.mutil.thread.concurrent;


/**
 * 提供创建线程的接口，以便个性化定制线程（Thread）,比如：该线程被加入到哪个Group中，优先级，线程名字已经是否守护线程等。
 */
@FunctionalInterface
public interface ThreadFactory {

    /**
     * 创建线程
     * @param runnable
     * @return
     */
    Thread createThread(Runnable runnable );
}
