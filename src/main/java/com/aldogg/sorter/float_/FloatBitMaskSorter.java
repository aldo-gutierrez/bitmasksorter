package com.aldogg.sorter.float_;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.BitSorterParams;
import com.aldogg.sorter.MaskInfoInt;

import static com.aldogg.sorter.MaskInfoInt.getMaskAsArray;
import static com.aldogg.sorter.float_.FloatSorterUtils.*;

public class FloatBitMaskSorter implements FloatSorter {

    protected BitSorterParams params = BitSorterParams.getSTParams();

    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    @Override
    public void sort(float[] array, int start, int endP1) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        int ordered = listIsOrderedSigned(array, start, endP1);
        if (ordered == AnalysisResult.DESCENDING) {
            reverse(array, start, endP1);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        MaskInfoInt maskInfo = MaskInfoInt.getMaskBit(array, start, endP1);
        int mask = maskInfo.getMask();
        int[] kList = getMaskAsArray(mask);
        if (kList.length == 0) {
            return;
        }
//        setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        sort(array, start, endP1, kList);
    }
}
