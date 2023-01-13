package com.aldogg.sorter.longType;

import com.aldogg.sorter.*;

import static com.aldogg.sorter.MaskInfoLong.getMaskAsArray;
import static com.aldogg.sorter.longType.LongSorterUtils.*;

public abstract class LongBitMaskSorter implements LongSorter {

    protected boolean unsigned = false;

    protected BitSorterParams params = BitSorterParams.getSTParams();

    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    @Override
    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

    @Override
    public void sort(long[] array, int start, int end) {
        int n = end - start;
        if (n < 2) {
            return;
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, end) : listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            reverse(array, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        MaskInfoLong maskInfo = MaskInfoLong.getMaskBit(array, start, end);
        long mask = maskInfo.getMask();
        int[] kList = getMaskAsArray(mask);
        if (kList.length == 0) {
            return;
        }
//        setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        sort(array, start, end, kList);
    }
}
