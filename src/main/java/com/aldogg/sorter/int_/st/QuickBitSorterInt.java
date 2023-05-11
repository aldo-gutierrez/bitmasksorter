package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.int_.IntBitMaskSorter;
import com.aldogg.sorter.int_.IntSorterUtils;


import static com.aldogg.sorter.BitSorterParams.*;
import static com.aldogg.sorter.int_.IntSorterUtils.sortShortK;

public class QuickBitSorterInt extends IntBitMaskSorter {

    @Override
    public void sort(int[] array, int start, int endP1, int[] bList, Object multiThreadParams) {
        sort(array, start, endP1, bList, 0, false);
    }

    public void sort(final int[] array, final int start, final int endP1, int[] bList, int kIndex, boolean recalculate) {
        final int n = endP1 - start;
        if (n <= VERY_SMALL_N_SIZE) {
            snFunctions[n].accept(array, start);
            return;
        }

        if (recalculate && kIndex < 3) {
            MaskInfoInt maskParts = MaskInfoInt.calculateMask(array, start, endP1);
            int mask = maskParts.getMask();
            bList = MaskInfoInt.getMaskAsArray(mask);
            kIndex = 0;
        }

        int kDiff = bList.length - kIndex;
        if (kDiff <= params.getShortKBits()) {
            if (kDiff < 1) {
                return;
            }
            sortShortK(array, start, endP1, bList, kIndex);
            return;
        }

        int sortMask = 1 << bList[kIndex];
        int finalLeft = IntSorterUtils.partitionNotStable(array, start, endP1, sortMask);
        if (recalculate) {
            if (finalLeft - start > 1) {
                sort(array, start, finalLeft, bList, kIndex + 1);
            }
            if (endP1 - finalLeft > 1) {
                sort(array, finalLeft, endP1, bList, kIndex + 1);
            }
        } else {
            boolean recalculateBitMask = (finalLeft == start || finalLeft == endP1);

            if (finalLeft - start > 1) {
                sort(array, start, finalLeft, bList, kIndex + 1, recalculateBitMask);
            }
            if (endP1 - finalLeft > 1) {
                sort(array, finalLeft, endP1, bList, kIndex + 1, recalculateBitMask);
            }
        }
    }

    public void sort(final int[] array, final int start, final int endP1, int[] bList, int kIndex) {
        final int n = endP1 - start;
        if (n <= VERY_SMALL_N_SIZE) {
            snFunctions[n].accept(array, start);
            return;
        }

        int kDiff = bList.length - kIndex;
        if (kDiff <= params.getShortKBits()) {
            if (kDiff < 1) {
                return;
            }
            sortShortK(array, start, endP1, bList, kIndex);
            return;
        }

        int sortMask = 1 << bList[kIndex];
        int finalLeft = IntSorterUtils.partitionNotStable(array, start, endP1, sortMask);

        if (finalLeft - start > 1) {
            sort(array, start, finalLeft, bList, kIndex + 1);
        }
        if (endP1 - finalLeft > 1) {
            sort(array, finalLeft, endP1, bList, kIndex + 1);
        }
    }

}
