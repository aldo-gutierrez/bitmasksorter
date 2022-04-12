package com.aldogg.sorter;

import com.aldogg.parallel.SorterRunner;
import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

import java.util.concurrent.atomic.AtomicInteger;

import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.intType.IntSorterUtils.sortShortKList;

public class QuickBitSorterMTInt extends QuickBitSorterInt implements IntSorter {

    AtomicInteger numThreads = new AtomicInteger(1);

    protected BitSorterParams params = BitSorterParams.getMTParams();

    @Override
    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    @Override
    public void sort(int[] list) {
        if (list.length < 2) {
            return;
        }
        if (params.getMaxThreads() < 2) {
            super.sort(list);
        }
        final int start = 0;
        final int end = list.length;
        int ordered = isUnsigned() ? listIsOrderedUnSigned(list, start, end) : listIsOrderedSigned(list, start, end);
        if (ordered == IsOrderedResult.DESCENDING) {
            IntSorterUtils.reverseList(list, start, end);
        }
        if (ordered != IsOrderedResult.UNORDERED) return;

        int[] maskParts = getMaskBit(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);
        if (kList.length == 0) {
            return;
        }

        if (kList[0] == 31) { //there are negative numbers and positive numbers
            int sortMask = BitSorterUtils.getMaskBit(kList[0]);
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(list, start, end, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(list, start, end, sortMask);
            int size1 = finalLeft - start;
            int size2 = end - finalLeft;
            SorterRunner.runTwoRunnable(
                    size1 > 1 ? () -> { //sort negative numbers
                        int[] maskParts1 = getMaskBit(list, start, finalLeft);
                        int mask1 = maskParts1[0] & maskParts1[1];
                        int[] kList1 = getMaskAsList(mask1);
                        sortMT(list, start, finalLeft, kList1, 0, false);
                    } : null, size1,
                    size2 > 1 ? () -> { //sort positive numbers
                        int[] maskParts2 = getMaskBit(list, finalLeft, end);
                        int mask2 = maskParts2[0] & maskParts2[1];
                        int[] kList2 = getMaskAsList(mask2);
                        sortMT(list, finalLeft, end, kList2, 0, false);
                    } : null, size2, params.getDataSizeForThreads(),params.getMaxThreads(),  numThreads);
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

}
