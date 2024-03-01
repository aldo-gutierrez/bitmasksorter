package com.aldogg.sorter.int_.mt;

import com.aldogg.parallel.ParallelRunner;
import com.aldogg.sorter.FieldSorterOptions;
import com.aldogg.sorter.int_.BitMaskSorterInt;
import com.aldogg.sorter.int_.BitMaskSorterMTInt;
import com.aldogg.sorter.int_.SorterUtilsInt;
import com.aldogg.sorter.int_.st.QuickBitSorterInt;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;

import static com.aldogg.parallel.ArrayParallelRunner.splitWork;
import static com.aldogg.sorter.int_.SorterUtilsIntExt.sortShortK;
import static com.aldogg.sorter.shared.int_mask.MaskInfoInt.getMaskAsArray;

public class QuickBitSorterMTInt extends BitMaskSorterMTInt {

    @Override
    public void sort(int[] array, int start, int endP1, FieldSorterOptions options, int[] bList, Object params) {
        //Number of Threads was multiplied by two to get back performance TODO investigate
        sortMT(array, start, endP1, options, bList, 0, false, ((Integer) params) * 2);
    }

    public void sortMT(final int[] array, final int start, final int endP1, FieldSorterOptions options, int[] bList, int bListIndex, boolean recalculate, int maxThreads) {
        final int n = endP1 - start;
        if (n < params.getDataSizeForThreads() || maxThreads == 1) {
            QuickBitSorterInt stIntSorter = (QuickBitSorterInt) getSTIntSorter();
            stIntSorter.sort(array, start, endP1, bList, bListIndex, recalculate, options);
            return;
        }

        if (recalculate && bListIndex < 3) {
            MaskInfoInt maskParts = MaskInfoInt.calculateMask(array, start, endP1);
            int mask = maskParts.getMask();
            bList = getMaskAsArray(mask);
            bListIndex = 0;

        }

        int kDiff = bList.length - bListIndex;
        if (kDiff <= params.getShortKBits()) {
            if (kDiff < 1) {
                return;
            }
            sortShortK(array, start, endP1, bList, bListIndex);
            return;
        }

        int sortMask = 1 << bList[bListIndex];
        int finalLeft = SorterUtilsInt.partitionNotStable(array, start, endP1, sortMask);
        final boolean recalculateBitMask = (finalLeft == start || finalLeft == endP1);

        int[] finalbList = bList;
        int finalBListIndex = bListIndex;
        int n1 = finalLeft - start;
        int n2 = endP1 - finalLeft;

        int[] threadNumbers = splitWork(n1, n2, maxThreads);
        ParallelRunner.runTwoRunnable(
                n1 > 1 ? () -> {
                    sortMT(array, start, finalLeft, options, finalbList, finalBListIndex + 1, recalculateBitMask, threadNumbers[0]);
                } : null, n1,
                n2 > 1 ? () -> {
                    sortMT(array, finalLeft, endP1, options, finalbList, finalBListIndex + 1, recalculateBitMask, threadNumbers[1]);
                } : null, n2, params.getDataSizeForThreads(), maxThreads);
    }

    @Override
    public BitMaskSorterInt getSTIntSorter() {
        return new QuickBitSorterInt();
    }
}
