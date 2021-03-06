package com.aldogg.sorter.collection.st;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.MaskInfo;
import com.aldogg.sorter.Section;
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

        MaskInfo maskInfo = MaskInfo.getMaskBit(array, start, end);
        int mask = maskInfo.getMask();
        int[] kList = MaskInfo.getMaskAsArray(mask);
        if (kList.length == 0) { //all numbers are equal
            return;
        }
        sort(oArray, array, start, end, kList);
    }

    public void sort(Object[] oArray, int[] array, int start, int end, int[] kList) {
        if (kList[0] == 31) { //there are negative numbers and positive numbers
            MaskInfo maskInfo;
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
                maskInfo = MaskInfo.getMaskBit(array, start, finalLeft);
                mask = maskInfo.getMask();
                kList = MaskInfo.getMaskAsArray(mask);
                radixSort(oArray, array, start, finalLeft, kList, 0, kList.length - 1, oAux, aux);
            }
            if (end - finalLeft > 1) { //sort positive numbers
                int[] aux = new int[end - finalLeft];
                Object[] oAux = new Object[end - finalLeft];
                maskInfo = MaskInfo.getMaskBit(array, finalLeft, end);
                mask = maskInfo.getMask();
                kList = MaskInfo.getMaskAsArray(mask);
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

        Section[] sections = BitSorterUtils.getMaskAsSections(kList, kStart, kEnd);
//        int n = end - start;
        for (int i = sections.length - 1; i >= 0; i--) {
            Section section = sections[i];
            Section[] sSections = BitSorterUtils.splitSection(section);
            for (int j = sSections.length - 1; j >= 0; j--) {
                Section sSection = sSections[j];
                if (sSection.length > 1) {
                    int twoPowerK = 1 << sSection.length;
                    if (sSection.isSectionAtEnd()) {
                        partitionStableLastBits(oArray, array, start, end, sSection.sortMask, twoPowerK, oAux, aux);
                    } else {
                        partitionStableGroupBits(oArray, array, start, end, sSection.sortMask, sSection.shiftRight, twoPowerK, oAux, aux);
                    }
                } else {
                    partitionStable(oArray, array, start, end, sSection.sortMask, oAux, aux);
                }
            }
        }
    }

}
