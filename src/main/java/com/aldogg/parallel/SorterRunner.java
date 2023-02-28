package com.aldogg.parallel;

import java.util.concurrent.atomic.AtomicInteger;


public class SorterRunner {

    public static void runTwoRunnable(Runnable r1, int length1, Runnable r2, int length2, int dataSizeForThreads, int maxThreads, AtomicInteger numThreads) {
        if (length1 < dataSizeForThreads || length2 < dataSizeForThreads) {
            r1.run();
            r2.run();
            return;
        }

        if (numThreads == null) {
            if (length1 > length2) {
                Thread t = new Thread(r2);
                t.start();
                r1.run();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Thread t = new Thread(r1);
                t.start();
                r2.run();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        if (numThreads.get() < maxThreads) {
            //length1 > length2 is faster than length2> length1 that is faster than just lunch r1
            //but lunching r1 inline as in QuickBitSorterMT is faster than length1 > length2. OoO
            if (length1 > length2) {
                Thread t = new Thread(r2);
                numThreads.addAndGet(1);
                t.start();
                r1.run();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    numThreads.addAndGet(-1);
                }
            } else {
                Thread t = new Thread(r1);
                numThreads.addAndGet(1);
                t.start();
                r2.run();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    numThreads.addAndGet(-1);
                }
            }
        } else {
            r1.run();
            r2.run();
        }
    }


}
