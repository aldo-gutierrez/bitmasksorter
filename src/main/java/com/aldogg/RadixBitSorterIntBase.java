package com.aldogg;

import com.aldogg.intType.IntSorter;
import com.aldogg.intType.IntSorterUtils;

import static com.aldogg.BitSorterUtils.getMaskBit;
import static com.aldogg.BitSorterUtils.getMaskAsList;

public class RadixBitSorterIntBase implements IntSorter {

    boolean unsigned = false;

    @Override
    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

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
        if (!isUnsigned() && kList[0] == 31) { //there are negative numbers and positive numbers
            int sortMask = BitSorterUtils.getMaskBit(kList[0]);
            int finalLeft = IntSorterUtils.partitionReverseNotStable(list, start, end, sortMask);
            if (finalLeft - start > 1) { //sort negative numbers
                int[] aux = new int[finalLeft - start];
                maskParts = getMaskBit(list, start, finalLeft);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsList(mask);
                for (int i = kList.length - 1; i >= 0; i--) {
                    int sortMaskI = BitSorterUtils.getMaskBit(kList[i]);
                    IntSorterUtils.partitionStable(list, start, finalLeft, sortMaskI, aux);
                }
            }
            if (end - finalLeft > 1) { //sort positive numbers
                int[] aux = new int[end - finalLeft];
                maskParts = getMaskBit(list, finalLeft, end);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsList(mask);
                for (int i = kList.length - 1; i >= 0; i--) {
                    int sortMaskI = BitSorterUtils.getMaskBit(kList[i]);
                    IntSorterUtils.partitionStable(list, finalLeft, end, sortMaskI, aux);
                }
            }
        } else {
            int[] aux = new int[end - start];
            for (int i = kList.length - 1; i >= 0; i--) {
                int sortMask = BitSorterUtils.getMaskBit(kList[i]);
                IntSorterUtils.partitionStable(list, start, end, sortMask, aux);
            }
        }
    }

}
