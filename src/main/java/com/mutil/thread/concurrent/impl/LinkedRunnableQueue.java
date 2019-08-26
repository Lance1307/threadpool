package com.mutil.thread.concurrent.impl;

import com.mutil.thread.concurrent.DenyPolicy;
import com.mutil.thread.concurrent.RunnableQueue;
import com.mutil.thread.concurrent.ThreadPool;

import java.util.LinkedList;

/**
 * 对线程池进行详细的实现。
 * LinkedRunnableQueue:几个重要的属性
 * limit：Runnable的上限。当提交的Runnable的数量达到limit上限时，则会调用DenyPolicy的reject方法；
 * runnableList：是一个双向循环列表，用于存放Runnable任务
 *
 *
 */
public class LinkedRunnableQueue implements RunnableQueue {

    // 任务队列的最大容量，在构造时传入
    private final int limit;

    // 若任务队列中的任务已经满了，则需要执行拒绝策略
    private final DenyPolicy denyPolicy;

    // 存放任务的队列
    private final LinkedList<Runnable> runnableList = new LinkedList<>();

    private final ThreadPool threadPool;

    public LinkedRunnableQueue(int limit, DenyPolicy denyPolicy, ThreadPool threadPool) {
        this.limit = limit;
        this.denyPolicy = denyPolicy;
        this.threadPool = threadPool;
    }

    /**
     * offer方法是一个同步方法，如果队列数量达到上限，则会执行拒绝策略，否则会将runnable存放到队列中，
     * 同时唤醒take任务的线程：
     * @param runnable
     */
    @Override
    public void offer(Runnable runnable) {
        synchronized (runnable){
            if(runnableList .size() >= limit){
                // 无法容纳新的任务时执行拒绝策略
                denyPolicy.reject(runnable,threadPool);
            }else{
                // 将任务加入到队列中，并且唤醒阻塞中的检查
                runnableList.add(runnable);
                runnableList.notifyAll();
            }
        }
    }

    /**
     * task方法也是同步方法，线程不断从队列中获取就绪任务，当队列为null时候工作线程会陷入阻塞，有可能在阻塞的过程
     * 中被唤醒，为了传递中年短信息需要在catch块中将异常抛出一通知上游InternalTask.
     * @return
     */
    @Override
    public Runnable take() throws InterruptedException {
        synchronized (runnableList){
            while(runnableList.isEmpty()){
                try{
                    // 如果任务队列中没有可执行的任务，则当前线程就会挂起，
                    // 进入runableList关联的monitor waitset 中等待唤醒（新的任务加入）
                    runnableList.wait();
                }catch(InterruptedException e){
                    // 被中断时需要将该异常抛出
                    throw e;
                }
            }
        }
        // 从任务队列头部移除一个任务
        return runnableList.removeFirst();
    }

    @Override
    public int size() {
        synchronized (runnableList){
            // 返回当前人恩物队列中的任务数量
            return runnableList.size();
        }
    }
}
