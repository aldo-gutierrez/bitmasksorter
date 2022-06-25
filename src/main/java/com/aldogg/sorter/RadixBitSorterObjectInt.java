package com.aldogg.sorter;

import com.aldogg.sorter.collection.IntComparator;
import com.aldogg.sorter.collection.ObjectSorter;
import com.aldogg.sorter.collection.ObjectSorterUtils;
import com.aldogg.sorter.intType.IntSorterUtils;

import static com.aldogg.sorter.BitSorterParams.MAX_BITS_RADIX_SORT;
import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.collection.ObjectSorterUtils.*;

public class RadixBitSorterObjectInt implements ObjectSorter {

    boolean unsigned = false;
    boolean stable = false;

    @Override
    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

    @Override
    public boolean isStable() {
        return stable;
    }

    @Override
    public void setStable(boolean stable) {
        this.stable = stable;
    }

    @Override
    public void sort(Object[] oArray, int start, int end, IntComparator comparator) {
        int n = end - start;
        if (n < 2) {
            return;
        }
        int[] array = new int[n];
        for (int i = 0; i < array.length; i++) {
            array[i] = comparator.intValue(oArray[i]);
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, end) : listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(array, start, end);
            ObjectSorterUtils.reverse(oArray, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        int[] maskParts = getMaskBit(array, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsArray(mask);
        if (kList.length == 0) { //all numbers are equal
            return;
        }
        sort(oArray, array, start, end, kList);
    }

    public void sort(Object[] oArray, int[] array, int start, int end, int[] kList) {
        if (kList[0] == 31) { //there are negative numbers and positive numbers
            int[] maskParts;
            int mask;
            int sortMask = 1 << kList[0];
            int finalLeft = isStable()
                    ? (isUnsigned()
                    ? ObjectSorterUtils.partitionStable(oArray, array, start, end, sortMask)
                    : ObjectSorterUtils.partitionReverseStable(oArray, array, start, end, sortMask))
                    : (isUnsigned()
                    ? ObjectSorterUtils.partitionNotStable(oArray, array, start, end, sortMask)
                    : ObjectSorterUtils.partitionReverseNotStable(oArray, array, start, end, sortMask));

            if (finalLeft - start > 1) { //sort negative numbers
                int[] aux = new int[finalLeft - start];
                Object[] oAux = new Object[finalLeft - start];
                maskParts = getMaskBit(array, start, finalLeft);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsArray(mask);
                radixSort(oArray, array, start, finalLeft, kList, 0, kList.length - 1, oAux, aux);
            }
            if (end - finalLeft > 1) { //sort positive numbers
                int[] aux = new int[end - finalLeft];
                Object[] oAux = new Object[end - finalLeft];
                maskParts = getMaskBit(array, finalLeft, end);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsArray(mask);
                radixSort(oArray, array, finalLeft, end, kList, 0, kList.length - 1, oAux, aux);
            }
        } else {
            int[] aux = new int[end - start];
            Object[] oAux = new Object[end - start];
            radixSort(oArray, array, start, end, kList, 0, kList.length - 1, oAux, aux);
        }
    }

    public static void radixSort(Object[] oArray, int[] array, int start, int end, int[] kList, int kStart, int kEnd, Object[] oAux, int[] aux) {
        for (int i = kEnd; i >= kStart; i--) {
            int kListI = kList[i];
            int maskI = 1 << kListI;
            int bits = 1;
            int imm = 0;
            for (int j = 1; j <= MAX_BITS_RADIX_SORT; j++) {
                if (i - j >= kStart) {
                    int kListIm1 = kList[i - j];
                    if (kListIm1 == kListI + j) {
                        maskI = maskI | 1 << kListIm1;
                        bits++;
                        imm++;
                    } else {
                        break;
                    }
                }
            }
            i -= imm;
            if (bits == 1) {
                partitionStable(oArray, array, start, end, maskI, oAux, aux);
            } else {
                int twoPowerK = 1 << bits;
                if (kListI == 0) {
                    partitionStableLastBits(oArray, array, start, end, maskI, twoPowerK, oAux, aux);
                } else {
                    partitionStableGroupBits(oArray, array, start, end, maskI, kListI, twoPowerK, oAux, aux);
                }
            }
        }
    }

}
