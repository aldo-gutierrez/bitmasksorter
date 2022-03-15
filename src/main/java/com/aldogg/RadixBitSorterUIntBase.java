package com.aldogg;

import com.aldogg.intType.IntSorter;
import com.aldogg.intType.IntSorterUtils;

import static com.aldogg.BitSorterUtils.getMaskBit;
import static com.aldogg.BitSorterUtils.getMaskAsList;

public class RadixBitSorterUIntBase implements IntSorter {

    @Override
    public void sort(int[] list) {
        final int start = 0;
        final int end = list.length;
        int[] maskParts = getMaskBit(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);
        int[] aux = new int[end - start];
        for (int i = kList.length - 1; i >= 0; i--) {
            int sortMask = BitSorterUtils.getMaskBit(kList[i]);
            IntSorterUtils.partitionStable(list, start, end, sortMask, aux);
        }
    }

}
