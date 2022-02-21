package com.aldogg;

import static com.aldogg.BitSorterUtils.getMask;
import static com.aldogg.BitSorterUtils.getMaskAsList;

public class RadixBitSorterUInt extends QuickBitSorter2UInt {

    @Override
    public void sort(int[] list) {
        final int start = 0;
        final int end = list.length;
        int[] maskParts = getMask(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);
        int[] aux = new int[end - start];
        for (int i = kList.length - 1; i >= 0; i--) {
            int sortMask = getMask(kList[i]);
            IntSorterUtils.partitionStable(list, start, end, sortMask, aux);
        }
    }

}
