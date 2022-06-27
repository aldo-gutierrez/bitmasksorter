package com.aldogg.sorter.intType.st;

import com.aldogg.sorter.intType.IntBitMaskSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

import static com.aldogg.sorter.BitSorterUtils.*;

/**
 * Basic XXXSorter
 * It doesn't include a count sort
 * It doesn't include a comparator sort for small lists
 */
public class QuickBitBaseSorterInt extends IntBitMaskSorter {

    @Override
    public void sort(int[] array, int start, int end, int[] kList) {
        if (kList[0] == 31) { //there are negative numbers
            int sortMask = 1 << kList[0];
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
            int[] maskParts;
            int mask;
            if (finalLeft - start > 1) {
                maskParts = getMaskBit(array, start, finalLeft);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsArray(mask);
                sort(array, start, finalLeft, kList, 0);
            }
            if (end - finalLeft > 1) {
                maskParts = getMaskBit(array, finalLeft, end);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsArray(mask);
                sort(array, finalLeft, end, kList, 0);
            }
        } else {
            sort(array, start, end, kList, 0);
        }
    }

    public void sort(final int[] array, final int start, final int end, final int[] kList, final int kIndex) {
        int kDiff = kList.length - kIndex;
        if (kDiff < 1) {
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
