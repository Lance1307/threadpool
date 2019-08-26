package com.mutil.thread.concurrent.impl;


import com.mutil.thread.concurrent.RunnableQueue;

/**
 * InternatTask：runnable的一种现实，主要用于线程池内部，该类会使用到RunnableQueue,然后不断地从queue
 * 获取某个runnable,并运行runnable的run方法
 */
public class InternalTask implements Runnable {

    private final RunnableQueue runnableQueue;

    private volatile boolean running = true;

    public InternalTask(RunnableQueue runnbaleQueue){
        this.runnableQueue = runnbaleQueue;
    }

    @Override
    public void run(){
        /**
         * 如果当前任务为running并且没有被中断，则其将不断地从队列中获取就绪runnable的任务，
         * 然后执行run方法
         */
        while(running && !Thread.currentThread().isInterrupted()){
            try {
                Runnable task = runnableQueue.take();
                task.run();
            }catch (Exception e){
                running = false;
                break;
            }
        }
    }

    public void stop(){
        this.running = false;
    }

}
