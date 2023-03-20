package com.aldogg.sorter.int_.collection.st;

import com.aldogg.sorter.*;
import com.aldogg.sorter.int_.IntSorterUtils;
import com.aldogg.sorter.int_.collection.IntComparator;
import com.aldogg.sorter.int_.collection.ObjectIntSorter;

import static com.aldogg.sorter.int_.IntSorter.SIGN_BIT_POS;
import static com.aldogg.sorter.int_.IntSorterUtils.listIsOrderedSigned;
import static com.aldogg.sorter.int_.IntSorterUtils.listIsOrderedUnSigned;
import static com.aldogg.sorter.int_.collection.ObjectIntSorterUtils.*;

public class RadixBitSorterObjectInt implements ObjectIntSorter {

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
            array[i] = comparator.value(oArray[i]);
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, end) : listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(array, start, end);
            ObjectSorterUtils.reverse(oArray, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        MaskInfoInt maskInfo = MaskInfoInt.getMaskBit(array, start, end);
        int mask = maskInfo.getMask();
        int[] kList = MaskInfoInt.getMaskAsArray(mask);
        if (kList.length == 0) { //all numbers are equal
            return;
        }
        sort(oArray, array, start, end, kList);
    }

    public void sort(Object[] oArray, int[] array, int start, int end, int[] kList) {
        if (kList[0] == SIGN_BIT_POS) { //there are negative numbers and positive numbers
            MaskInfoInt maskInfo;
            int mask;
            int sortMask = 1 << kList[0];
            int finalLeft = isStable()
                    ? (isUnsigned()
                    ? partitionStable(oArray, array, start, end, sortMask)
                    : partitionReverseStable(oArray, array, start, end, sortMask))
                    : (isUnsigned()
                    ? partitionNotStable(oArray, array, start, end, sortMask)
                    : partitionReverseNotStable(oArray, array, start, end, sortMask));
            int n1 = finalLeft - start;
            int n2 = end - finalLeft;
            int[] aux = new int[Math.max(n1, n2)];
            Object[] oAux = new Object[Math.max(n1, n2)];
            if (n1 > 1) { //sort negative numbers
                maskInfo = MaskInfoInt.getMaskBit(array, start, finalLeft);
                mask = maskInfo.getMask();
                kList = MaskInfoInt.getMaskAsArray(mask);
                radixSort(oArray, array, start, finalLeft, kList, 0, kList.length - 1, oAux, aux);
            }
            if (n2 > 1) { //sort positive numbers
                maskInfo = MaskInfoInt.getMaskBit(array, finalLeft, end);
                mask = maskInfo.getMask();
                kList = MaskInfoInt.getMaskAsArray(mask);
                radixSort(oArray, array, finalLeft, end, kList, 0, kList.length - 1, oAux, aux);
            }
        } else {
            int[] aux = new int[end - start];
            Object[] oAux = new Object[end - start];
            radixSort(oArray, array, start, end, kList, 0, kList.length - 1, oAux, aux);
        }
    }

    /**
     * BitSorterUtils.splitSection
     * Improved performance except by
     * 100000,"0:10000000","RadixBitSorterObjectInt",3->5
     * 10000000,"0:10000000","RadixBitSorterObjectInt",653->873
     * 1000000,"0:10000000","RadixBitSorterObjectInt",47->63
     */
    public static void radixSort(Object[] oArray, int[] array, int start, int end, int[] kList, int kStart, int kEnd, Object[] oAux, int[] aux) {
        IntSectionsInfo sectionsInfo = BitSorterUtils.getOrderedSections(kList, kStart, kEnd);
        IntSection[] finalSectionList = sectionsInfo.sections;

        if (finalSectionList.length == 1 && finalSectionList[0].length == 1) {
            partitionStable(oArray, array, start, end, finalSectionList[0].sortMask, oAux, aux);
            return;
        }
        int n = end - start;
        int startAux = 0;
//        int ops = 0;
//        int[] arrayOrig = array;
//        Object[] oArrayOrig = oArray;
//        int startOrig = start;

        for (IntSection section : finalSectionList) {
            if (!section.isSectionAtEnd()) {
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
