package com.aldogg.sorter.intType.mt;

import com.aldogg.parallel.SorterRunner;
import com.aldogg.sorter.MaskInfo;
import com.aldogg.sorter.intType.IntBitMaskSorter;
import com.aldogg.sorter.intType.IntBitMaskSorterMT;
import com.aldogg.sorter.intType.IntSorterUtils;
import com.aldogg.sorter.intType.st.QuickBitSorterInt;

import static com.aldogg.sorter.MaskInfo.getMaskAsArray;
import static com.aldogg.sorter.MaskInfo.getMaskBit;
import static com.aldogg.sorter.intType.IntSorterUtils.sortShortK;

public class QuickBitSorterMTInt extends IntBitMaskSorterMT {

    @Override
    public void sort(int[] array, int start, int end, int[] kList) {
        if (kList[0] == 31) { //there are negative numbers and positive numbers
            int sortMask = 1 << kList[0];
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
            int size1 = finalLeft - start;
            int size2 = end - finalLeft;
            SorterRunner.runTwoRunnable(
                    size1 > 1 ? () -> { //sort negative numbers
                        MaskInfo maskInfo1 = getMaskBit(array, start, finalLeft);
                        int mask1 = maskInfo1.getMask();
                        int[] kList1 = getMaskAsArray(mask1);
                        sortMT(array, start, finalLeft, kList1, 0, false);
                    } : null, size1,
                    size2 > 1 ? () -> { //sort positive numbers
                        MaskInfo maskInfo2 = getMaskBit(array, finalLeft, end);
                        int mask2 = maskInfo2.getMask();
                        int[] kList2 = getMaskAsArray(mask2);
                        sortMT(array, finalLeft, end, kList2, 0, false);
                    } : null, size2, params.getDataSizeForThreads(), params.getMaxThreads(), numThreads);
        } else {
            sortMT(array, start, end, kList, 0, false);
        }
    }

    public void sortMT(final int[] array, final int start, final int end, int[] kList, int kIndex, boolean recalculate) {
        final int n = end - start;
        if (n < params.getDataSizeForThreads()) {
            ((QuickBitSorterInt) stSorter).sort(array, start, end, kList, kIndex, recalculate);
            return;
        }

        if (recalculate && kIndex < 3) {
            MaskInfo maskParts = getMaskBit(array, start, end);
            int mask = maskParts.getMask();
            kList = getMaskAsArray(mask);
            kIndex = 0;

        }

        int kDiff = kList.length - kIndex;
        if (kDiff <= params.getShortKBits()) {
            if (kDiff < 1) {
                return;
            }
            sortShortK(array, start, end, kList, kIndex);
            return;
        }

        int sortMask = 1 << kList[kIndex];
        int finalLeft = IntSorterUtils.partitionNotStable(array, start, end, sortMask);
        final boolean recalculateBitMask = (finalLeft == start || finalLeft == end);

        int[] finalKList = kList;
        int finalKIndex = kIndex;
        Thread t1 = null;
        int size1 = finalLeft - start;
        if (size1 > 1) {
            if (size1 > params.getDataSizeForThreads() && numThreads.get() < params.getMaxThreads() + 1) {
                Runnable r1 = () -> sortMT(array, start, finalLeft, finalKList, finalKIndex + 1, recalculateBitMask);
                t1 = new Thread(r1);
                t1.start();
                numThreads.addAndGet(1);
            } else {
                sortMT(array, start, finalLeft, finalKList, finalKIndex + 1, recalculateBitMask);
            }
        }
        int size2 = end - finalLeft;
        if (size2 > 1) {
            sortMT(array, finalLeft, end, kList, kIndex + 1, recalculateBitMask);
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

    @Override
    public IntBitMaskSorter getSTIntSorter() {
        return new QuickBitSorterInt();
    }
}
