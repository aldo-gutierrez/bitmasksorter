package com.aldogg.parallel;

import java.util.concurrent.atomic.AtomicInteger;

public class ArrayParallelRunner {

    public static <R> R runInParallel(final int[] array, final int start, final int end, final int maxThreads,
                                      AtomicInteger numThreads, ArrayRunnable<R> mapReducer) {
        if (maxThreads <= 1) {
            return mapReducer.map(array, start, end);
        }
        int length = end - start;
        int range = length / maxThreads;
        int maxThreadsM1 = maxThreads - 1;
        Thread[] threads = new Thread[maxThreadsM1];
        final R[] results = (R[]) new Object[maxThreads];
        int startThread = start;
        int endThread = startThread + range;
        for (int i = 0; i < maxThreadsM1; i++) {
            int finalI = i;
            int finalStartThread = startThread;
            int finalEndThread = endThread;
            Runnable runnable = () -> {
                numThreads.addAndGet(1);
                results[finalI] = mapReducer.map(array, finalStartThread, finalEndThread);
                numThreads.addAndGet(-1);
            };
            threads[i] = new Thread(runnable);
            threads[i].start();
            startThread = endThread;
            endThread = startThread + range;
        }
        endThread = end;
        numThreads.addAndGet(1);
        results[maxThreadsM1] = mapReducer.map(array, startThread, endThread);
        numThreads.addAndGet(-1);

        for (int i = 0; i < maxThreadsM1; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int numResults = maxThreads;
        while (numResults > 1) {
            int quotient = numResults / 2;
            int reminder = numResults % 2;
            threads = new Thread[quotient];

            for (int i = 0; i < quotient; i++) {
                int finalI = i;
                Runnable runnable = () -> {
                    numThreads.addAndGet(1);
                    results[finalI] = mapReducer.reduce(results[finalI *2], results[finalI *2+1]);
                    numThreads.addAndGet(-1);
                };
                threads[i] = new Thread(runnable);
                threads[i].start();
            }
            if (reminder == 1) {
                numThreads.addAndGet(1);
                results[quotient-1] = mapReducer.reduce(results[quotient-1], results[quotient*2+2]);
                numThreads.addAndGet(-1);
            }
            for (int i = 0; i < quotient; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i= quotient; i < results.length; i++) {
                results[i] = null;
            }
            numResults = quotient;
        }
        return results[0];
    }
}