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
    public void sort(int[] array, int start, int endP1, int[] kList, Object multiThreadParams) {
        if (kList[0] == SIGN_BIT_POS) { //there are negative numbers
            int sortMask = 1 << kList[0];
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(array, start, endP1, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(array, start, endP1, sortMask);
            MaskInfoInt maskInfo;
            int mask;
            if (finalLeft - start > 1) {
                maskInfo = MaskInfoInt.getMaskBit(array, start, finalLeft);
                mask = maskInfo.getMask();
                kList = MaskInfoInt.getMaskAsArray(mask);
                sort(array, start, finalLeft, kList, 0);
            }
            if (endP1 - finalLeft > 1) {
                maskInfo = MaskInfoInt.getMaskBit(array, finalLeft, endP1);
                mask = maskInfo.getMask();
                kList = MaskInfoInt.getMaskAsArray(mask);
                sort(array, finalLeft, endP1, kList, 0);
            }
        } else {
            sort(array, start, endP1, kList, 0);
        }
    }

    public void sort(final int[] array, final int start, final int endP1, final int[] kList, final int kIndex) {
        int kDiff = kList.length - kIndex;
        if (kDiff < 1) {
            return;
        }
        int sortMask = 1 << kList[kIndex];
        int finalLeft = IntSorterUtils.partitionNotStable(array, start, endP1, sortMask);
        if (finalLeft - start > 1) {
            sort(array, start, finalLeft, kList, kIndex + 1);
        }
        if (endP1 - finalLeft > 1) {
            sort(array, finalLeft, endP1, kList, kIndex + 1);
        }
    }


}
