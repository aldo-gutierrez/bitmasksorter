package com.aldogg;

import static com.aldogg.BitSorterUtils.getMaskBit;
import static com.aldogg.BitSorterUtils.getMaskAsList;

public class RadixBitSorterInt extends RadixBitSorterUInt {

    @Override
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
        if (kList[0] == 31) { //there are negative numbers and positive numbers
            int sortMask = BitSorterUtils.getMaskBit(kList[0]);
            int finalLeft = IntSorterUtils.partitionReverse(list, start, end, sortMask);
            if (finalLeft - start > 1) { //sort negative numbers
                int[] aux = new int[finalLeft - start];
                maskParts = getMaskBit(list, start, finalLeft);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsList(mask);
                radixSort(list, start, finalLeft, aux, kList, kList.length - 1, 0);
            }
            if (end - finalLeft > 1) { //sort positive numbers
                int[] aux = new int[end - finalLeft];
                maskParts = getMaskBit(list, finalLeft, end);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsList(mask);
                radixSort(list, finalLeft, end, aux, kList, kList.length - 1, 0);
            }
        } else {
            int[] aux = new int[end - start];
            radixSort(list, start, end, aux, kList, kList.length - 1, 0);
        }
    }


}
