package com.aldogg.sorter.generic;

import com.aldogg.sorter.MaskInfoLong;
import com.aldogg.sorter.long_.object.LongMapper;

import static com.aldogg.sorter.generic.SorterUtilsGenericLong.partitionNotStableUpperBit;
import static com.aldogg.sorter.generic.SorterUtilsGenericLong.partitionReverseNotStableUpperBit;

public class BitMaskSorterGenericLong<T> implements SorterObjectLong<T> {

    protected boolean unsigned = false;

    @Override
    public boolean isUnsigned() {
        return unsigned;
    }

    @Override
    public void sort(T[] array, LongMapper<T> mapper, int start, int endP1) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        MaskInfoLong maskInfo = MaskInfoLong.calculateMaskBreakIfUpperBit(array, start, endP1, null, mapper);
        if (maskInfo.isUpperBitMaskSet()) { //the sign bit is set
            int finalLeft = isUnsigned()
                    ? partitionNotStableUpperBit(array, start, endP1, mapper)
                    : partitionReverseNotStableUpperBit(array, start, endP1, mapper);
            int n1 = finalLeft - start;
            int n2 = endP1 - finalLeft;
            int[] bList1 = null;
            int[] bList2 = null;
            if (n1 > 1) { //sort negative numbers
                bList1 = MaskInfoLong.getMaskAsArray(MaskInfoLong.calculateMask(array, start, finalLeft, mapper).getMask());
                if (bList1.length <= 0) {
                    n1 = 0;
                }
            }
            if (n2 > 1) { //sort positive numbers
                bList2 = MaskInfoLong.getMaskAsArray(MaskInfoLong.calculateMask(array, finalLeft, endP1, mapper).getMask());
                if (bList2.length <= 0) {
                    n2 = 0;
                }
            }
            T[] aux = (T[]) new Object[Math.max(n1, n2)];
            if (n1 > 1) {
                sort(array, start, finalLeft, bList1, aux);
                if (isIee754()) {
                    SorterUtilsGeneric.reverse(array, start, finalLeft);
                }
            }
            if (n2 > 1) {
                sort(array, finalLeft, endP1, bList2, aux);
            }

        } else {
            T[] aux = (T[]) new Object[n];
            long mask = maskInfo.getMask();
            int[] bList = MaskInfoLong.getMaskAsArray(mask);
            if (bList.length > 0) {
                sort(array, start, endP1, bList, aux);
                if (isIee754()) {
                    if (mapper.value(array[0]) < 0L) {
                        SorterUtilsGeneric.reverse(array, start, endP1);
                    }
                }
            }
        }
    }

}
