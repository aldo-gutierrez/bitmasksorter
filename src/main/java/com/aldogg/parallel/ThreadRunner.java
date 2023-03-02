package com.aldogg.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadRunner {
    int mode;
    int maxThreads;
    List<Future> futures;

    ExecutorService executorService;

    List<Thread> threads;

    public void init(int maxThreads, int mode) {
        this.maxThreads = maxThreads;
        this.mode = mode;
        if (mode == 0) {
            futures = new ArrayList<>();
            executorService = Executors.newFixedThreadPool(maxThreads);
        } else {
            threads = new ArrayList<>();
        }
    }

    public void submit(Runnable runnable) {
        if (mode == 0) {
            futures.add(executorService.submit(runnable));
        } else {
            Thread thread = new Thread(runnable);
            threads.add(thread);
            thread.start();
        }
    }

    public void waitAll() {
        if (mode == 0) {
            for (Future future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            futures.clear();
        } else {
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            threads.clear();
        }
    }


}
