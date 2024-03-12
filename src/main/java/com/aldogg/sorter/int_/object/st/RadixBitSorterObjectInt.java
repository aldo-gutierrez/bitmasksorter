package com.aldogg.sorter.int_.object.st;

import com.aldogg.sorter.*;
import com.aldogg.sorter.int_.SorterObjectInt;
import com.aldogg.sorter.generic.SorterUtilsGeneric;
import com.aldogg.sorter.int_.SorterUtilsInt;
import com.aldogg.sorter.int_.object.IntMapper;
import com.aldogg.sorter.shared.OrderAnalysisResult;
import com.aldogg.sorter.shared.Section;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;

import static com.aldogg.sorter.BitSorterParams.RADIX_SORT_MAX_BITS;
import static com.aldogg.sorter.int_.SorterUtilsInt.listIsOrderedSigned;
import static com.aldogg.sorter.int_.SorterUtilsInt.listIsOrderedUnSigned;
import static com.aldogg.sorter.int_.object.SorterUtilsObjectInt.*;

public class RadixBitSorterObjectInt<T> implements SorterObjectInt<T> {

    @Override
    public void sortNNA(T[] oArray, int oStart, int oEndP1, IntMapper<T> mapper) {
        int n = oEndP1 - oStart;
        if (n < 2) {
            return;
        }
        int[] array = new int[n];
        for (int i = 0; i < array.length; i++) {
            array[i] = mapper.value(oArray[oStart + i]);
        }
        int ordered = mapper.isUnsigned() ? listIsOrderedUnSigned(array, 0, n) : listIsOrderedSigned(array, 0, n);
        if (ordered == OrderAnalysisResult.DESCENDING) {
            SorterUtilsInt.reverse(array, 0, n);
            SorterUtilsGeneric.reverse(oArray, oStart, oEndP1);
        }
        if (ordered != OrderAnalysisResult.UNORDERED) return;

        RuntimeOptionsInt runtime = new RuntimeOptionsInt();
        runtime.array = array;
        runtime.oArray = oArray;
        MaskInfoInt maskInfo = MaskInfoInt.calculateMaskBreakIfUpperBit(array, 0, n, null);
        if (maskInfo.isUpperBitMaskSet()) { //the sign bit is set
            int sortMask = MaskInfoInt.getUpperBitMask();
            int oFinalLeft = mapper.isStable()
                    ? (mapper.isUnsigned()
                    ? partitionStable(runtime, oStart, sortMask, n)
                    : partitionReverseStable(runtime, oStart, sortMask, n))
                    : (mapper.isUnsigned()
                    ? partitionNotStable(oArray, oStart, array, 0, sortMask, n)
                    : partitionReverseNotStable(oArray, oStart, array, 0, sortMask, n));
            int n1 = oFinalLeft - oStart;
            int n2 = oEndP1 - oFinalLeft;
            int[] bList1 = null;
            int[] bList2 = null;
            if (n1 > 1) { //sort negative numbers
                bList1 = MaskInfoInt.getMaskAsArray(MaskInfoInt.calculateMask(array, 0, oFinalLeft - oStart).getMask());
                if (bList1.length <= 0) {
                    n1 = 0;
                }
            }
            if (n2 > 1) { //sort positive numbers
                bList2 = MaskInfoInt.getMaskAsArray(MaskInfoInt.calculateMask(array, oFinalLeft - oStart, n).getMask());
                if (bList2.length <= 0) {
                    n2 = 0;
                }
            }
            if (runtime.oAux == null) {
                runtime.aux = new int[Math.max(n1, n2)];
                runtime.oAux = new Object[Math.max(n1, n2)];
            }
            if (n1 > 1) {
                radixSort(runtime, oStart, 0, bList1, 0, 0, n1);
            }
            if (n2 > 1) {
                radixSort(runtime, oFinalLeft, n1, bList2, 0, 0, n2);
            }
        } else {
            if (runtime.oAux == null) {
                runtime.aux = new int[oEndP1 - oStart];
                runtime.oAux = new Object[oEndP1 - oStart];
            }
            int[] bList = MaskInfoInt.getMaskAsArray(maskInfo.getMask());
            radixSort(runtime, oStart, 0, bList, 0, 0, n);
        }
    }

    /**
     * BitSorterUtils.splitSection
     * Improved performance except by
     * 100000,"0:10000000","RadixBitSorterObjectInt",3->5
     * 10000000,"0:10000000","RadixBitSorterObjectInt",653->873
     * 1000000,"0:10000000","RadixBitSorterObjectInt",47->63
     */
    public static void radixSort(RuntimeOptionsInt runtime, int oStart, int aStart, int[] bList, int bListStart, int startAux, int n) {
        Section[] finalSectionList = BitSorterUtils.getProcessedSections(bList, bListStart, bList.length - 1, RADIX_SORT_MAX_BITS);

        if (finalSectionList.length == 1 && finalSectionList[0].bits == 1) {
            Section section = finalSectionList[0];
            int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
            partitionStable(runtime, oStart, mask, startAux, n);
            return;
        }
//        int ops = 0;
//        int[] arrayOrig = array;
//        Object[] oArrayOrig = oArray;
//        int startOrig = start;

        for (Section section : finalSectionList) {
            if (!(section.shift == 0)) {
                partitionStableOneGroupBits(runtime, oStart, aStart, section, startAux, n);
            } else {
                partitionStableLastBits(runtime, oStart, aStart, section, startAux, n);
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
