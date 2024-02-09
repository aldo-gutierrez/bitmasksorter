package com.aldogg.sorter.float_;

import com.aldogg.sorter.shared.OrderAnalysisResult;
import com.aldogg.sorter.BitSorterParams;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;

import static com.aldogg.sorter.shared.int_mask.MaskInfoInt.getMaskAsArray;
import static com.aldogg.sorter.float_.SorterUtilsFloat.*;

public abstract class BitMaskSorterFloat implements SorterFloat {

    protected BitSorterParams params = BitSorterParams.getSTParams();

    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    abstract public void sort(float[] array, int start, int endP1, int[] bList);

    @Override
    public void sort(float[] array, int start, int endP1) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        int ordered = listIsOrderedSigned(array, start, endP1);
        if (ordered == OrderAnalysisResult.DESCENDING) {
            reverse(array, start, endP1);
        }
        if (ordered != OrderAnalysisResult.UNORDERED) return;

        MaskInfoInt maskInfo = MaskInfoInt.calculateMask(array, start, endP1);
        int mask = maskInfo.getMask();
        int[] bList = getMaskAsArray(mask);
        if (bList.length == 0) {
            return;
        }
//        setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        sort(array, start, endP1, bList);
    }
}
