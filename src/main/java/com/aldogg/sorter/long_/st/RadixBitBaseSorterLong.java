package com.aldogg.sorter.long_.st;

import com.aldogg.sorter.MaskInfoLong;
import com.aldogg.sorter.long_.LongBitMaskSorter;

import static com.aldogg.sorter.long_.LongSorterUtils.*;

public class RadixBitBaseSorterLong extends LongBitMaskSorter {

    @Override
    public void sort(long[] array, int start, int endP1, int[] bList) {
        if (bList.length == 0) {
            return;
        }
        if (bList[0] == MaskInfoLong.UPPER_BIT) { //there are negative numbers and positive numbers
            MaskInfoLong maskInfo;
            long mask;
            long sortMask = 1L << bList[0];
            int finalLeft = isUnsigned()
                    ? partitionNotStable(array, start, endP1, sortMask)
                    : partitionReverseNotStable(array, start, endP1, sortMask);
            if (finalLeft - start > 1) { //sort negative numbers
                long[] aux = new long[finalLeft - start];
                maskInfo = MaskInfoLong.calculateMask(array, start, finalLeft);
                mask = maskInfo.getMask();
                bList = MaskInfoLong.getMaskAsArray(mask);
                for (int i = bList.length - 1; i >= 0; i--) {
                    long sortMaskI = 1L << bList[i];
                    partitionStable(array, start, finalLeft, sortMaskI, aux);
                }
            }
            if (endP1 - finalLeft > 1) { //sort positive numbers
                long[] aux = new long[endP1 - finalLeft];
                maskInfo = MaskInfoLong.calculateMask(array, finalLeft, endP1);
                mask = maskInfo.getMask();
                bList = MaskInfoLong.getMaskAsArray(mask);
                for (int i = bList.length - 1; i >= 0; i--) {
                    long sortMaskI = 1L << bList[i];
                    partitionStable(array, finalLeft, endP1, sortMaskI, aux);
                }
            }
        } else {
            long[] aux = new long[endP1 - start];
            for (int i = bList.length - 1; i >= 0; i--) {
                long sortMask = 1L << bList[i];
                partitionStable(array, start, endP1, sortMask, aux);
            }
        }
    }
}
