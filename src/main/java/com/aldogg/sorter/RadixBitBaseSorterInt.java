package com.aldogg.sorter;

import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

import static com.aldogg.sorter.BitSorterUtils.*;

public class RadixBitBaseSorterInt implements IntSorter {

    boolean unsigned = false;

    @Override
    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

    @Override
    public void sort(int[] array, int start, int end) {
        int n = end - start;
        if (n < 2) {
            return;
        }
        int[] maskParts = getMaskBit(array, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsArray(mask);
        sort(array, start, end, kList);
    }


    @Override
    public void sort(int[] array, int start, int end, int[] kList) {
        if (kList.length == 0) {
            return;
        }
        if (kList[0] == 31) { //there are negative numbers and positive numbers
            int[] maskParts;
            int mask;
            int sortMask = BitSorterUtils.getMaskBit(kList[0]);
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
            if (finalLeft - start > 1) { //sort negative numbers
                int[] aux = new int[finalLeft - start];
                maskParts = getMaskBit(array, start, finalLeft);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsArray(mask);
                for (int i = kList.length - 1; i >= 0; i--) {
                    int sortMaskI = BitSorterUtils.getMaskBit(kList[i]);
                    IntSorterUtils.partitionStable(array, start, finalLeft, sortMaskI, aux);
                }
            }
            if (end - finalLeft > 1) { //sort positive numbers
                int[] aux = new int[end - finalLeft];
                maskParts = getMaskBit(array, finalLeft, end);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsArray(mask);
                for (int i = kList.length - 1; i >= 0; i--) {
                    int sortMaskI = BitSorterUtils.getMaskBit(kList[i]);
                    IntSorterUtils.partitionStable(array, finalLeft, end, sortMaskI, aux);
                }
            }
        } else {
            int[] aux = new int[end - start];
            for (int i = kList.length - 1; i >= 0; i--) {
                int sortMask = BitSorterUtils.getMaskBit(kList[i]);
                IntSorterUtils.partitionStable(array, start, end, sortMask, aux);
            }
        }
    }

}
