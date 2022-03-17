package com.aldogg.parallel;

import java.util.concurrent.atomic.AtomicInteger;

public class ArrayThreadRunner {

    public static <R> R runInParallel(final int[] list, final int start, final int end, final int maxThreads,
                                      AtomicInteger numThreads, R initial, ArrayRunnable<R> mapReducer) {
        int length = end - start;
        int range = length/maxThreads;
        Thread[] threads = new Thread[maxThreads];
        R[] partialResults = (R[]) new Object[maxThreads];
        for (int i=0; i < maxThreads; i++) {
            int startThread = start + i * range;
            int endThread = (i == maxThreads -1) ? end: startThread + range;
            int finalI = i;
            Runnable runnable = () -> partialResults[finalI] = mapReducer.map(list, startThread, endThread);
            if (i == maxThreads -1) {
                runnable.run();
            } else {
                threads[i] = new Thread(runnable);
                threads[i].start();
                numThreads.addAndGet(1);
            }
        }
        for (int i=0; i < maxThreads - 1; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                numThreads.addAndGet(-1);
            }
        }
        R finalResult = null;
        for (int i=0; i < maxThreads; i++) {
            if (i==0) {
                finalResult = mapReducer.reduce(initial, partialResults[0]);
            } else {
                finalResult = mapReducer.reduce(finalResult, partialResults[i]);
            }
        }
        return finalResult;
    }
}
