package com.mutil.thread.concurrent;

/**
 * RunnbaleQueue:主要是存放就绪（Runable）的任务，而该Runable就是一个阻塞队列（BlockedQueue）,并且有上限（limit）的限制
 *
 *   任务队列：主要用于缓存提交到线程池中任务
 */
public interface RunnableQueue {

    /**
     * 当有新的任务进来是，首先会offer到队列中
     * (主要就是将任务提交的队列中)
     */
    void offer(Runnable runnable);

    /**
     * 工作线程通过take方法获取Runable
     * （主要是从队列中获取相应的任务）
     */
    Runnable take() throws InterruptedException;

    /**
     * 获取任务队列中线程数量
     * （获取当前队列中Runnable就绪的任务数量）
     */
    int size();
}
