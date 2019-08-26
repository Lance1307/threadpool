package com.mutil.thread.test;

import com.mutil.thread.concurrent.ThreadPool;
import com.mutil.thread.concurrent.impl.BasicThreadPool;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {

    @Test
    public void test() throws InterruptedException {
        /**
         * 定义一个线程池
         * 初始化线程数为：2
         * 核心线程数：4,
         * 最大线程数：6
         * 任务队列最多运行1000个任务
          */
        final ThreadPool threadPool = new BasicThreadPool(2,6,4,1000);
        // 定义20个任务并且提交给线程池
        for(int i= 0 ;i < 20 ; i++){
            threadPool.execute(()->{
                try{
                    TimeUnit.SECONDS.sleep(10);
                    System.out.println(Thread.currentThread().getName()+" is running and done;");
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            });
            for (;;){
                System.out.println("getActiveCount:"+threadPool.getActiveCount());
                System.out.println("getQueueSize:"+threadPool.getQueueSize());
                System.out.println("getCoreSize:"+threadPool.getCoreSize());
                System.out.println("getMaxSize:"+threadPool.getMaxSize());
                System.out.println("================================================================");
                TimeUnit.SECONDS.sleep(5);

            }
        }

    }
}
