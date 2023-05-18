package com.aldogg.sorter.long_.object.st;

import com.aldogg.sorter.*;
import com.aldogg.sorter.generic.SorterUtilsGeneric;
import com.aldogg.sorter.long_.SorterUtilsLong;
import com.aldogg.sorter.long_.object.LongMapper;
import com.aldogg.sorter.long_.object.SorterObjectLong;

import static com.aldogg.sorter.BitSorterParams.RADIX_SORT_MAX_BITS;
import static com.aldogg.sorter.MaskInfoInt.UPPER_BIT;
import static com.aldogg.sorter.long_.SorterUtilsLong.listIsOrderedSigned;
import static com.aldogg.sorter.long_.SorterUtilsLong.listIsOrderedUnSigned;
import static com.aldogg.sorter.long_.object.SorterUtilsObjectLong.*;

public class RadixBitSorterObjectLong implements SorterObjectLong {

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
    public void sort(Object[] oArray, int start, int endP1, LongMapper mapper) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        long[] array = new long[n];
        for (int i = 0; i < array.length; i++) {
            array[i] = mapper.value(oArray[i]);
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, endP1) : listIsOrderedSigned(array, start, endP1);
        if (ordered == AnalysisResult.DESCENDING) {
            SorterUtilsLong.reverse(array, start, endP1);
            SorterUtilsGeneric.reverse(oArray, start, endP1);
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

    public void sort(Object[] oArray, long[] array, int start, int endP1, int[] bList) {
        if (bList[0] == UPPER_BIT) { //there are negative numbers and positive numbers
            MaskInfoLong maskInfo;
            long mask;
            long sortMask = 1 << bList[0];
            int finalLeft = isStable()
                    ? (isUnsigned()
                    ? partitionStable(oArray, array, start, endP1, sortMask)
                    : partitionReverseStable(oArray, array, start, endP1, sortMask))
                    : (isUnsigned()
                    ? partitionNotStable(oArray, array, start, endP1, sortMask)
                    : partitionReverseNotStable(oArray, array, start, endP1, sortMask));
            int n1 = finalLeft - start;
            int n2 = endP1 - finalLeft;
            long[] aux = new long[Math.max(n1, n2)];
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
            long[] aux = new long[endP1 - start];
            Object[] oAux = new Object[endP1 - start];
            radixSort(oArray, array, start, endP1, bList, 0, bList.length - 1, oAux, aux);
        }
    }

    public static void radixSort(Object[] oArray, long[] array, int start, int endP1, int[] bList, int bListStart, int bListEnd, Object[] oAux, long[] aux) {
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
            if (!section.isSectionAtEnd()) {
                partitionStableGroupBits(oArray, array, start, section, oAux, aux, startAux, n);
            } else {
                partitionStableLastBits(oArray, array, start, section, oAux, aux, startAux, n);
            }

        }

    }
}
