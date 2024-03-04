package com.aldogg.sorter.generic;

import com.aldogg.sorter.FieldOptions;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;
import com.aldogg.sorter.int_.object.IntMapper;

import static com.aldogg.sorter.generic.SorterUtilsGenericInt.partitionNotStableUpperBit;
import static com.aldogg.sorter.generic.SorterUtilsGenericInt.partitionReverseNotStableUpperBit;

public abstract class BitMaskSorterGenericInt<T> implements SorterObjectInt<T> {

    FieldOptions options;

    abstract public void sortNNA(T[] array, int start, int endP1, int[] bList, Object params);

    @Override
    public void sortNNA(T[] array, int start, int endP1, IntMapper<T> mapper) {
        options = mapper;
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        MaskInfoInt maskInfo = MaskInfoInt.calculateMaskBreakIfUpperBit(array, start, endP1, null, mapper);
        if (maskInfo.isUpperBitMaskSet()) { //the sign bit is set
            int finalLeft = options.isUnsigned()
                    ? partitionNotStableUpperBit(array, start, endP1, mapper)
                    : partitionReverseNotStableUpperBit(array, start, endP1, mapper);
            int n1 = finalLeft - start;
            int n2 = endP1 - finalLeft;
            int[] bList1 = null;
            int[] bList2 = null;
            if (n1 > 1) { //sort negative numbers
                bList1 = MaskInfoInt.getMaskAsArray(MaskInfoInt.calculateMask(array, start, finalLeft, mapper).getMask());
                if (bList1.length <= 0) {
                    n1 = 0;
                }
            }
            if (n2 > 1) { //sort positive numbers
                bList2 = MaskInfoInt.getMaskAsArray(MaskInfoInt.calculateMask(array, finalLeft, endP1, mapper).getMask());
                if (bList2.length <= 0) {
                    n2 = 0;
                }
            }
            T[] aux = (T[]) new Object[Math.max(n1, n2)];
            if (n1 > 1) {
                sortNNA(array, start, finalLeft, bList1, new Object[]{aux, mapper});
                if (options.isIeee754()) {
                    SorterUtilsGeneric.reverse(array, start, finalLeft);
                }
            }
            if (n2 > 1) {
                sortNNA(array, finalLeft, endP1, bList2, new Object[]{aux, mapper});
            }

        } else {
            T[] aux = (T[]) new Object[n];
            int mask = maskInfo.getMask();
            int[] bList = MaskInfoInt.getMaskAsArray(mask);
            if (bList.length > 0) {
                sortNNA(array, start, endP1, bList, new Object[]{aux, mapper});
                if (options.isIeee754()) {
                    if (mapper.value(array[0]) < 0) {
                        SorterUtilsGeneric.reverse(array, start, endP1);
                    }
                }
            }
        }
    }
}
