package com.aldogg.sorter.doubleType;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.BitSorterParams;
import com.aldogg.sorter.MaskInfoLong;

import static com.aldogg.sorter.doubleType.DoubleSorterUtils.listIsOrderedSigned;
import static com.aldogg.sorter.doubleType.DoubleSorterUtils.reverse;

public class DoubleBitMaskSorter implements DoubleSorter {

    protected BitSorterParams params = BitSorterParams.getSTParams();

    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    @Override
    public void sort(double[] array, int start, int end) {
        int n = end - start;
        if (n < 2) {
            return;
        }
        int ordered = listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            reverse(array, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        MaskInfoLong maskInfo = MaskInfoLong.getMaskBit(array, start, end);
        long mask = maskInfo.getMask();
        int[] kList = MaskInfoLong.getMaskAsArray(mask);
        if (kList.length == 0) {
            return;
        }
//        setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        sort(array, start, end, kList);
    }
}

