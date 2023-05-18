package com.aldogg.sorter.float_.object.st;

import com.aldogg.sorter.*;
import com.aldogg.sorter.float_.SorterUtilsFloat;
import com.aldogg.sorter.float_.object.FloatMapper;
import com.aldogg.sorter.float_.object.SorterObjectFloat;

import static com.aldogg.sorter.BitSorterParams.RADIX_SORT_MAX_BITS;
import static com.aldogg.sorter.float_.SorterUtilsFloat.listIsOrderedSigned;
import static com.aldogg.sorter.float_.object.SorterUtilsObjectFloat.*;
import static com.aldogg.sorter.MaskInfoInt.UPPER_BIT;
import static com.aldogg.sorter.generic.SorterUtilsGeneric.reverse;

public class RadixBitSorterObjectFloat implements SorterObjectFloat {

    boolean stable = false;

    @Override
    public boolean isStable() {
        return stable;
    }

    @Override
    public void setStable(boolean stable) {
        this.stable = stable;
    }

    @Override
    public void sort(Object[] oArray, int start, int endP1, FloatMapper mapper) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        float[] array = new float[n];
        for (int i = 0; i < array.length; i++) {
            array[i] = mapper.value(oArray[i]);
        }
        int ordered = listIsOrderedSigned(array, start, endP1);
        if (ordered == AnalysisResult.DESCENDING) {
            SorterUtilsFloat.reverse(array, start, endP1);
            reverse(oArray, start, endP1);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        MaskInfoInt maskInfo = MaskInfoInt.calculateMask(array, start, endP1);
        int mask = maskInfo.getMask();
        int[] bList = MaskInfoInt.getMaskAsArray(mask);
        if (bList.length == 0) { //all numbers are equal
            return;
        }
        sort(oArray, array, start, endP1, bList);
    }

    public void sort(Object[] oArray, float[] array, int start, int end, int[] bList) {
        if (bList[0] == UPPER_BIT) { //there are negative numbers and positive numbers
            MaskInfoInt maskInfo;
            int mask;
            int sortMask = 1 << bList[0];
            int finalLeft = isStable()
                    ? (partitionReverseStable(oArray, array, start, end, sortMask))
                    : (partitionReverseNotStable(oArray, array, start, end, sortMask));
            int n1 = finalLeft - start;
            int n2 = end - finalLeft;
            float[] aux = new float[Math.max(n1, n2)];
            Object[] oAux = new Object[Math.max(n1, n2)];
            if (n1 > 1) { //sort negative numbers
                maskInfo = MaskInfoInt.calculateMask(array, start, finalLeft);
                mask = maskInfo.getMask();
                bList = MaskInfoInt.getMaskAsArray(mask);
                radixSort(oArray, array, start, finalLeft, bList, 0, bList.length - 1, oAux, aux);
            }
            if (n2 > 1) { //sort positive numbers
                maskInfo = MaskInfoInt.calculateMask(array, finalLeft, end);
                mask = maskInfo.getMask();
                bList = MaskInfoInt.getMaskAsArray(mask);
                radixSort(oArray, array, finalLeft, end, bList, 0, bList.length - 1, oAux, aux);
            }
        } else {
            float[] aux = new float[end - start];
            Object[] oAux = new Object[end - start];
            radixSort(oArray, array, start, end, bList, 0, bList.length - 1, oAux, aux);
        }
    }

    public static void radixSort(Object[] oArray, float[] array, int start, int end, int[] bList, int bListStart, int bListEnd, Object[] oAux, float[] aux) {
        Section[] finalSectionList = BitSorterUtils.getProcessedSections(bList, bListStart, bListEnd, RADIX_SORT_MAX_BITS);

        if (finalSectionList.length == 1 && finalSectionList[0].bits == 1) {
            Section section = finalSectionList[0];
            int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
            partitionStable(oArray, array, start, end, mask, oAux, aux);
            return;
        }
        int n = end - start;
        int startAux = 0;

        for (Section section : finalSectionList) {
            if (!section.isSectionAtEnd()) {
                partitionStableGroupBits(oArray, array, start, section, oAux, aux, startAux, n);
            } else {
                partitionStableLastBits(oArray, array, start, section, oAux, aux, startAux, n);
            }

        }

    }
}
