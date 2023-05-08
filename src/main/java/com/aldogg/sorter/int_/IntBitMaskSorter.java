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
    public void sort(int[] array, int start, int endP1) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, endP1) : listIsOrderedSigned(array, start, endP1);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(array, start, endP1);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);

        MaskInfoInt maskInfo = MaskInfoInt.calculateMaskBreakIfUpperBit(array, start, endP1, null);
        if (maskInfo == null || (maskInfo.getMask() & 0x80000000) != 0) { //the sign bit is set
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStableUpperBit(array, start, endP1)
                    : IntSorterUtils.partitionReverseNotStableUpperBit(array, start, endP1);
            int n1 = finalLeft - start;
            int n2 = endP1 - finalLeft;
            if (n1 > 1) { //sort negative numbers
                maskInfo = MaskInfoInt.calculateMask(array, start, finalLeft);
                int mask = maskInfo.getMask();
                int[] kList = MaskInfoInt.getMaskAsArray(mask);
                if (kList.length > 0) {
                    sort(array, start, finalLeft, kList, null); //aux
                }
            }
            if (n2 > 1) { //sort positive numbers
                maskInfo = MaskInfoInt.calculateMask(array, finalLeft, endP1);
                int mask = maskInfo.getMask();
                int[] kList = MaskInfoInt.getMaskAsArray(mask);
                if (kList.length > 0) {
                    sort(array, finalLeft, endP1, kList, null);
                }
            }
        } else {
            int mask = maskInfo.getMask();
            int[] kList = MaskInfoInt.getMaskAsArray(mask);
            if (kList.length > 0) {
                sort(array, start, endP1, kList, null);
            }
        }
    }

}
