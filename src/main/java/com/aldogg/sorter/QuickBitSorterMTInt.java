package com.aldogg.sorter;

import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;
import com.aldogg.parallel.ArrayRunnable;
import com.aldogg.parallel.ArrayThreadRunner;

import java.util.concurrent.atomic.AtomicInteger;

import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.intType.IntSorterUtils.sortShortKList;

public class QuickBitSorterMTInt extends QuickBitSorterInt implements IntSorter {

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
        int[] maskParts = getMaskBit(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);
        if (kList.length == 0) {
            return;
        }

        if (!isUnsigned() && kList[0] == 31) { //there are negative numbers and positive numbers
            int sortMask = BitSorterUtils.getMaskBit(kList[0]);
            int finalLeft = IntSorterUtils.partitionReverseNotStable(list, start, end, sortMask);
            Thread t1 = null;
            if (finalLeft - start > 1) { //sort negative numbers
                maskParts = getMaskBit(list, start, finalLeft);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsList(mask);
                int[] finalKList = kList;
                Runnable r1 = () -> sortMT(list, start, finalLeft, finalKList, 0, false);
                t1 = new Thread(r1);
                t1.start();
                numThreads.addAndGet(1);
            }
            if (end - finalLeft > 1) { //sort positive numbers
                maskParts = getMaskBit(list, finalLeft, end);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsList(mask);
                sortMT(list, finalLeft, end, kList, 0, false);
            }
            if (t1 != null) {
                try {
                    t1.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            sortMT(list, start, end, kList, 0, false);
        }

    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    public void sortMT(final int[] list, final int start, final int end, int[] kList, int kIndex, boolean recalculate) {
        final int listLength = end - start;
        if (listLength < params.getDataSizeForThreads()) {
            sort(list, start, end, kList, kIndex, recalculate);
            return;
        }

        if (recalculate && kIndex < 3) {
            int[] maskParts = getMaskBit(list, start, end);
            int mask = maskParts[0] & maskParts[1];
            kList = getMaskAsList(mask);
            kIndex = 0;

        }

        int kDiff = kList.length - kIndex;
        if (kDiff < 1) {
            return;
        }

        if (kDiff <= params.getCountingSortBits()) {
            sortShortKList(list, start, end, kList, kIndex);
            return;
        }

        int sortMask = getMaskBit(kList[kIndex]);
        int finalLeft = IntSorterUtils.partitionNotStable(list, start, end, sortMask);
        final boolean recalculateBitMask = (finalLeft == start || finalLeft == end);

        int[] finalKList = kList;
        int finalKIndex = kIndex;
        Thread t1 = null;
        int size1 = finalLeft - start;
        if (size1 > 1) {
            if (size1 > params.getDataSizeForThreads() && numThreads.get() < params.getMaxThreads() + 1) {
                Runnable r1 = () -> sortMT(list, start, finalLeft, finalKList, finalKIndex + 1, recalculateBitMask);
                t1 = new Thread(r1);
                t1.start();
                numThreads.addAndGet(1);
            } else {
                sortMT(list, start, finalLeft, finalKList, finalKIndex + 1, recalculateBitMask);
            }
        }
        int size2 = end - finalLeft;
        if (size2 > 1) {
            sortMT(list, finalLeft, end, kList, kIndex + 1, recalculateBitMask);
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

            Object left = ArrayThreadRunner.runInParallel(list, start, end, maxThreads, numThreads, new int[]{}, new ArrayRunnable<Object>() {
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

}
