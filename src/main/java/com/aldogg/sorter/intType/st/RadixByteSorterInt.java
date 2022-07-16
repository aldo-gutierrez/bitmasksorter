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
        if (calculateBitMaskOptimization) {
            MaskInfo maskParts;
            int mask;
            if (kList[0] == 31 && !isUnsigned()) { //sign bit is set and there are negative numbers and positive numbers
                int sortMask = 1 << kList[0];
                int finalLeft = isUnsigned()
                        ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                        : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
                int n1 = finalLeft - start;
                int n2 = end - finalLeft;
                int[] aux = new int[Math.max(n1, n2)];
                if (n1 > 1) { //sort negative numbers
                    maskParts = MaskInfo.getMaskBit(array, start, finalLeft);
                    mask = maskParts.getMask();
                    s0 = (mask & 0xFF) != 0;
                    s8 = (mask & 0xFF00) != 0;
                    s16 = (mask & 0xFF0000) != 0;
                    s24 = (mask & 0xFF000000) != 0;
                    sortBytes(array, start, finalLeft, aux, s0, s8, s16, s24);
                }
                if (n2 > 1) { //sort positive numbers
                    maskParts = MaskInfo.getMaskBit(array, finalLeft, end);
                    mask = maskParts.getMask();
                    s0 = (mask & 0xFF) != 0;
                    s8 = (mask & 0xFF00) != 0;
                    s16 = (mask & 0xFF0000) != 0;
                    s24 = (mask & 0xFF000000) != 0;
                    sortBytes(array, finalLeft, end, aux, s0, s8, s16, s24);
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
        int[] aux = new int[end - start];
        sortBytes(array, start, end, aux, s0, s8, s16, s24);
    }

    private void sortBytes(final int[] arrayOrig, final int startOrig, final int end, int[] auxOrig, final boolean s0, final boolean s8, final boolean s16, final boolean s24) {
        int n = end - startOrig;
        int[] leftX = new int[256];
        int[] array = arrayOrig;
        int[] aux = auxOrig;
        int start = startOrig;
        int startAux = 0;
        Section section = new Section();
        int ops = 0;
        if (s0) {
            section.sortMask = 0xFF;
            int[] count = new int[256];
            leftX[0] = 0;
            IntSorterUtils.partitionStableLastBits(array, start, aux, startAux, n, section, leftX, count);
            //System.arraycopy(aux, 0, array, start, n);
            int[] tempArray = array;
            array = aux;
            aux = tempArray;

            int temp = start;
            start = startAux;
            startAux = temp;

            ops++;
        }
        if (s8) {
            int[] count = new int[256];
            leftX[0] = 0;
            section.sortMask = 0xFF00;
            section.shiftRight = 8;
            IntSorterUtils.partitionStableOneGroupBits(array, start, aux, startAux, n, section, leftX, count);
            //System.arraycopy(aux, 0, array, start, n);
            int[] tempArray = array;
            array = aux;
            aux = tempArray;

            int temp = start;
            start = startAux;
            startAux = temp;

            ops++;
        }
        if (s16) {
            int[] count = new int[256];
            leftX[0] = 0;
            section.sortMask = 0xFF0000;
            section.shiftRight = 16;
            IntSorterUtils.partitionStableOneGroupBits(array, start, aux, startAux, n, section, leftX, count);
            //System.arraycopy(aux, 0, array, start, n);
            int[] tempArray = array;
            array = aux;
            aux = tempArray;

            int temp = start;
            start = startAux;
            startAux = temp;

            ops++;
        }
        if (s24) {
            int[] count = new int[256];
            leftX[0] = 0;
            section.sortMask = 0xFF000000;
            section.shiftRight = 24;
            IntSorterUtils.partitionStableOneGroupBits(array, start, aux, startAux, n, section, leftX, count);
            int lengthPositive = leftX[128];
            if (isUnsigned()) {
//                System.arraycopy(aux, 0, array, start, n);
            } else {
                if (lengthPositive < n) {
                    int lengthNegative = n - lengthPositive;
//                    System.arraycopy(aux, lengthPositive, array, start, lengthNegative);
//                    System.arraycopy(aux, 0, array, start + lengthNegative, lengthPositive);
                    System.arraycopy(aux, startAux + lengthPositive, array, start, lengthNegative);
                    System.arraycopy(aux, startAux, array, start + lengthNegative, lengthPositive);
                } else {
//                    System.arraycopy(aux, 0, array, start, n);
                }
            }

            array = aux;
            start = startAux;
            ops++;
        }
        if (ops % 2 == 1) {
            System.arraycopy(array, start, arrayOrig, startOrig, n);
        }
      }
}