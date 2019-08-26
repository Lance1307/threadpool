package com.mutil.thread.concurrent;
/**
 * ThreadPool
 * 定义一个线程池中该具备的操作和方法
 */
public interface ThreadPool {

    /** 提交任务到线程池*/
    void execute(Runnable runnable);

    /** 关闭线s程池*/
    void shutdown();

    /** 获取线程池初始化大小*/
    int getInitSize();

    /** 获取线程池最大线程数量*/
    int getMaxSize();

    /** 核心线程数量*/
    int getCoreSize();
    /** 获取线程池用于缓存任务队列的大小*/
    int getQueueSize();

    /** 获取线程池中活跃的线程数量*/
    int getActiveCount();

    /** 查看线程池是否已经被shutDown*/
    boolean isShutdown();


}
