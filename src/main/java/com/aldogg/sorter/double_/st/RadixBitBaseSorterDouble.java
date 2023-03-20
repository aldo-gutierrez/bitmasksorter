package com.aldogg.sorter.double_.st;

import com.aldogg.sorter.MaskInfoLong;
import com.aldogg.sorter.double_.DoubleBitMaskSorter;

import static com.aldogg.sorter.double_.DoubleSorterUtils.*;
import static com.aldogg.sorter.long_.LongSorter.LONG_SIGN_BIT_POS;

public class RadixBitBaseSorterDouble extends DoubleBitMaskSorter {

    @Override
    public void sort(double[] array, int start, int end, int[] kList) {
        if (kList.length == 0) {
            return;
        }
        if (kList[0] == LONG_SIGN_BIT_POS) { //there are negative numbers and positive numbers
            MaskInfoLong maskInfo;
            long mask;
            long sortMask = 1L << kList[0];
            int finalLeft = partitionReverseNotStable(array, start, end, sortMask);
            if (finalLeft - start > 1) { //sort negative numbers
                double[] aux = new double[finalLeft - start];
                maskInfo = MaskInfoLong.getMaskBit(array, start, finalLeft);
                mask = maskInfo.getMask();
                kList = MaskInfoLong.getMaskAsArray(mask);
                for (int i = kList.length - 1; i >= 0; i--) {
                    long sortMaskI = 1L << kList[i];
                    partitionStable(array, start, finalLeft, sortMaskI, aux);
                }
                reverse(array, start, finalLeft);
            }
            if (end - finalLeft > 1) { //sort positive numbers
                double[] aux = new double[end - finalLeft];
                maskInfo = MaskInfoLong.getMaskBit(array, finalLeft, end);
                mask = maskInfo.getMask();
                kList = MaskInfoLong.getMaskAsArray(mask);
                for (int i = kList.length - 1; i >= 0; i--) {
                    long sortMaskI = 1L << kList[i];
                    partitionStable(array, finalLeft, end, sortMaskI, aux);
                }
            }
        } else {
            double[] aux = new double[end - start];
            for (int i = kList.length - 1; i >= 0; i--) {
                long sortMask = 1L << kList[i];
                partitionStable(array, start, end, sortMask, aux);
            }
            if (array[0] < 0) { //all negative numbers
                reverse(array, start, end);
            }
        }
    }
}
