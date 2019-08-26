package com.mutil.thread.concurrent;

import com.mutil.thread.concurrent.impl.RunnableDenyException;

/**
 * 拒绝策略：
 * （用于当前queue队列中）
 * 当前线程达到上限，并且队列已满的情况下，
 *
 */
@FunctionalInterface
public interface DenyPolicy {

    void reject(Runnable runnable, ThreadPool threadPool);

    // 该拒绝策略会直接将任务丢弃
    class DiscardDenyPolicy implements DenyPolicy{
        @Override
        public void reject(Runnable runnable,ThreadPool threadPool){
            //Do Nothing
        }
    }
    // 该拒绝策略会向提交者抛出异常
    class AbortDenyPolicy implements DenyPolicy{
        @Override
        public void reject(Runnable runnable,ThreadPool threadPool){
            throw new RunnableDenyException("The runnable "+runnable+" will be abort");
        }
    }
    // 该拒绝策略会使任务在提交者所在的线程中执行任务
    class RunnerDenyPolicy implements DenyPolicy{
        @Override
        public void reject(Runnable runnable,ThreadPool threadPool){
            if(!threadPool.isShutdown()){
                runnable.run();
            }
        }
    }
}
