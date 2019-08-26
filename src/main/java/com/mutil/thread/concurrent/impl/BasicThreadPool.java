package com.mutil.thread.concurrent.impl;

import com.mutil.thread.concurrent.DenyPolicy;
import com.mutil.thread.concurrent.RunnableQueue;
import com.mutil.thread.concurrent.ThreadFactory;
import com.mutil.thread.concurrent.ThreadPool;
import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;
import java.util.Queue;

/**
 *   一个线程池处理控制参数外，还要有活动线程，其中Queue主要用来存放活动的线程。
 *   BasicThreadPool同时也是Thread子类，在初始化的时候启动，在keepalive时间间隔到了之后再自动维护活动线程数量
 *   （采用extends的方式其实不是一种好方法，会暴露THread的方法，可以采用组合的形式，这里先不修改了）
 */
public class BasicThreadPool extends Thread implements ThreadPool {

    // 初始化线程数量
    private final int initSize;

    // 最大线程数量
    private final int maxSize ;

    // 核心线程数量 ，当线程平稳的时候的核心数量啊
    private final int coreSize ;

    // 活跃线程数量
    private int activeCount ;

    // 创建线程所需要的线程工厂
    private final ThreadFactory threadFactory ;

    // 任务队列
    private final RunnableQueue runnableQueue ;

    // 是否关闭
    private volatile boolean isShutdown = false ;

    // 工作线程队列
    private final Queue<ThreadTask> threadQueue = new ArrayDeque<>() ;

    // 默认线程工厂
    private final  static ThreadFactory DEFAULT_THREAD_FACTORY = new DefaultThreadFactory();

    // 默认拒绝策略
    private final static  DenyPolicy DEFAULT_DENY_POLICY = new DenyPolicy.DiscardDenyPolicy();

    // 线程池的检查更新线程的时间间隔
    private final long keepAliveTime ;

    // 时间处理对象
    private final TimeUnit timeUnit ;

    public BasicThreadPool(int initSize, int maxSize , int coreSize , int queueSize) {

        this(initSize,maxSize,coreSize,DEFAULT_THREAD_FACTORY,
                queueSize,DEFAULT_DENY_POLICY,10,TimeUnit.SECONDS) ;
    }

    public BasicThreadPool(int initSize,
                           int maxSize ,
                           int coreSize ,
                           ThreadFactory threadFactory,
                           int queueSize,
                           DenyPolicy denyPoliy,
                           long keepAliveTime ,
                           TimeUnit timeUnit
    ) {

        this.initSize = initSize ;
        this.maxSize = maxSize ;
        this.coreSize = coreSize ;
        this.threadFactory = threadFactory ;
        this.runnableQueue = new LinkedRunnableQueue(queueSize, denyPoliy, this) ;
        this.keepAliveTime = keepAliveTime ;
        this.timeUnit = timeUnit ;
        this.init();
    }
    /**
     * 初始化时，新创建initsize个线程
     */
    private void init(){
        start();
        for (int i=0;i<initSize;i++){
            newThread();
        }
    }

    /**
     * 创建新的线程，并记录活跃线程数量
     * 启动该线程
     */
    private void newThread() {
        //创建任务线程，并且启动
        InternalTask internalTask = new InternalTask(runnableQueue);
        Thread thread = this.threadFactory.createThread(internalTask);
        ThreadTask threadTask = new ThreadTask(thread,internalTask);
        threadQueue.offer(threadTask);
        this.activeCount++;
        thread.start();
    }
    private void removeThread(){
        // 从线程池中移除某个线程
        ThreadTask threadTask = threadQueue.remove();
        threadTask.internalTask.stop();
        this.activeCount--;
    }

    /**
     * 提交任务
     * 只是将Runnable插入runnableQueue中
     * @param runnable
     */
    @Override
    public void execute(Runnable runnable) {
        //线程是否关闭
        if(this.isShutdown){
            throw new IllegalMonitorStateException("The thread Pool is destroy.");
        }else{
            // 提交任务，往任务队列加入就绪的线程
            this.runnableQueue.offer(runnable);
        }
    }

    @Override
    public void run(){
        // run方法继承自Thread,主要用于维护线程数量，比如扩容，回收等工作
        while(!isShutdown && !isInterrupted()){
            try{
                timeUnit.sleep(keepAliveTime);
            }catch (InterruptedException e){
                isShutdown = true;
                break;
            }


            synchronized (this){
                if (isShutdown) break;

                // 如果当前队列中有未处理的任务，并且activeCount < coresize则需要扩展
                if(runnableQueue.size() > 0 && activeCount < coreSize){
                    for (int i=initSize ; i< coreSize; i++){
                        newThread();
                    }
                    // 防止线程扩容达到maxSize
                    continue;
                }
                // 如果任务队列中没有任务，则需要回收线程，回收值coreSize即可
                if(runnableQueue.size() == 0 && activeCount > coreSize){
                    for (int i = coreSize; i < activeCount; i++){
                        removeThread();
                    }
                }
            }
        }
    }
    @Override
    public void shutdown() {
        synchronized (this){
            if(this.isShutdown) return ;
            isShutdown = false;
            threadQueue.forEach(threadTask -> {
                threadTask.internalTask.stop();
                threadTask.thread.interrupt();
            });
        }
        this.interrupt();
    }

    @Override
    public int getInitSize() {
        if(this.isShutdown){
            throw new IllegalMonitorStateException("The thread Pool is destroy.");
        }
        return this.initSize;
    }

    @Override
    public int getMaxSize() {
        if(this.isShutdown){
            throw new IllegalMonitorStateException("The thread Pool is destroy.");
        }
        return this.maxSize;
    }

    @Override
    public int getQueueSize() {
        if(this.isShutdown){
            throw new IllegalMonitorStateException("The thread Pool is destroy.");
        }
        return runnableQueue.size();
    }

    @Override
    public int getActiveCount() {
        // 获取活跃线程数，需要进行同步操作
        synchronized (this) {
            return this.activeCount;
        }
    }

    @Override
    public boolean isShutdown() {
        return this.isShutdown;
    }
}
