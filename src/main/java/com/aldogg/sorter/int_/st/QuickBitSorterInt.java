package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.FieldSorterOptions;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;
import com.aldogg.sorter.int_.BitMaskSorterInt;
import com.aldogg.sorter.int_.SorterUtilsInt;


import static com.aldogg.sorter.BitSorterParams.*;
import static com.aldogg.sorter.int_.SorterUtilsIntExt.sortShortK;

public class QuickBitSorterInt extends BitMaskSorterInt {

    @Override
    public void sort(int[] array, int start, int endP1, FieldSorterOptions options, int[] bList, Object params) {
        sort(array, start, endP1, bList, 0, false);
    }

    public void sort(final int[] array, final int start, final int endP1, int[] bList, int bListIndex, boolean recalculate) {
        final int n = endP1 - start;
        if (n <= VERY_SMALL_N_SIZE) {
            snFunctions[n].accept(array, start);
            return;
        }

        if (recalculate && bListIndex < 3) {
            MaskInfoInt maskParts = MaskInfoInt.calculateMask(array, start, endP1);
            int mask = maskParts.getMask();
            bList = MaskInfoInt.getMaskAsArray(mask);
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
        if (recalculate) {
            if (finalLeft - start > 1) {
                sort(array, start, finalLeft, bList, bListIndex + 1);
            }
            if (endP1 - finalLeft > 1) {
                sort(array, finalLeft, endP1, bList, bListIndex + 1);
            }
        } else {
            boolean recalculateBitMask = (finalLeft == start || finalLeft == endP1);

            if (finalLeft - start > 1) {
                sort(array, start, finalLeft, bList, bListIndex + 1, recalculateBitMask);
            }
            if (endP1 - finalLeft > 1) {
                sort(array, finalLeft, endP1, bList, bListIndex + 1, recalculateBitMask);
            }
        }
    }

    public void sort(final int[] array, final int start, final int endP1, int[] bList, int bListIndex) {
        final int n = endP1 - start;
        if (n <= VERY_SMALL_N_SIZE) {
            snFunctions[n].accept(array, start);
            return;
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

        if (finalLeft - start > 1) {
            sort(array, start, finalLeft, bList, bListIndex + 1);
        }
        if (endP1 - finalLeft > 1) {
            sort(array, finalLeft, endP1, bList, bListIndex + 1);
        }
    }

}
