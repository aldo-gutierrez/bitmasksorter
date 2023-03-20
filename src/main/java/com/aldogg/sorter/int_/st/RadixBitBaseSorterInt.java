package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.int_.IntSorter;
import com.aldogg.sorter.int_.IntSorterUtils;

import static com.aldogg.sorter.int_.IntSorterUtils.partitionStable;

public class RadixBitBaseSorterInt implements IntSorter {

    protected boolean unsigned = false;

    @Override
    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

    @Override
    public void sort(int[] array, int start, int end) {
        MaskInfoInt maskInfo = MaskInfoInt.getMaskBit(array, start, end);
        int mask = maskInfo.getMask();
        int[] kList = MaskInfoInt.getMaskAsArray(mask);
        if (kList.length == 0) {
            return;
        }
        if (kList[0] == SIGN_BIT_POS) { //there are negative numbers and positive numbers
            int sortMask = 1 << kList[0];
            int finalLeft = isUnsigned() ? IntSorterUtils.partitionNotStable(array, start, end, sortMask) : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
            if (finalLeft - start > 1) { //sort negative numbers
                int[] aux = new int[finalLeft - start];
                maskInfo = MaskInfoInt.getMaskBit(array, start, finalLeft);
                mask = maskInfo.getMask();
                kList = MaskInfoInt.getMaskAsArray(mask);
                for (int i = kList.length - 1; i >= 0; i--) {
                    int sortMaskI = 1 << kList[i];
                    partitionStable(array, start, finalLeft, sortMaskI, aux);
                }
            }
            if (end - finalLeft > 1) { //sort positive numbers
                int[] aux = new int[end - finalLeft];
                maskInfo = MaskInfoInt.getMaskBit(array, finalLeft, end);
                mask = maskInfo.getMask();
                kList = MaskInfoInt.getMaskAsArray(mask);
                for (int i = kList.length - 1; i >= 0; i--) {
                    int sortMaskI = 1 << kList[i];
                    partitionStable(array, finalLeft, end, sortMaskI, aux);
                }
            }
        } else {
            int[] aux = new int[end - start];
            for (int i = kList.length - 1; i >= 0; i--) {
                int sortMask = 1 << kList[i];
                partitionStable(array, start, end, sortMask, aux);
            }
        }
    }

}
