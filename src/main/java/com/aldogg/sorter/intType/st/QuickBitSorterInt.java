package com.aldogg.sorter.intType.st;

import com.aldogg.sorter.MaskInfo;
import com.aldogg.sorter.intType.IntBitMaskSorter;
import com.aldogg.sorter.intType.IntSorterUtils;


import static com.aldogg.sorter.BitSorterParams.*;
import static com.aldogg.sorter.intType.IntSorterUtils.sortShortK;

public class QuickBitSorterInt extends IntBitMaskSorter {

    @Override
    public void sort(int[] array, int start, int end, int[] kList) {
        if (kList[0] == 31) { //there are negative numbers
            int sortMask = 1 << kList[0];
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
            if (finalLeft - start > 1) {
                sort(array, start, finalLeft, kList, 1, true);
            }
            if (end - finalLeft > 1) {
                sort(array, finalLeft, end, kList, 1, true);
            }
        } else {
            sort(array, start, end, kList, 0, false);
        }
    }

    public void sort(final int[] array, final int start, final int end, int[] kList, int kIndex, boolean recalculate) {
        final int n = end - start;
        if (n <= VERY_SMALL_N_SIZE) {
            snFunctions.get(n).accept(array, start);
            return;
        }

        if (recalculate && kIndex < 3) {
            MaskInfo maskParts = MaskInfo.getMaskBit(array, start, end);
            int mask = maskParts.getMask();
            kList = MaskInfo.getMaskAsArray(mask);
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
        if (recalculate) {
            if (finalLeft - start > 1) {
                sort(array, start, finalLeft, kList, kIndex + 1);
            }
            if (end - finalLeft > 1) {
                sort(array, finalLeft, end, kList, kIndex + 1);
            }
        } else {
            boolean recalculateBitMask = (finalLeft == start || finalLeft == end);

            if (finalLeft - start > 1) {
                sort(array, start, finalLeft, kList, kIndex + 1, recalculateBitMask);
            }
            if (end - finalLeft > 1) {
                sort(array, finalLeft, end, kList, kIndex + 1, recalculateBitMask);
            }
        }
    }

    public void sort(final int[] array, final int start, final int end, int[] kList, int kIndex) {
        final int n = end - start;
        if (n <= VERY_SMALL_N_SIZE) {
            snFunctions.get(n).accept(array, start);
            return;
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

        if (finalLeft - start > 1) {
            sort(array, start, finalLeft, kList, kIndex + 1);
        }
        if (end - finalLeft > 1) {
            sort(array, finalLeft, end, kList, kIndex + 1);
        }
    }

}
