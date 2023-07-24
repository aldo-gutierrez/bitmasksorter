package com.aldogg.sorter.long_;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.BitSorterParams;
import com.aldogg.sorter.FieldSorterOptions;
import com.aldogg.sorter.MaskInfoLong;

import static com.aldogg.sorter.MaskInfoLong.getMaskAsArray;
import static com.aldogg.sorter.long_.SorterUtilsLong.*;

public abstract class BitMaskSorterLong implements SorterLong {


    protected BitSorterParams params = BitSorterParams.getSTParams();

    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    abstract public void sort(long[] array, int start, int endP1, int[] bList);

    @Override
    public void sort(long[] array, int start, int endP1) {
        FieldSorterOptions options = getFieldSorterOptions();
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        int ordered = options.isUnsigned() ? listIsOrderedUnSigned(array, start, endP1) : listIsOrderedSigned(array, start, endP1);
        if (ordered == AnalysisResult.DESCENDING) {
            reverse(array, start, endP1);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        MaskInfoLong maskInfo = MaskInfoLong.calculateMask(array, start, endP1);
        long mask = maskInfo.getMask();
        int[] bList = getMaskAsArray(mask);
        if (bList.length == 0) {
            return;
        }
//        setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        sort(array, start, endP1, bList);
    }
}
