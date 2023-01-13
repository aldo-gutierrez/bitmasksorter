package com.aldogg.sorter.intType;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.BitSorterParams;
import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.SortingNetworks;

import java.util.function.BiConsumer;

import static com.aldogg.sorter.MaskInfoInt.getMaskAsArray;
import static com.aldogg.sorter.intType.IntSorterUtils.listIsOrderedSigned;
import static com.aldogg.sorter.intType.IntSorterUtils.listIsOrderedUnSigned;

public abstract class IntBitMaskSorter implements IntSorter{

    protected boolean unsigned = false;

    protected BiConsumer<int[], Integer>[] snFunctions;

    protected BitSorterParams params = BitSorterParams.getSTParams();

    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    public void setSNFunctions(BiConsumer<int[], Integer>[] snFunctions) {
        this.snFunctions =snFunctions;
    }

    @Override
    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

    @Override
    public void sort(int[] array, int start, int end) {
        int n = end - start;
        if (n < 2) {
            return;
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, end) : listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(array, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        MaskInfoInt maskInfo = MaskInfoInt.getMaskBit(array, start, end);
        int mask = maskInfo.getMask();
        int[] kList = getMaskAsArray(mask);
        if (kList.length == 0) {
            return;
        }
        setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        sort(array, start, end, kList);
    }
}
