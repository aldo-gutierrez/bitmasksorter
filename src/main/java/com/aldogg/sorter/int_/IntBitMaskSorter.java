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

    protected int auxSize = 0;

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
            int[] bList1 = null;
            int[] bList2 = null;
            if (n1 > 1) { //sort negative numbers
                MaskInfoInt maskInfo1 = MaskInfoInt.calculateMask(array, start, finalLeft);
                bList1 = MaskInfoInt.getMaskAsArray(maskInfo1.getMask());
                if (bList1.length <= 0) {
                    n1 = 0;
                }
            }
            if (n2 > 1) { //sort positive numbers
                MaskInfoInt maskInfo2 = MaskInfoInt.calculateMask(array, finalLeft, endP1);
                bList2 = MaskInfoInt.getMaskAsArray(maskInfo2.getMask());
                if (bList2.length <= 0) {
                    n2 = 0;
                }
            }
            auxSize = Math.max(n1, n2);
            if (n1 > 1) {
                sort(array, start, finalLeft, bList1, null);
            }
            if (n2 > 1) {
                sort(array, finalLeft, endP1, bList2, null);
            }

        } else {
            auxSize = n;
            int mask = maskInfo.getMask();
            int[] bList = MaskInfoInt.getMaskAsArray(mask);
            if (bList.length > 0) {
                sort(array, start, endP1, bList, null);
            }
        }
    }

}
