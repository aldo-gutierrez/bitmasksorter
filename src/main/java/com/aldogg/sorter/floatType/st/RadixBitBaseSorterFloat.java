package com.aldogg.sorter.floatType.st;

import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.MaskInfoLong;
import com.aldogg.sorter.floatType.FloatBitMaskSorter;

import static com.aldogg.sorter.floatType.FloatSorterUtils.*;
import static com.aldogg.sorter.intType.IntSorter.SIGN_BIT_POS;

public class RadixBitBaseSorterFloat extends FloatBitMaskSorter {

    @Override
    public void sort(float[] array, int start, int end, int[] kList) {
        if (kList.length == 0) {
            return;
        }
        if (kList[0] == SIGN_BIT_POS) { //there are negative numbers and positive numbers
            MaskInfoInt maskInfo;
            int mask;
            int sortMask = 1 << kList[0];
            int finalLeft = partitionReverseNotStable(array, start, end, sortMask);
            if (finalLeft - start > 1) { //sort negative numbers
                float[] aux = new float[finalLeft - start];
                maskInfo = MaskInfoInt.getMaskBit(array, start, finalLeft);
                mask = maskInfo.getMask();
                kList = MaskInfoLong.getMaskAsArray(mask);
                for (int i = kList.length - 1; i >= 0; i--) {
                    int sortMaskI = 1 << kList[i];
                    partitionStable(array, start, finalLeft, sortMaskI, aux);
                }
                reverse(array, start, finalLeft);
            }
            if (end - finalLeft > 1) { //sort positive numbers
                float[] aux = new float[end - finalLeft];
                maskInfo = MaskInfoInt.getMaskBit(array, finalLeft, end);
                mask = maskInfo.getMask();
                kList = MaskInfoLong.getMaskAsArray(mask);
                for (int i = kList.length - 1; i >= 0; i--) {
                    int sortMaskI = 1 << kList[i];
                    partitionStable(array, finalLeft, end, sortMaskI, aux);
                }
            }
        } else {
            float[] aux = new float[end - start];
            for (int i = kList.length - 1; i >= 0; i--) {
                int sortMask = 1 << kList[i];
                partitionStable(array, start, end, sortMask, aux);
            }
            if (array[0] < 0) { //all negative numbers
                reverse(array, start, end);
            }
        }
    }
}
