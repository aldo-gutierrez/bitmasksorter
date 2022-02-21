package com.aldogg;

import static com.aldogg.BitSorterUtils.getMask;
import static com.aldogg.BitSorterUtils.getMaskAsList;

public class RadixBitSorterInt extends RadixBitSorter2UInt {

    @Override
    public void sort(int[] list) {
        if (list.length < 2) {
            return;
        }
        final int start = 0;
        final int end = list.length;
        int[] maskParts = getMask(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);
        if (kList.length == 0) {
            return;
        }
        if (kList[0] == 31) { //there are negative numbers and positive numbers
            int sortMask = getMask(kList[0]);
            int finalLeft = IntSorterUtils.partitionReverse(list, start, end, sortMask);
            int[] aux = new int[end - start];
            if (finalLeft - start > 1) { //sort negative numbers
                radixSort(list, start, finalLeft, aux, kList, kList.length - 1, 1);
            }
            if (end - finalLeft > 1) { //sort positive numbers
                radixSort(list, finalLeft, end, aux, kList, kList.length - 1, 1);
            }
        } else {
            int[] aux = new int[end - start];
            radixSort(list, start, end, aux, kList, kList.length - 1, 0);
        }
    }


}
