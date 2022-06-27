package com.aldogg.sorter.intType.st;

import com.aldogg.sorter.intType.IntBitMaskSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

import static com.aldogg.sorter.BitSorterUtils.*;

public class RadixBitBaseSorterInt extends IntBitMaskSorter {

    @Override
    public void sort(int[] array, int start, int end, int[] kList) {
        if (kList.length == 0) {
            return;
        }
        if (kList[0] == 31) { //there are negative numbers and positive numbers
            int[] maskParts;
            int mask;
            int sortMask = 1 << kList[0];
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
            if (finalLeft - start > 1) { //sort negative numbers
                int[] aux = new int[finalLeft - start];
                maskParts = getMaskBit(array, start, finalLeft);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsArray(mask);
                for (int i = kList.length - 1; i >= 0; i--) {
                    int sortMaskI = 1 << kList[i];
                    IntSorterUtils.partitionStable(array, start, finalLeft, sortMaskI, aux);
                }
            }
            if (end - finalLeft > 1) { //sort positive numbers
                int[] aux = new int[end - finalLeft];
                maskParts = getMaskBit(array, finalLeft, end);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsArray(mask);
                for (int i = kList.length - 1; i >= 0; i--) {
                    int sortMaskI = 1 << kList[i];
                    IntSorterUtils.partitionStable(array, finalLeft, end, sortMaskI, aux);
                }
            }
        } else {
            int[] aux = new int[end - start];
            for (int i = kList.length - 1; i >= 0; i--) {
                int sortMask = 1 << kList[i];
                IntSorterUtils.partitionStable(array, start, end, sortMask, aux);
            }
        }
    }

}
