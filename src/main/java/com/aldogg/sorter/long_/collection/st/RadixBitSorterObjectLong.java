package com.aldogg.sorter.long_.collection.st;

import com.aldogg.sorter.*;
import com.aldogg.sorter.long_.LongSorterUtils;
import com.aldogg.sorter.long_.collection.LongComparator;
import com.aldogg.sorter.long_.collection.ObjectLongSorter;

import static com.aldogg.sorter.int_.IntSorter.SIGN_BIT_POS;
import static com.aldogg.sorter.long_.LongSorterUtils.listIsOrderedSigned;
import static com.aldogg.sorter.long_.LongSorterUtils.listIsOrderedUnSigned;
import static com.aldogg.sorter.long_.collection.ObjectLongSorterUtils.*;

public class RadixBitSorterObjectLong implements ObjectLongSorter {

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
    public void sort(Object[] oArray, int start, int endP1, LongComparator comparator) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        long[] array = new long[n];
        for (int i = 0; i < array.length; i++) {
            array[i] = comparator.value(oArray[i]);
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, endP1) : listIsOrderedSigned(array, start, endP1);
        if (ordered == AnalysisResult.DESCENDING) {
            LongSorterUtils.reverse(array, start, endP1);
            ObjectSorterUtils.reverse(oArray, start, endP1);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        MaskInfoLong maskInfo = MaskInfoLong.getMaskBit(array, start, endP1);
        long mask = maskInfo.getMask();
        int[] kList = MaskInfoLong.getMaskAsArray(mask);
        if (kList.length == 0) { //all numbers are equal
            return;
        }
        sort(oArray, array, start, endP1, kList);
    }

    public void sort(Object[] oArray, long[] array, int start, int endP1, int[] kList) {
        if (kList[0] == SIGN_BIT_POS) { //there are negative numbers and positive numbers
            MaskInfoLong maskInfo;
            long mask;
            long sortMask = 1 << kList[0];
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
                maskInfo = MaskInfoLong.getMaskBit(array, start, finalLeft);
                mask = maskInfo.getMask();
                kList = MaskInfoLong.getMaskAsArray(mask);
                radixSort(oArray, array, start, finalLeft, kList, 0, kList.length - 1, oAux, aux);
            }
            if (n2 > 1) { //sort positive numbers
                maskInfo = MaskInfoLong.getMaskBit(array, finalLeft, endP1);
                mask = maskInfo.getMask();
                kList = MaskInfoLong.getMaskAsArray(mask);
                radixSort(oArray, array, finalLeft, endP1, kList, 0, kList.length - 1, oAux, aux);
            }
        } else {
            long[] aux = new long[endP1 - start];
            Object[] oAux = new Object[endP1 - start];
            radixSort(oArray, array, start, endP1, kList, 0, kList.length - 1, oAux, aux);
        }
    }

    public static void radixSort(Object[] oArray, long[] array, int start, int endP1, int[] kList, int kStart, int kEnd, Object[] oAux, long[] aux) {
        LongSectionsInfo sectionsInfo = BitSorterUtils.getOrderedSectionsLong(kList, kStart, kEnd);
        LongSection[] finalSectionList = sectionsInfo.sections;

        if (finalSectionList.length == 1 && finalSectionList[0].length == 1) {
            partitionStable(oArray, array, start, endP1, finalSectionList[0].sortMask, oAux, aux);
            return;
        }
        int n = endP1 - start;
        int startAux = 0;

        for (LongSection section : finalSectionList) {
            if (!section.isSectionAtEnd()) {
                partitionStableGroupBits(oArray, array, start, section, oAux, aux, startAux, n);
            } else {
                partitionStableLastBits(oArray, array, start, section, oAux, aux, startAux, n);
            }

        }

    }
}
