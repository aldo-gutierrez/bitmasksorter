package com.aldogg;

import com.aldogg.intType.IntSorter;
import com.aldogg.intType.IntSorterUtils;
import com.aldogg.parallel.ArrayRunnable;
import com.aldogg.parallel.ArrayThreadRunner;

import java.util.concurrent.atomic.AtomicInteger;

import static com.aldogg.BitSorterParams.*;
import static com.aldogg.BitSorterUtils.*;
import static com.aldogg.intType.IntSorterUtils.sortShortList;

public class QuickBitSorterMTUInt extends QuickBitSorterUInt implements IntSorter {

    AtomicInteger numThreads = new AtomicInteger(1);

    protected BitSorterParams params = BitSorterParams.getMTParams();

    int[] aux;

    @Override
    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    @Override
    public void sort(int[] list) {
        if (list.length < 2) {
            return;
        }
        aux = null;
        if (params.getMaxThreads() < 2) {
            super.sort(list);
        }
        numThreads = new AtomicInteger(1);
        final int start = 0;
        final int end = list.length;
        //if (listIsOrdered(list, start, end)) return;

        /*
        int[] maskParts = ArrayThreadRunner.runInParallel(list, start, end, 2, new ArrayRunnable<int[]>() {
            @Override
            public int[] map(int[] list, int start, int end) {
                return getMask(list, start, end);
            }

            @Override
            public int[] reduce(int[] m1, int[] m2) {
                return new int[]{m1[0] | m2[0], m1[1] | m2[1]};
            }
        });
        */


        int[] maskParts = getMaskBit(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] listK = getMaskAsList(mask);
        sortMT(list, start, end, listK, 0, false);
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    public void sortMT(final int[] list, final int start, final int end, int[] kList, int kIndex, boolean recalculate) {
        final int listLength = end - start;
        if (listLength <= SMALL_LIST_SIZE) {
            SortingNetworks.sortSmallList(list, start, end);
            return;
        }
        int kDiff = kList.length - kIndex;

        if (recalculate) {
            int[] maskParts = getMaskBit(list, start, end);
            int mask = maskParts[0] & maskParts[1];
            kList = getMaskAsList(mask);
            kIndex = 0;

        }

        if (kDiff < 1) {
            return;
        }

        if (kDiff <= params.getCountingSortBits()) {
            sortShortList(list, start, end, kList, kIndex);
            return;
        }
        int sortMask = getMaskBit(kList[kIndex]);
        int finalLeft = IntSorterUtils.partitionNotStable(list, start, end, sortMask);
        final boolean recalculateBitMask = (finalLeft == start || finalLeft == end);

        int[] finalKList = kList;
        int finalKIndex = kIndex;
        Thread t1 = null;
        int size1 = finalLeft - start;
        if (size1 > params.getDataSizeForThreads()) {
            if (numThreads.get() < params.getMaxThreads() + 1) {
                Runnable r1 = () -> sortMT(list, start, finalLeft, finalKList, finalKIndex + 1, recalculateBitMask);
                t1 = new Thread(r1);
                t1.start();
                numThreads.addAndGet(1);
            } else {
                sortMT(list, start, finalLeft, finalKList, finalKIndex + 1, recalculateBitMask);
            }
        } else {
            if (size1 > 1) {
                sort(list, start, finalLeft, finalKList, finalKIndex + 1, recalculateBitMask);
            }
        }
        int size2 = end - finalLeft;
        if (size2 > params.getDataSizeForThreads()) {
            sortMT(list, finalLeft, end, kList, kIndex + 1, recalculateBitMask);
        } else {
            if (size2 > 1) {
                sort(list, finalLeft, end, kList, kIndex + 1, recalculateBitMask);
            }
        }
        if (t1 != null) {
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                numThreads.addAndGet(-1);
            }
        }
    }

    //somehow slow, tested 2021 12 10
    protected int partitionMT(int[] list, int start, int end, int sortMask) {
        int listLength = end - start;
        if (aux == null) {
            aux = new int[listLength];
        }
        int maxThreads = params.getMaxThreads();
        if (listLength > maxThreads * params.getDataSizeForThreads()) {

            Object left = ArrayThreadRunner.runInParallel(list, start, end, maxThreads, new int[]{}, new ArrayRunnable<Object>() {
                int left = start;
                int right = end - 1;

                @Override
                public int[] map(int[] list, int start, int end) {
                    int left = IntSorterUtils.partitionNotStable(list, start, end, sortMask);
                    return new int[]{start, end, left};
                }

                @Override
                public Object reduce(Object result, Object partialResult) {
                    int[] partialResultInt = (int[]) partialResult;
                    int start = partialResultInt[0];
                    int end = partialResultInt[1];
                    int partialLeft = partialResultInt[2];
                    int leftLength = partialLeft - start;
                    if (leftLength > 0) {
                        System.arraycopy(list, start, aux, left, leftLength);
                        left = left + leftLength;
                    }

                    int rightLength = (end - start) - leftLength;
                    if (rightLength > 0) {
                        System.arraycopy(list, end - rightLength, aux, right - rightLength + 1, rightLength);
                        right = right - rightLength;
                    }
                    return left;
                }
            });

            System.arraycopy(aux, start, list, start, end - start);
            return (Integer) left;
        } else {
            return IntSorterUtils.partitionNotStable(list, start, end, sortMask);
        }
    }

    //too slow, tested 2021 11 19
    protected int partitionMT2(int[] list, int start, int end, int sortMask) {
        int listLength = end - start;
        if (aux == null) {
            aux = new int[listLength];
        }
        final AtomicInteger left = new AtomicInteger(start);
        final AtomicInteger right = new AtomicInteger(end - 1);

        int maxThreads = 2; //calculate max threads available to use
        if (listLength > maxThreads * params.getDataSizeForThreads()) {

            ArrayThreadRunner.runInParallel(list, start, end, maxThreads, null, new ArrayRunnable<Object>() {

                @Override
                public Object map(int[] list, int start, int end) {
                    for (int i = start; i < end; i++) {
                        int element = list[i];
                        if ((element & sortMask) == 0) {
                            aux[left.getAndAdd(1)] = element;
                        } else {
                            aux[right.getAndAdd(-1)] = element;
                        }
                    }
                    return null;
                }

                @Override
                public Object reduce(Object result, Object partialResult) {
                    return null;
                }
            });
            System.arraycopy(aux, start, list, start, end - start);
            return left.get();
        } else {
            return IntSorterUtils.partitionNotStable(list, start, end, sortMask);
        }
    }


}
