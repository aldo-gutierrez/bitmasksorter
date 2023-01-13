package com.aldogg.sorter.floatType;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.BitSorterParams;
import com.aldogg.sorter.MaskInfoInt;

import static com.aldogg.sorter.MaskInfoInt.getMaskAsArray;
import static com.aldogg.sorter.floatType.FloatSorterUtils.*;

public class FloatBitMaskSorter implements FloatSorter {

    protected BitSorterParams params = BitSorterParams.getSTParams();

    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    @Override
    public void sort(float[] array, int start, int end) {
        int n = end - start;
        if (n < 2) {
            return;
        }
        int ordered = listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            reverse(array, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        MaskInfoInt maskInfo = MaskInfoInt.getMaskBit(array, start, end);
        int mask = maskInfo.getMask();
        int[] kList = getMaskAsArray(mask);
        if (kList.length == 0) {
            return;
        }
//        setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        sort(array, start, end, kList);
    }
}
