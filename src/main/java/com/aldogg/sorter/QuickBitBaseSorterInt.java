package com.aldogg.sorter;

import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

import static com.aldogg.sorter.BitSorterUtils.*;

/**
 * Basic XXXSorter
 * It doesn't include a count sort
 * It doesn't include a comparator sort for small lists
 */
public class QuickBitBaseSorterInt implements IntSorter {
    boolean unsigned = false;

    @Override
    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

    public void sort(int[] array) {
        if (array.length < 2) {
            return;
        }
        final int start = 0;
        final int end = array.length;
        int[] maskParts = getMaskBit(array, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsArray(mask);
        if (kList.length == 0) {
            return;
        }
        if (kList[0] == 31) { //there are negative numbers
            int sortMask = BitSorterUtils.getMaskBit(kList[0]);
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
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

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    public void sort(final int[] array, final int start, final int end, final int[] kList, final int kIndex) {
        int kDiff = kList.length - kIndex;
        if (kDiff < 1) {
            return;
        }
        int sortMask = getMaskBit(kList[kIndex]);
        int finalLeft = IntSorterUtils.partitionNotStable(array, start, end, sortMask);
        if (finalLeft - start > 1) {
            sort(array, start, finalLeft, kList, kIndex + 1);
        }
        if (end - finalLeft > 1) {
            sort(array, finalLeft, end, kList, kIndex + 1);
        }
    }


}
