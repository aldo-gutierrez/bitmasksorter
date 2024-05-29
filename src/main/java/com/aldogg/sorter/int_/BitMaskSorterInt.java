package com.aldogg.sorter.int_;

import com.aldogg.sorter.*;
import com.aldogg.sorter.shared.FieldType;
import com.aldogg.sorter.shared.OrderAnalysisResult;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;

import static com.aldogg.sorter.int_.SorterUtilsInt.listIsOrderedSigned;
import static com.aldogg.sorter.int_.SorterUtilsInt.listIsOrderedUnSigned;

public abstract class BitMaskSorterInt implements SorterInt {

    protected BitSorterParams params = BitSorterParams.getSTParams();

    public void setParams(BitSorterParams params) {
        this.params = params;
    }


    public abstract void sort(int[] array, int start, int endP1, FieldOptions options, int[] bList, Object params);
    @Override
    public void sort(int[] array, int start, int endP1, FieldOptions options) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        int ordered = options.getFieldType().equals(FieldType.UNSIGNED_INTEGER) ? listIsOrderedUnSigned(array, start, endP1) : listIsOrderedSigned(array, start, endP1);
        if (ordered == OrderAnalysisResult.DESCENDING) {
            SorterUtilsInt.reverse(array, start, endP1);
        }
        if (ordered != OrderAnalysisResult.UNORDERED) return;

        MaskInfoInt maskInfo = MaskInfoInt.calculateMaskBreakIfUpperBit(array, start, endP1, null);
        if (maskInfo.isUpperBitMaskSet()) { //the sign bit is set
            int finalLeft = options.getFieldType().equals(FieldType.UNSIGNED_INTEGER)
                    ? SorterUtilsIntExt.partitionNotStableUpperBit(array, start, endP1)
                    : SorterUtilsIntExt.partitionReverseNotStableUpperBit(array, start, endP1);
            int n1 = finalLeft - start;
            int n2 = endP1 - finalLeft;
            int[] bList1 = null;
            int[] bList2 = null;
            if (n1 > 1) { //sort negative numbers
                bList1 = MaskInfoInt.getMaskAsArray(MaskInfoInt.calculateMask(array, start, finalLeft).getMask());
                if (bList1.length <= 0) {
                    n1 = 0;
                }
            }
            if (n2 > 1) { //sort positive numbers
                bList2 = MaskInfoInt.getMaskAsArray(MaskInfoInt.calculateMask(array, finalLeft, endP1).getMask());
                if (bList2.length <= 0) {
                    n2 = 0;
                }
            }
            int[] aux = new int[Math.max(n1, n2)];
            if (n1 > 1) {
                sort(array, start, finalLeft, options, bList1, aux);
            }
            if (n2 > 1) {
                sort(array, finalLeft, endP1, options, bList2, aux);
            }

        } else {
            int[] aux = new int[n];
            int mask = maskInfo.getMask();
            int[] bList = MaskInfoInt.getMaskAsArray(mask);
            if (bList.length > 0) {
                sort(array, start, endP1, options, bList, aux);
            }
        }
    }

}
