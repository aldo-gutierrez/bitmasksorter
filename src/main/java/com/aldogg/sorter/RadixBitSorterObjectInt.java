package com.aldogg.sorter;

import com.aldogg.sorter.collection.IntComparator;
import com.aldogg.sorter.collection.ObjectSorter;
import com.aldogg.sorter.collection.ObjectSorterUtils;
import com.aldogg.sorter.intType.IntSorterUtils;

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
    public void sort(Object[] array, IntComparator comparator) {
        if (array.length < 2) {
            return;
        }
        final int start = 0;
        final int end = array.length;
        int[] list = new int[array.length];
        for (int i = 0; i < list.length; i++) {
            list[i] = comparator.intValue(array[i]);
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(list, start, end) : listIsOrderedSigned(list, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(list, start, end);
            ObjectSorterUtils.reverse(array, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        int[] maskParts = getMaskBit(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsArray(mask);
        if (kList.length == 0) { //all numbers are equal
            return;
        }

        if (kList[0] == 31) { //there are negative numbers and positive numbers
            int sortMask = BitSorterUtils.getMaskBit(kList[0]);
            int finalLeft = isStable()
                    ? (isUnsigned()
                    ? ObjectSorterUtils.partitionStable(array, list, start, end, sortMask)
                    : ObjectSorterUtils.partitionReverseStable(array, list, start, end, sortMask))
                    : (isUnsigned()
                    ? ObjectSorterUtils.partitionNotStable(array, list, start, end, sortMask)
                    : ObjectSorterUtils.partitionReverseNotStable(array, list, start, end, sortMask));

            if (finalLeft - start > 1) { //sort negative numbers
                int[] aux = new int[finalLeft - start];
                Object[] oAux = new Object[finalLeft - start];
                maskParts = getMaskBit(list, start, finalLeft);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsArray(mask);
                radixSort(array, list, start, finalLeft, kList, 0, kList.length - 1, oAux, aux);
            }
            if (end - finalLeft > 1) { //sort positive numbers
                int[] aux = new int[end - finalLeft];
                Object[] oAux = new Object[end - finalLeft];
                maskParts = getMaskBit(list, finalLeft, end);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsArray(mask);
                radixSort(array, list, finalLeft, end, kList, 0, kList.length - 1, oAux, aux);
            }
        } else {
            int[] aux = new int[end - start];
            Object[] oAux = new Object[end - start];
            radixSort(array, list, start, end, kList, 0, kList.length - 1, oAux, aux);
        }
    }

    public static void radixSort(Object[] oArray, int[] array, int start, int end, int[] kList, int kIndexEnd, int kIndexStart, Object[] oAux, int[] aux) {
        for (int i = kIndexStart; i >= kIndexEnd; i--) {
            int kListI = kList[i];
            int sortMask1 = BitSorterUtils.getMaskBit(kListI);
            int bits = 1;
            int imm = 0;
            for (int j = 1; j <= 11; j++) { //11bits looks faster than 8 on AMD 4800H, 15 is slower
                if (i - j >= kIndexEnd) {
                    int kListIm1 = kList[i - j];
                    if (kListIm1 == kListI + j) {
                        int sortMask2 = BitSorterUtils.getMaskBit(kListIm1);
                        sortMask1 = sortMask1 | sortMask2;
                        bits++;
                        imm++;
                    } else {
                        break;
                    }
                }
            }
            i -= imm;
            if (bits == 1) {
                partitionStable(oArray, array, start, end, sortMask1, oAux, aux);
            } else {
                int lengthBitsToNumber = twoPowerX(bits);
                if (kListI == 0) {
                    partitionStableLastBits(oArray, array, start, end, sortMask1, lengthBitsToNumber, oAux, aux);
                } else {
                    partitionStableGroupBits(oArray, array, start, end, sortMask1, kListI, lengthBitsToNumber, oAux, aux);
                }
            }
        }
    }

}
