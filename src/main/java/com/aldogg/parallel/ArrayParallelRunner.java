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
        //R[] previousResults = results;
        //R[] currentResults = results;
        while (numResults > 1) {
            int quotient = numResults / 2;
            int reminder = numResults % 2;
;
            //currentResults = (R[]) new Object[quotient];
            for (int i = 0; i < quotient; i++) {
                //currentResults[i] = mapReducer.reduce(previousResults[i*2], previousResults[i*2+1]);
                results[i] = mapReducer.reduce(results[i*2], results[i*2+1]);
            }
            if (reminder == 1) {
                //currentResults[quotient-1] = mapReducer.reduce(currentResults[quotient-1], previousResults[quotient*2+2]);
                results[quotient-1] = mapReducer.reduce(results[quotient-1], results[quotient*2+2]);
            }
            numResults = quotient;
            //previousResults = currentResults;
        }
        return results[0];
    }
}
