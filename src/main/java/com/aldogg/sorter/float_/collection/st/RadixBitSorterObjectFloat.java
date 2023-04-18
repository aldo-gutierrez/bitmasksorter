package com.aldogg.sorter.float_.collection.st;

import com.aldogg.sorter.*;
import com.aldogg.sorter.float_.FloatSorterUtils;
import com.aldogg.sorter.float_.collection.FloatComparator;
import com.aldogg.sorter.float_.collection.ObjectFloatSorter;

import static com.aldogg.sorter.float_.FloatSorterUtils.listIsOrderedSigned;
import static com.aldogg.sorter.float_.collection.ObjectFloatSorterUtils.*;
import static com.aldogg.sorter.int_.IntSorter.SIGN_BIT_POS;

public class RadixBitSorterObjectFloat implements ObjectFloatSorter {

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
    public void sort(Object[] oArray, int start, int endP1, FloatComparator comparator) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        float[] array = new float[n];
        for (int i = 0; i < array.length; i++) {
            array[i] = comparator.value(oArray[i]);
        }
        int ordered = listIsOrderedSigned(array, start, endP1);
        if (ordered == AnalysisResult.DESCENDING) {
            FloatSorterUtils.reverse(array, start, endP1);
            ObjectSorterUtils.reverse(oArray, start, endP1);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        MaskInfoInt maskInfo = MaskInfoInt.getMaskBit(array, start, endP1);
        int mask = maskInfo.getMask();
        int[] kList = MaskInfoInt.getMaskAsArray(mask);
        if (kList.length == 0) { //all numbers are equal
            return;
        }
        sort(oArray, array, start, endP1, kList);
    }

    public void sort(Object[] oArray, float[] array, int start, int end, int[] kList) {
        if (kList[0] == SIGN_BIT_POS) { //there are negative numbers and positive numbers
            MaskInfoInt maskInfo;
            int mask;
            int sortMask = 1 << kList[0];
            int finalLeft = isStable()
                    ? (partitionReverseStable(oArray, array, start, end, sortMask))
                    : (partitionReverseNotStable(oArray, array, start, end, sortMask));
            int n1 = finalLeft - start;
            int n2 = end - finalLeft;
            float[] aux = new float[Math.max(n1, n2)];
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
            float[] aux = new float[end - start];
            Object[] oAux = new Object[end - start];
            radixSort(oArray, array, start, end, kList, 0, kList.length - 1, oAux, aux);
        }
    }

    public static void radixSort(Object[] oArray, float[] array, int start, int end, int[] kList, int kStart, int kEnd, Object[] oAux, float[] aux) {
        IntSectionsInfo sectionsInfo = BitSorterUtils.getOrderedSections(kList, kStart, kEnd);
        IntSection[] finalSectionList = sectionsInfo.sections;

        if (finalSectionList.length == 1 && finalSectionList[0].length == 1) {
            partitionStable(oArray, array, start, end, finalSectionList[0].sortMask, oAux, aux);
            return;
        }
        int n = end - start;
        int startAux = 0;

        for (IntSection section : finalSectionList) {
            if (!section.isSectionAtEnd()) {
                partitionStableGroupBits(oArray, array, start, section, oAux, aux, startAux, n);
            } else {
                partitionStableLastBits(oArray, array, start, section, oAux, aux, startAux, n);
            }

        }

    }
}
