package com.aldogg.sorter.double_.object.st;

import com.aldogg.sorter.*;
import com.aldogg.sorter.double_.SorterUtilsDouble;
import com.aldogg.sorter.double_.object.DoubleMapper;
import com.aldogg.sorter.double_.object.SorterObjectDouble;

import static com.aldogg.sorter.BitSorterParams.RADIX_SORT_MAX_BITS;
import static com.aldogg.sorter.double_.SorterUtilsDouble.listIsOrderedSigned;
import static com.aldogg.sorter.double_.object.SorterUtilsObjectDouble.*;
import static com.aldogg.sorter.MaskInfoInt.UPPER_BIT;
import static com.aldogg.sorter.generic.SorterUtilsGeneric.reverse;

public class RadixBitSorterObjectDouble implements SorterObjectDouble {

    FieldSorterOptions options;

    @Override
    public void sort(Object[] oArray, int start, int endP1, DoubleMapper mapper) {
        options = mapper;
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        double[] array = new double[n];
        for (int i = 0; i < array.length; i++) {
            array[i] = mapper.value(oArray[i]);
        }
        int ordered = listIsOrderedSigned(array, start, endP1);
        if (ordered == AnalysisResult.DESCENDING) {
            SorterUtilsDouble.reverse(array, start, endP1);
            reverse(oArray, start, endP1);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        MaskInfoLong maskInfo = MaskInfoLong.calculateMask(array, start, endP1);
        long mask = maskInfo.getMask();
        int[] bList = MaskInfoLong.getMaskAsArray(mask);
        if (bList.length == 0) { //all numbers are equal
            return;
        }
        sort(oArray, array, start, endP1, bList);
    }

    public void sort(Object[] oArray, double[] array, int start, int endP1, int[] bList) {
        if (bList[0] == UPPER_BIT) { //there are negative numbers and positive numbers
            MaskInfoLong maskInfo;
            long mask;
            long sortMask = 1 << bList[0];
            int finalLeft = options.isStable()
                    ? (partitionReverseStable(oArray, array, start, endP1, sortMask))
                    : (partitionReverseNotStable(oArray, array, start, endP1, sortMask));
            int n1 = finalLeft - start;
            int n2 = endP1 - finalLeft;
            double[] aux = new double[Math.max(n1, n2)];
            Object[] oAux = new Object[Math.max(n1, n2)];
            if (n1 > 1) { //sort negative numbers
                maskInfo = MaskInfoLong.calculateMask(array, start, finalLeft);
                mask = maskInfo.getMask();
                bList = MaskInfoLong.getMaskAsArray(mask);
                radixSort(oArray, array, start, finalLeft, bList, 0, bList.length - 1, oAux, aux);
            }
            if (n2 > 1) { //sort positive numbers
                maskInfo = MaskInfoLong.calculateMask(array, finalLeft, endP1);
                mask = maskInfo.getMask();
                bList = MaskInfoLong.getMaskAsArray(mask);
                radixSort(oArray, array, finalLeft, endP1, bList, 0, bList.length - 1, oAux, aux);
            }
        } else {
            double[] aux = new double[endP1 - start];
            Object[] oAux = new Object[endP1 - start];
            radixSort(oArray, array, start, endP1, bList, 0, bList.length - 1, oAux, aux);
        }
    }

    public static void radixSort(Object[] oArray, double[] array, int start, int endP1, int[] bList, int bListStart, int bListEnd, Object[] oAux, double[] aux) {
        Section[] finalSectionList = BitSorterUtils.getProcessedSections(bList, bListStart, bListEnd, RADIX_SORT_MAX_BITS);

        if (finalSectionList.length == 1 && finalSectionList[0].bits == 1) {
            Section section = finalSectionList[0];
            long mask = MaskInfoLong.getMaskRangeBits(section.start, section.shift);
            partitionStable(oArray, array, start, endP1, mask, oAux, aux);
            return;
        }
        int n = endP1 - start;
        int startAux = 0;

        for (Section section : finalSectionList) {
            if (!(section.shift == 0)) {
                partitionStableGroupBits(oArray, array, start, section, oAux, aux, startAux, n);
            } else {
                partitionStableLastBits(oArray, array, start, section, oAux, aux, startAux, n);
            }

        }

    }
}
