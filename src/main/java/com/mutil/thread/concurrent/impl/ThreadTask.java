package com.mutil.thread.concurrent.impl;

/**
 * 组合类
 * InternalTask
 * Thread
 */
public class ThreadTask {

    public InternalTask internalTask;

    public Thread thread;

    public ThreadTask(Thread thread, InternalTask internalTask) {
        this.thread = thread;
        this.internalTask = internalTask;
    }

}
