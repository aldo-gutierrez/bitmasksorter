package com.aldogg.sorter.float_.st;

import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.MaskInfoLong;
import com.aldogg.sorter.float_.FloatBitMaskSorter;

import static com.aldogg.sorter.float_.FloatSorterUtils.*;
import static com.aldogg.sorter.MaskInfoInt.UPPER_BIT;

public class RadixBitBaseSorterFloat extends FloatBitMaskSorter {

    @Override
    public void sort(float[] array, int start, int endP1, int[] bList) {
        if (bList.length == 0) {
            return;
        }
        if (bList[0] == UPPER_BIT) { //there are negative numbers and positive numbers
            MaskInfoInt maskInfo;
            int mask;
            int sortMask = 1 << bList[0];
            int finalLeft = partitionReverseNotStable(array, start, endP1, sortMask);
            if (finalLeft - start > 1) { //sort negative numbers
                float[] aux = new float[finalLeft - start];
                maskInfo = MaskInfoInt.calculateMask(array, start, finalLeft);
                mask = maskInfo.getMask();
                bList = MaskInfoLong.getMaskAsArray(mask);
                for (int i = bList.length - 1; i >= 0; i--) {
                    int sortMaskI = 1 << bList[i];
                    partitionStable(array, start, finalLeft, sortMaskI, aux);
                }
                reverse(array, start, finalLeft);
            }
            if (endP1 - finalLeft > 1) { //sort positive numbers
                float[] aux = new float[endP1 - finalLeft];
                maskInfo = MaskInfoInt.calculateMask(array, finalLeft, endP1);
                mask = maskInfo.getMask();
                bList = MaskInfoLong.getMaskAsArray(mask);
                for (int i = bList.length - 1; i >= 0; i--) {
                    int sortMaskI = 1 << bList[i];
                    partitionStable(array, finalLeft, endP1, sortMaskI, aux);
                }
            }
        } else {
            float[] aux = new float[endP1 - start];
            for (int i = bList.length - 1; i >= 0; i--) {
                int sortMask = 1 << bList[i];
                partitionStable(array, start, endP1, sortMask, aux);
            }
            if (array[0] < 0) { //all negative numbers
                reverse(array, start, endP1);
            }
        }
    }
}
