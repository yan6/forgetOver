package com.example.demo.tool.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ywj
 * 线程池
 */
public class ThreadPool {
    private ExecutorService commonExec = new ThreadPoolExecutor(20, 20, 5 * 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());//公用线程池
    private static class SingletonHolder {
        private static final ThreadPool INSTANCE = new ThreadPool();
    }

    private ThreadPool() {
    }

    public static ThreadPool getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void exec(Runnable command) {
        commonExec.execute(command);
    }
}
