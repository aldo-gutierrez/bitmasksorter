package com.aldogg.sorter.int_.object.st;

import com.aldogg.sorter.*;
import com.aldogg.sorter.generic.SorterObjectInt;
import com.aldogg.sorter.generic.SorterUtilsGeneric;
import com.aldogg.sorter.int_.SorterUtilsInt;
import com.aldogg.sorter.int_.object.IntMapper;
import com.aldogg.sorter.shared.OrderAnalysisResult;
import com.aldogg.sorter.shared.Section;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;

import static com.aldogg.sorter.BitSorterParams.RADIX_SORT_MAX_BITS;
import static com.aldogg.sorter.shared.int_mask.MaskInfoInt.UPPER_BIT;
import static com.aldogg.sorter.int_.SorterUtilsInt.listIsOrderedSigned;
import static com.aldogg.sorter.int_.SorterUtilsInt.listIsOrderedUnSigned;
import static com.aldogg.sorter.int_.object.SorterUtilsObjectInt.*;

public class RadixBitSorterObjectInt<T> implements SorterObjectInt<T> {

    @Override
    public void sortNNA(T[] oArray, int start, int endP1, IntMapper<T> mapper) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        int[] array = new int[n];
        for (int i = 0; i < array.length; i++) {
            array[i] = mapper.value(oArray[i]);
        }
        int ordered = mapper.isUnsigned() ? listIsOrderedUnSigned(array, start, endP1) : listIsOrderedSigned(array, start, endP1);
        if (ordered == OrderAnalysisResult.DESCENDING) {
            SorterUtilsInt.reverse(array, start, endP1);
            SorterUtilsGeneric.reverse(oArray, start, endP1);
        }
        if (ordered != OrderAnalysisResult.UNORDERED) return;

        MaskInfoInt maskInfo = MaskInfoInt.calculateMask(array, start, endP1);
        int mask = maskInfo.getMask();
        int[] bList = MaskInfoInt.getMaskAsArray(mask);
        if (bList.length == 0) { //all numbers are equal
            return;
        }
        sort(oArray, array, start, endP1, mapper, bList);
    }

    public void sort(Object[] oArray, int[] array, int start, int endP1, FieldOptions options, int[] bList) {
        if (bList[0] == UPPER_BIT) { //there are negative numbers and positive numbers
            MaskInfoInt maskInfo;
            int mask;
            int sortMask = 1 << bList[0];
            int finalLeft = options.isStable()
                    ? (options.isUnsigned()
                    ? partitionStable(oArray, array, start, endP1, sortMask)
                    : partitionReverseStable(oArray, array, start, endP1, sortMask))
                    : (options.isUnsigned()
                    ? partitionNotStable(oArray, array, start, endP1, sortMask)
                    : partitionReverseNotStable(oArray, array, start, endP1, sortMask));
            int n1 = finalLeft - start;
            int n2 = endP1 - finalLeft;
            int[] aux = new int[Math.max(n1, n2)];
            Object[] oAux = new Object[Math.max(n1, n2)];
            if (n1 > 1) { //sort negative numbers
                maskInfo = MaskInfoInt.calculateMask(array, start, finalLeft);
                mask = maskInfo.getMask();
                bList = MaskInfoInt.getMaskAsArray(mask);
                radixSort(oArray, array, start, finalLeft, bList, 0, bList.length - 1, oAux, aux, 0);
            }
            if (n2 > 1) { //sort positive numbers
                maskInfo = MaskInfoInt.calculateMask(array, finalLeft, endP1);
                mask = maskInfo.getMask();
                bList = MaskInfoInt.getMaskAsArray(mask);
                radixSort(oArray, array, finalLeft, endP1, bList, 0, bList.length - 1, oAux, aux, 0);
            }
        } else {
            int[] aux = new int[endP1 - start];
            Object[] oAux = new Object[endP1 - start];
            radixSort(oArray, array, start, endP1, bList, 0, bList.length - 1, oAux, aux, 0);
        }
    }

    /**
     * BitSorterUtils.splitSection
     * Improved performance except by
     * 100000,"0:10000000","RadixBitSorterObjectInt",3->5
     * 10000000,"0:10000000","RadixBitSorterObjectInt",653->873
     * 1000000,"0:10000000","RadixBitSorterObjectInt",47->63
     */
    public static void radixSort(Object[] oArray, int[] array, int start, int endP1, int[] bList, int bListStart, int bListEnd, Object[] oAux, int[] aux, int startAux) {
        Section[] finalSectionList = BitSorterUtils.getProcessedSections(bList, bListStart, bListEnd, RADIX_SORT_MAX_BITS);

        if (finalSectionList.length == 1 && finalSectionList[0].bits == 1) {
            Section section = finalSectionList[0];
            int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
            partitionStable(oArray, array, start, endP1, mask, oAux, aux); //TODO FALTA aumentar startAux
            return;
        }
        int n = endP1 - start;
//        int ops = 0;
//        int[] arrayOrig = array;
//        Object[] oArrayOrig = oArray;
//        int startOrig = start;

        for (Section section : finalSectionList) {
            if (!(section.shift == 0)) {
                partitionStableGroupBits(oArray, array, start, section, oAux, aux, startAux, n);
            } else {
                partitionStableLastBits(oArray, array, start, section, oAux, aux, startAux, n);
            }

//            int[] tempArray = array;
//            array = aux;
//            aux = tempArray;
//
//            Object[] oTempArray = oArray;
//            oArray = oAux;
//            oAux = oTempArray;
//
//            int temp = start;
//            start = startAux;
//            startAux = temp;
//            ops++;
        }
//        if (ops % 2 == 1) {
//            System.arraycopy(array, start, arrayOrig, startOrig, n);
//            System.arraycopy(oArray, start, oArrayOrig, startOrig, n);
//        }

    }

}
