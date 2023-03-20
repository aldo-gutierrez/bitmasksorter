package com.aldogg.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelRunner {
    int mode;
    int maxThreads;
    List<Future> futures;

    ExecutorService executorService;

    List<Thread> threads;

    List<Runnable> runnables;

    public void init(int maxThreads, int mode) {
        this.maxThreads = maxThreads;
        this.mode = mode;
        if (mode == 0) {
            futures = new ArrayList<>();
            executorService = Executors.newFixedThreadPool(maxThreads);
        } else {
            threads = new ArrayList<>();
        }
        runnables = new ArrayList<>();
    }

    public void preSubmit(Runnable runnable) {
        runnables.add(runnable);
    }

    public void submit() {
        if (mode == 0) {
            for (int i = 0; i < runnables.size(); i++) {
                Runnable runnable = runnables.get(i);
                if (i == runnables.size() - 1) {
                    runnable.run();
                } else {
                    futures.add(executorService.submit(runnable));
                }
            }
        } else {
            for (int i = 0; i < runnables.size(); i++) {
                Runnable runnable = runnables.get(i);
                if (i == runnables.size() - 1) {
                    runnable.run();
                } else {
                    Thread thread = new Thread(runnable);
                    threads.add(thread);
                    thread.start();
                }
            }
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
        runnables.clear();
    }

    public static void runTwoRunnable(Runnable r1, int length1, Runnable r2, int length2, int dataSizeForThreads, int maxThreads, AtomicInteger runningThreads) {
        if (r1 == null) {
            r2.run();
            return;
        }
        if (r2 == null) {
            r1.run();
            return;
        }

        if (length1 < dataSizeForThreads || length2 < dataSizeForThreads) {
            r1.run();
            r2.run();
            return;
        }

        if (runningThreads == null) {
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

        if (runningThreads.get() < maxThreads) {
            //length1 > length2 is faster than length2> length1 that is faster than just lunch r1
            //but lunching r1 inline as in QuickBitSorterMT is faster than length1 > length2. OoO
            if (length1 > length2) {
                Thread t = new Thread(r2);
                runningThreads.addAndGet(1);
                t.start();
                r1.run();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    runningThreads.addAndGet(-1);
                }
            } else {
                Thread t = new Thread(r1);
                runningThreads.addAndGet(1);
                t.start();
                r2.run();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    runningThreads.addAndGet(-1);
                }
            }
        } else {
            r1.run();
            r2.run();
        }
    }

}
