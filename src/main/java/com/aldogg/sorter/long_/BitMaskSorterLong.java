package com.aldogg.sorter.long_;

import com.aldogg.sorter.FieldSortOptions;
import com.aldogg.sorter.shared.OrderAnalysisResult;
import com.aldogg.sorter.BitSorterParams;
import com.aldogg.sorter.shared.long_mask.MaskInfoLong;

import static com.aldogg.sorter.shared.FieldType.UNSIGNED_INTEGER;
import static com.aldogg.sorter.shared.long_mask.MaskInfoLong.getMaskAsArray;
import static com.aldogg.sorter.long_.SorterUtilsLong.*;

public abstract class BitMaskSorterLong implements SorterLong {


    protected BitSorterParams params = BitSorterParams.getSTParams();

    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    abstract public void sort(long[] array, int start, int endP1, FieldSortOptions options, int[] bList);

    @Override
    public void sort(long[] array, int start, int endP1, FieldSortOptions options) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        int ordered = options.getFieldType().equals(UNSIGNED_INTEGER) ? listIsOrderedUnSigned(array, start, endP1) : listIsOrderedSigned(array, start, endP1);
        if (ordered == OrderAnalysisResult.DESCENDING) {
            reverse(array, start, endP1);
        }
        if (ordered != OrderAnalysisResult.UNORDERED) return;

        MaskInfoLong maskInfo = MaskInfoLong.calculateMask(array, start, endP1);
        long mask = maskInfo.getMask();
        int[] bList = getMaskAsArray(mask);
        if (bList.length == 0) {
            return;
        }
        sort(array, start, endP1, options, bList);
    }
}
