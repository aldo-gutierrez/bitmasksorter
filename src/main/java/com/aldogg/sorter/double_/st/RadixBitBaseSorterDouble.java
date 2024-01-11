package com.aldogg.sorter.double_.st;

import com.aldogg.sorter.shared.long_mask.MaskInfoLong;
import com.aldogg.sorter.double_.BitMaskSorterDouble;

import static com.aldogg.sorter.double_.SorterUtilsDouble.*;
import static com.aldogg.sorter.shared.long_mask.MaskInfoLong.UPPER_BIT;

public class RadixBitBaseSorterDouble extends BitMaskSorterDouble {

    @Override
    public void sort(double[] array, int start, int endP1, int[] bList) {
        if (bList.length == 0) {
            return;
        }
        if (bList[0] == UPPER_BIT) { //there are negative numbers and positive numbers
            MaskInfoLong maskInfo;
            long mask;
            long sortMask = 1L << bList[0];
            int finalLeft = partitionReverseNotStable(array, start, endP1, sortMask);
            if (finalLeft - start > 1) { //sort negative numbers
                double[] aux = new double[finalLeft - start];
                maskInfo = MaskInfoLong.calculateMask(array, start, finalLeft);
                mask = maskInfo.getMask();
                bList = MaskInfoLong.getMaskAsArray(mask);
                for (int i = bList.length - 1; i >= 0; i--) {
                    long sortMaskI = 1L << bList[i];
                    partitionStable(array, start, finalLeft, sortMaskI, aux);
                }
                reverse(array, start, finalLeft);
            }
            if (endP1 - finalLeft > 1) { //sort positive numbers
                double[] aux = new double[endP1 - finalLeft];
                maskInfo = MaskInfoLong.calculateMask(array, finalLeft, endP1);
                mask = maskInfo.getMask();
                bList = MaskInfoLong.getMaskAsArray(mask);
                for (int i = bList.length - 1; i >= 0; i--) {
                    long sortMaskI = 1L << bList[i];
                    partitionStable(array, finalLeft, endP1, sortMaskI, aux);
                }
            }
        } else {
            double[] aux = new double[endP1 - start];
            for (int i = bList.length - 1; i >= 0; i--) {
                long sortMask = 1L << bList[i];
                partitionStable(array, start, endP1, sortMask, aux);
            }
            if (array[0] < 0) { //all negative numbers
                reverse(array, start, endP1);
            }
        }
    }
}
