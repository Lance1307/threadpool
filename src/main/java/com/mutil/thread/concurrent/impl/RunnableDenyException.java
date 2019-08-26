package com.mutil.thread.concurrent.impl;

/**
 *  主要用于通知任务提交者，任务队列已经无法在接收新的任务
 */
public class RunnableDenyException extends RuntimeException {
    public RunnableDenyException(String msg){
        super(msg);
    }
}
