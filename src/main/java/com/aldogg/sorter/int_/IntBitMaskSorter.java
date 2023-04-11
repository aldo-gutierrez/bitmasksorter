package com.aldogg.sorter.int_;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.BitSorterParams;
import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.SortingNetworks;

import java.util.function.BiConsumer;

import static com.aldogg.sorter.int_.IntSorterUtils.listIsOrderedSigned;
import static com.aldogg.sorter.int_.IntSorterUtils.listIsOrderedUnSigned;

public abstract class IntBitMaskSorter implements IntSorter {

    protected boolean unsigned = false;

    protected BiConsumer<int[], Integer>[] snFunctions;

    protected BitSorterParams params = BitSorterParams.getSTParams();

    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    public void setSNFunctions(BiConsumer<int[], Integer>[] snFunctions) {
        this.snFunctions = snFunctions;
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

        setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);

        MaskInfoInt maskInfo = MaskInfoInt.getMaskBitDetectSignBit(array, start, end, null);
        if (maskInfo == null || (maskInfo.getMask() & 0x80000000) != 0) { //the sign bit is set
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStableSignBit(array, start, end)
                    : IntSorterUtils.partitionReverseNotStableSignBit(array, start, end);
            int n1 = finalLeft - start;
            int n2 = end - finalLeft;
            if (n1 > 1) { //sort negative numbers
                maskInfo = MaskInfoInt.getMaskBit(array, start, finalLeft);
                int mask = maskInfo.getMask();
                int[] kList = MaskInfoInt.getMaskAsArray(mask);
                if (kList.length > 0) {
                    sort(array, start, finalLeft, kList, null); //aux
                }
            }
            if (n2 > 1) { //sort positive numbers
                maskInfo = MaskInfoInt.getMaskBit(array, finalLeft, end);
                int mask = maskInfo.getMask();
                int[] kList = MaskInfoInt.getMaskAsArray(mask);
                if (kList.length > 0) {
                    sort(array, finalLeft, end, kList, null);
                }
            }
        } else {
            int mask = maskInfo.getMask();
            int[] kList = MaskInfoInt.getMaskAsArray(mask);
            if (kList.length > 0) {
                sort(array, start, end, kList, null);
            }
        }
    }

}
