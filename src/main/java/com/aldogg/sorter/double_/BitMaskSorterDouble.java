package com.aldogg.sorter.double_;

import com.aldogg.sorter.shared.OrderAnalysisResult;
import com.aldogg.sorter.BitSorterParams;
import com.aldogg.sorter.shared.long_mask.MaskInfoLong;

import static com.aldogg.sorter.double_.SorterUtilsDouble.listIsOrderedSigned;
import static com.aldogg.sorter.double_.SorterUtilsDouble.reverse;

public abstract class BitMaskSorterDouble implements SorterDouble {

    protected BitSorterParams params = BitSorterParams.getSTParams();

    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    abstract public void sort(double[] array, int start, int endP1, int[] bList);
    @Override
    public void sort(double[] array, int start, int endP1) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        int ordered = listIsOrderedSigned(array, start, endP1);
        if (ordered == OrderAnalysisResult.DESCENDING) {
            reverse(array, start, endP1);
        }
        if (ordered != OrderAnalysisResult.UNORDERED) return;

        MaskInfoLong maskInfo = MaskInfoLong.calculateMask(array, start, endP1);
        long mask = maskInfo.getMask();
        int[] bList = MaskInfoLong.getMaskAsArray(mask);
        if (bList.length == 0) {
            return;
        }
//        setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        sort(array, start, endP1, bList);
    }
}

