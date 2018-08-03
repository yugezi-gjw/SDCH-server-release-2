package com.varian.oiscn.core.hipaa.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 3/2/2018
 * @Modified By:
 */
@Slf4j
public class AuditLogBaseThreadFactory {

    private ThreadGroup threadGroup;
    private String threadPoolName;
    private AtomicInteger count = new AtomicInteger(0);

    public AuditLogBaseThreadFactory(String threadPoolName) {
        this.threadPoolName = threadPoolName;
        this.threadGroup = new ThreadGroup("audit log " + threadPoolName + " group ");
    }

    public Thread newThread(Runnable runnable) {
        return new Thread(threadGroup, runnable, threadPoolName + "-" + count.getAndIncrement());
    }

//    public Thread newThread(String namePrefix, Runnable runnable) {
//        return new Thread(threadGroup, runnable, namePrefix + "-" + count.getAndIncrement());
//    }
//
//    public ThreadGroup getThreadGroup() {
//        return threadGroup;
//    }
//
//    public List<Thread> getThreads() {
//        Thread[] threads = new Thread[getThreadGroup().activeCount()];
//        getThreadGroup().enumerate(threads);
//        return Arrays.asList(threads);
//    }

}
