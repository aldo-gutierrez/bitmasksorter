package com.aldogg.parallel;

import java.util.concurrent.atomic.AtomicBoolean;

public class ArrayParallelRunner {

    public static class APRParameters {
        int maxThreads = 2;
        boolean reduceInParallel = false;

        public APRParameters() {
        }

        public APRParameters(int maxThreads) {
            this.maxThreads = maxThreads;
        }

    }

    public static <R> R runInParallel(final Object array, final int start, final int endP1, APRParameters parameters, ArrayRunnable<R> mapReducer) {
        int maxThreads = parameters.maxThreads;
        if (maxThreads <= 1) {
            return mapReducer.map(array, start, endP1, 0, null);
        }
        ParallelRunner parallelRunner = new ParallelRunner();
        parallelRunner.init(maxThreads, 1);
        AtomicBoolean stop = new AtomicBoolean(false);
        int n = endP1 - start;
        int range = n / maxThreads;
        final R[] results = (R[]) new Object[maxThreads];
        int startThread = start;
        for (int i = 0; i < maxThreads; i++) {
            int endThread = i == maxThreads - 1 ? endP1 : startThread + range;
            int finalI = i;
            int finalStartThread = startThread;
            Runnable runnable = () -> {
                results[finalI] = mapReducer.map(array, finalStartThread, endThread, finalI, stop);
            };
            parallelRunner.preSubmit(runnable);
            startThread = endThread;
        }
        parallelRunner.submit();
        parallelRunner.waitAll();

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
                for (int i = 0; i < maxThreads; i += inc) {
                    int i2 = i + incD2;
                    if (i2 < maxThreads) {
                        int finalI = i;
                        int finalIncD2 = incD2;
                        parallelRunner.preSubmit(() -> results[finalI] = mapReducer.reduce(results[finalI], results[finalI + finalIncD2]));
                    }
                }
                inc = inc << 1;
                incD2 = incD2 << 1;
                parallelRunner.submit();
                parallelRunner.waitAll();
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