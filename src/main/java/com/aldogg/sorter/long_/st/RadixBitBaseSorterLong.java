package com.aldogg.sorter.long_.st;

import com.aldogg.sorter.MaskInfoLong;
import com.aldogg.sorter.long_.LongBitMaskSorter;

import static com.aldogg.sorter.long_.LongSorterUtils.*;

public class RadixBitBaseSorterLong extends LongBitMaskSorter {

    @Override
    public void sort(long[] array, int start, int endP1, int[] kList) {
        if (kList.length == 0) {
            return;
        }
        if (kList[0] == LONG_SIGN_BIT_POS) { //there are negative numbers and positive numbers
            MaskInfoLong maskInfo;
            long mask;
            long sortMask = 1L << kList[0];
            int finalLeft = isUnsigned()
                    ? partitionNotStable(array, start, endP1, sortMask)
                    : partitionReverseNotStable(array, start, endP1, sortMask);
            if (finalLeft - start > 1) { //sort negative numbers
                long[] aux = new long[finalLeft - start];
                maskInfo = MaskInfoLong.getMaskBit(array, start, finalLeft);
                mask = maskInfo.getMask();
                kList = MaskInfoLong.getMaskAsArray(mask);
                for (int i = kList.length - 1; i >= 0; i--) {
                    long sortMaskI = 1L << kList[i];
                    partitionStable(array, start, finalLeft, sortMaskI, aux);
                }
            }
            if (endP1 - finalLeft > 1) { //sort positive numbers
                long[] aux = new long[endP1 - finalLeft];
                maskInfo = MaskInfoLong.getMaskBit(array, finalLeft, endP1);
                mask = maskInfo.getMask();
                kList = MaskInfoLong.getMaskAsArray(mask);
                for (int i = kList.length - 1; i >= 0; i--) {
                    long sortMaskI = 1L << kList[i];
                    partitionStable(array, finalLeft, endP1, sortMaskI, aux);
                }
            }
        } else {
            long[] aux = new long[endP1 - start];
            for (int i = kList.length - 1; i >= 0; i--) {
                long sortMask = 1L << kList[i];
                partitionStable(array, start, endP1, sortMask, aux);
            }
        }
    }
}
