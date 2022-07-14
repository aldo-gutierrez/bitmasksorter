package com.aldogg.sorter.intType.st;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.MaskInfo;
import com.aldogg.sorter.Section;
import com.aldogg.sorter.intType.IntBitMaskSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

import static com.aldogg.sorter.BitSorterUtils.*;

public class RadixByteSorterInt extends IntBitMaskSorter {

    boolean calculateBitMaskOptimization = true;

    public void setCalculateBitMaskOptimization(boolean calculateBitMaskOptimization) {
        this.calculateBitMaskOptimization = calculateBitMaskOptimization;
    }

    @Override
    public void sort(int[] array, final int start, final int end) {
        int n = end - start;
        if (n < 2) {
            return;
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, end) : listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(array, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        int[] kList = null;

        if (calculateBitMaskOptimization) {
            MaskInfo maskInfo = MaskInfo.getMaskBit(array, start, end);
            int mask = maskInfo.getMask();
            kList = MaskInfo.getMaskAsArray(mask);
            if (kList.length == 0) {
                return;
            }
        }
        sort(array, start, end, kList);
    }

    @Override
    public void sort(int[] array, int start, int end, int[] kList) {
        boolean s0 = true;
        boolean s8 = true;
        boolean s16 = true;
        boolean s24 = true;
        if (calculateBitMaskOptimization) {
            if (kList.length == 0) {
                return;
            }
        }
        int[] aux = new int[end - start];
        if (calculateBitMaskOptimization) {
            MaskInfo maskParts;
            int mask;
            if (kList[0] == 31 && !isUnsigned()) { //sign bit is set and there are negative numbers and positive numbers
                int sortMask = 1 << kList[0];
                int finalLeft = isUnsigned()
                        ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                        : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
                if (finalLeft - start > 1) { //sort negative numbers
                    maskParts = MaskInfo.getMaskBit(array, start, finalLeft);
                    mask = maskParts.getMask();
                    s0 = (mask & 0xFF) != 0;
                    s8 = (mask & 0xFF00) != 0;
                    s16 = (mask & 0xFF0000) != 0;
                    s24 = (mask & 0xFF000000) != 0;
                    sortBytes(array, aux, start, finalLeft, s0, s8, s16, s24);
                }
                if (end - finalLeft > 1) { //sort positive numbers
                    maskParts = MaskInfo.getMaskBit(array, finalLeft, end);
                    mask = maskParts.getMask();
                    s0 = (mask & 0xFF) != 0;
                    s8 = (mask & 0xFF00) != 0;
                    s16 = (mask & 0xFF0000) != 0;
                    s24 = (mask & 0xFF000000) != 0;
                    sortBytes(array, aux, finalLeft, end, s0, s8, s16, s24);
                }
                return;
            } else {
                mask = MaskInfo.getMaskLastBits(kList, 0);
                s0 = (mask & 0xFF) != 0;
                s8 = (mask & 0xFF00) != 0;
                s16 = (mask & 0xFF0000) != 0;
                s24 = (mask & 0xFF000000) != 0;
            }
        }
        sortBytes(array, aux, start, end, s0, s8, s16, s24);
    }

    private void sortBytes(int[] array, int[] aux, int start, int end, boolean s0, boolean s8, boolean s16, boolean s24) {
        int[] original = array;
        int n = end - start;
        int[] leftX = new int[256];
        Section section = new Section();
        if (s0) {
            section.sortMask = 0xFF;
            int[] count = new int[256];
            leftX[0] = 0;
            IntSorterUtils.partitionStableLastBits(array, start, aux, start, n, section, leftX, count);
            //System.arraycopy(aux, 0, array, start, n);
            int[] temp = aux;
            aux = array;
            array = temp;
        }
        if (s8) {
            int[] count = new int[256];
            leftX[0] = 0;
            section.sortMask = 0xFF00;
            section.shiftRight = 8;
            IntSorterUtils.partitionStableOneGroupBits(array, start, aux, start, n, section, leftX, count);
            //System.arraycopy(aux, 0, array, start, n);
            int[] temp = aux;
            aux = array;
            array = temp;
        }
        if (s16) {
            int[] count = new int[256];
            leftX[0] = 0;
            section.sortMask = 0xFF0000;
            section.shiftRight = 16;
            IntSorterUtils.partitionStableOneGroupBits(array, start, aux, start, n, section, leftX, count);
            //System.arraycopy(aux, 0, array, start, n);
            int[] temp = aux;
            aux = array;
            array = temp;
        }
        if (s24) {
            int[] count = new int[256];
            leftX[0] = 0;
            section.sortMask = 0xFF000000;
            section.shiftRight = 24;
            IntSorterUtils.partitionStableOneGroupBits(array, start, aux, start, n, section, leftX, count);
            int lengthPositive = leftX[128];
            if (isUnsigned()) {
//                System.arraycopy(aux, 0, array, start, n);
                array = aux;
            } else {
                if (lengthPositive < n) {
                    int lengthNegative = n - lengthPositive;
//                    System.arraycopy(aux, lengthPositive, array, start, lengthNegative);
//                    System.arraycopy(aux, 0, array, start + lengthNegative, lengthPositive);
                    System.arraycopy(aux, start + lengthPositive, array, start, lengthNegative);
                    System.arraycopy(aux, start, array, start + lengthNegative, lengthPositive);
                    array = aux;
                } else {
//                    System.arraycopy(aux, 0, array, start, n);
                    array = aux;
                }
            }
        }
        System.arraycopy(array, start, original, start, n);
    }
}
