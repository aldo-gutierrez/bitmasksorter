package com.aldogg.parallel;

public class ArrayThreadRunner {

    public static <R> R runInParallel(final int[] list, final int start, final int end, final int numThreads, R initial, ArrayRunnable<R> mapReducer) {
        int length = end - start;
        int range = length/numThreads;
        Thread[] threads = new Thread[numThreads];
        R[] partialResults = (R[]) new Object[numThreads];
        for (int i=0; i < numThreads; i++) {
            int startThread = start + i * range;
            int endThread = (i == numThreads -1) ? end: startThread + range;
            int finalI = i;
            Runnable runnable = () -> partialResults[finalI] = mapReducer.map(list, startThread, endThread);
            if (i == numThreads -1) {
                runnable.run();
            } else {
                threads[i] = new Thread(runnable);
                threads[i].start();
            }
        }
        for (int i=0; i < numThreads - 1; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        R finalResult = null;
        for (int i=0; i < numThreads; i++) {
            if (i==0) {
                finalResult = mapReducer.reduce(initial, partialResults[0]);
            } else {
                finalResult = mapReducer.reduce(finalResult, partialResults[i]);
            }
        }
        return finalResult;
    }
}
