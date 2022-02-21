package com.aldogg;

import static com.aldogg.BitSorterUtils.getMask;
import static com.aldogg.BitSorterUtils.getMaskAsList;

public class QuickBitSorterInt extends QuickBitSorter3UInt implements IntSorter {

    @Override
    public void sort(int[] list) {
        final int start = 0;
        final int end = list.length;
        if (list.length <= 1) {
            return;
        }
        int[] maskParts = getMask(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);
        if (kList.length == 0) {
            return;
        } else if (kList[0] == 31) { //there are negative numbers
            int sortMask = getMask(kList[0]);
            int finalLeft = IntSorterUtils.partitionReverse(list, start, end, sortMask);
            if (finalLeft - start > 1) {
                sort(list, start, finalLeft, kList, 1, false);
            }
            if (end - finalLeft > 1) {
                sort(list, finalLeft, end, kList, 1, false);
            }
        } else {
            if (kList.length <= params.getCountingSortBits()) {
                CountSort.countSort(list, start, end, kList, 0);
            } else {
                sort(list, start, end, kList, 0);
            }
        }
    }
}