package com.aldogg;

import com.aldogg.intType.IntSorter;
import com.aldogg.intType.IntSorterUtils;

import static com.aldogg.BitSorterUtils.*;

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

    public void sort(int[] list) {
        if (list.length < 2) {
            return;
        }
        final int start = 0;
        final int end = list.length;
        int[] maskParts = getMaskBit(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);
        if (kList.length == 0) {
            return;
        }
        if (!isUnsigned() && kList[0] == 31) { //there are negative numbers
            int sortMask = BitSorterUtils.getMaskBit(kList[0]);
            int finalLeft = IntSorterUtils.partitionReverseNotStable(list, start, end, sortMask);
            if (finalLeft - start > 1) {
                maskParts = getMaskBit(list, start, finalLeft);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsList(mask);
                sort(list, start, finalLeft, kList, 0);
            }
            if (end - finalLeft > 1) {
                maskParts = getMaskBit(list, finalLeft, end);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsList(mask);
                sort(list, finalLeft, end, kList, 0);
            }
        } else {
            sort(list, start, end, kList, 0);
        }

    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    public void sort(final int[] list, final int start, final int end, final int[] kList, final int kIndex) {
        int kDiff = kList.length - kIndex;
        if (kDiff < 1) {
            return;
        }
        int sortMask = getMaskBit(kList[kIndex]);
        int finalLeft = IntSorterUtils.partitionNotStable(list, start, end, sortMask);
        if (finalLeft - start > 1) {
            sort(list, start, finalLeft, kList, kIndex + 1);
        }
        if (end - finalLeft > 1) {
            sort(list, finalLeft, end, kList, kIndex + 1);
        }
    }


}
