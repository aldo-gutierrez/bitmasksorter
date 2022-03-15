package com.aldogg;

import com.aldogg.intType.IntSorter;
import com.aldogg.intType.IntSorterUtils;

import static com.aldogg.BitSorterUtils.*;

/**
 * Basic XXXSorter
 * It doesn't include a count sort it
 * It doesn't include a comparator sort for small lists
 */
public class QuickBitSorterUIntBase implements IntSorter {

    public void sort(int[] list) {
        final int start = 0;
        final int end = list.length;
        final int listLength = end - start;
        if (listLength < 2) {
            return;
        }
        int[] maskParts = getMaskBit(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] listK = getMaskAsList(mask);
        if (listK.length > 0) {
            sort(list, start, end, listK, 0);
        }
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    public void sort(final int[] list, final int start, final int end, final int[] kList, final int kIndex) {
        if (kIndex > kList.length - 1) {
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
