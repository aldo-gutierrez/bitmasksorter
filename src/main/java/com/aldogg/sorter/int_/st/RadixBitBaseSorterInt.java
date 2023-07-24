package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.FieldSorterOptions;
import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.int_.SorterInt;
import com.aldogg.sorter.int_.SorterUtilsInt;

import java.lang.reflect.Field;

import static com.aldogg.sorter.int_.SorterUtilsInt.partitionStable;

public class RadixBitBaseSorterInt implements SorterInt {

    @Override
    public void sort(int[] array, int start, int endP1) {
        FieldSorterOptions options = getFieldSorterOptions();
        MaskInfoInt maskInfo = MaskInfoInt.calculateMask(array, start, endP1);
        int mask = maskInfo.getMask();
        int[] bList = MaskInfoInt.getMaskAsArray(mask);
        if (bList.length == 0) {
            return;
        }
        if (bList[0] == MaskInfoInt.UPPER_BIT) { //there are negative numbers and positive numbers
            int sortMask = 1 << bList[0];
            int finalLeft = options.isUnsigned() ? SorterUtilsInt.partitionNotStable(array, start, endP1, sortMask) : SorterUtilsInt.partitionReverseNotStable(array, start, endP1, sortMask);
            if (finalLeft - start > 1) { //sort negative numbers
                int[] aux = new int[finalLeft - start];
                maskInfo = MaskInfoInt.calculateMask(array, start, finalLeft);
                mask = maskInfo.getMask();
                bList = MaskInfoInt.getMaskAsArray(mask);
                for (int i = bList.length - 1; i >= 0; i--) {
                    int sortMaskI = 1 << bList[i];
                    partitionStable(array, start, finalLeft, sortMaskI, aux);
                }
            }
            if (endP1 - finalLeft > 1) { //sort positive numbers
                int[] aux = new int[endP1 - finalLeft];
                maskInfo = MaskInfoInt.calculateMask(array, finalLeft, endP1);
                mask = maskInfo.getMask();
                bList = MaskInfoInt.getMaskAsArray(mask);
                for (int i = bList.length - 1; i >= 0; i--) {
                    int sortMaskI = 1 << bList[i];
                    partitionStable(array, finalLeft, endP1, sortMaskI, aux);
                }
            }
        } else {
            int[] aux = new int[endP1 - start];
            for (int i = bList.length - 1; i >= 0; i--) {
                int sortMask = 1 << bList[i];
                partitionStable(array, start, endP1, sortMask, aux);
            }
        }
    }

}
