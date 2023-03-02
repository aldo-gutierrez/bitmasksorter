package com.aldogg.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ArrayParallelRunner {

    public static class APRParameters {
        int maxThreads = 2;
        AtomicInteger numThreads = null;
        boolean reduceInParallel = false;

        public APRParameters() {
        }

        public APRParameters(int maxThreads) {
            this.maxThreads = maxThreads;
        }

    }

    public static <R> R runInParallel(final Object array, final int start, final int endPlus1, APRParameters parameters, ArrayRunnable<R> mapReducer) {
        int maxThreads = parameters.maxThreads;
        if (maxThreads <= 1) {
            return mapReducer.map(array, start, endPlus1, 0, null);
        }
        AtomicBoolean stop = new AtomicBoolean(false);
        AtomicInteger numThreads = parameters.numThreads;
        int n = endPlus1 - start;
        int range = n / maxThreads;
        int maxThreadsMinus1 = maxThreads - 1;
        Thread[] threads = new Thread[maxThreadsMinus1];
        final R[] results = (R[]) new Object[maxThreads];
        int startThread = start;
        int endThread = startThread + range;
        for (int i = 0; i < maxThreadsMinus1; i++) {
            int finalI = i;
            int finalStartThread = startThread;
            int finalEndThread = endThread;
            Runnable runnable = () -> {
                if (numThreads != null) numThreads.addAndGet(1);
                results[finalI] = mapReducer.map(array, finalStartThread, finalEndThread, finalI, stop);
                if (numThreads != null) numThreads.addAndGet(-1);
            };
            threads[i] = new Thread(runnable);
            threads[i].start();
            startThread = endThread;
            endThread = startThread + range;
        }
        endThread = endPlus1;
        if (numThreads != null) numThreads.addAndGet(1);
        results[maxThreadsMinus1] = mapReducer.map(array, startThread, endThread, maxThreadsMinus1, stop);
        if (numThreads != null) numThreads.addAndGet(-1);

        for (int i = 0; i < maxThreadsMinus1; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int incD2 = 1;
        int inc = 2;
        if (!parameters.reduceInParallel) {
            while (incD2 < maxThreads) {
                for (int i = 0; i < maxThreads; i += inc) {
                    int i2 = i + incD2;
                    if (i2 < maxThreads) {
                        results[i] = mapReducer.reduce(results[i], results[i + incD2]);
                    }
                }
                inc = inc << 1;
                incD2 = incD2 << 1;
            }
        } else {
            while (incD2 < maxThreads) {
                List<Thread> threadList2 = new ArrayList<>();
                for (int i = 0; i < maxThreads; i += inc) {
                    int i2 = i + incD2;
                    if (i2 < maxThreads) {
                        int finalI = i;
                        int finalIncD2 = incD2;
                        Thread t = new Thread(() -> results[finalI] = mapReducer.reduce(results[finalI], results[finalI + finalIncD2]));
                        threadList2.add(t);
                        t.start();
                    }
                }
                inc = inc << 1;
                incD2 = incD2 << 1;
                for (Thread t : threadList2) {
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return results[0];
    }

    public static int[] splitWork(int n1, int n2, int maxThreads) {
        int sum = n1 + n2;
        int nPerThread = sum / maxThreads;
        if (n1 < n2) {
            if (n1 <= nPerThread) {
                return new int[]{1, maxThreads - 1};
            } else {
                double t1 = n1 * 1.0 / nPerThread;
                int t1i = (int) Math.round(t1);
                return new int[]{t1i, maxThreads - t1i};
            }
        } else {
            if (n2 <= nPerThread) {
                return new int[]{maxThreads - 1, 1};
            } else {
                double t2 = n2 * 1.0 / nPerThread;
                int t2i = (int) Math.round(t2);
                return new int[]{maxThreads - t2i, t2i};
            }
        }
    }

}