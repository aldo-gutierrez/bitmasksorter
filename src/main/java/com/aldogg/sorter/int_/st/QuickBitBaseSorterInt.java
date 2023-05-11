package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.int_.IntBitMaskSorter;
import com.aldogg.sorter.int_.IntSorterUtils;

/**
 * Basic XXXSorter
 * It doesn't include a count sort
 * It doesn't include a comparator sort for small lists
 */
public class QuickBitBaseSorterInt extends IntBitMaskSorter {

    @Override
    public void sort(int[] array, int start, int endP1, int[] bList, Object multiThreadParams) {
        if (bList[0] == MaskInfoInt.UPPER_BIT) { //there are negative numbers
            int sortMask = 1 << bList[0];
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(array, start, endP1, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(array, start, endP1, sortMask);
            MaskInfoInt maskInfo;
            int mask;
            if (finalLeft - start > 1) {
                maskInfo = MaskInfoInt.calculateMask(array, start, finalLeft);
                mask = maskInfo.getMask();
                bList = MaskInfoInt.getMaskAsArray(mask);
                sort(array, start, finalLeft, bList, 0);
            }
            if (endP1 - finalLeft > 1) {
                maskInfo = MaskInfoInt.calculateMask(array, finalLeft, endP1);
                mask = maskInfo.getMask();
                bList = MaskInfoInt.getMaskAsArray(mask);
                sort(array, finalLeft, endP1, bList, 0);
            }
        } else {
            sort(array, start, endP1, bList, 0);
        }
    }

    public void sort(final int[] array, final int start, final int endP1, final int[] bList, final int kIndex) {
        int kDiff = bList.length - kIndex;
        if (kDiff < 1) {
            return;
        }
        int sortMask = 1 << bList[kIndex];
        int finalLeft = IntSorterUtils.partitionNotStable(array, start, endP1, sortMask);
        if (finalLeft - start > 1) {
            sort(array, start, finalLeft, bList, kIndex + 1);
        }
        if (endP1 - finalLeft > 1) {
            sort(array, finalLeft, endP1, bList, kIndex + 1);
        }
    }


}
