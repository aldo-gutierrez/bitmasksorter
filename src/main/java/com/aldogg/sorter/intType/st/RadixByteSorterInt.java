package com.aldogg.sorter.intType.st;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.IntSection;
import com.aldogg.sorter.intType.IntBitMaskSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

import static com.aldogg.sorter.intType.IntSorterUtils.listIsOrderedSigned;
import static com.aldogg.sorter.intType.IntSorterUtils.listIsOrderedUnSigned;

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
            MaskInfoInt maskInfo = MaskInfoInt.getMaskBit(array, start, end);
            int mask = maskInfo.getMask();
            kList = MaskInfoInt.getMaskAsArray(mask);
            if (kList.length == 0) {
                return;
            }
        }
        sort(array, start, end, kList, null);
    }

    @Override
    public void sort(int[] array, int start, int end, int[] kList, Object multiThreadParams) {
        int mask = 0xFFFFFFFF;
        if (calculateBitMaskOptimization) {
            if (kList.length == 0) {
                return;
            }
            MaskInfoInt maskParts;
            if (kList[0] == SIGN_BIT_POS && !isUnsigned()) { //sign bit is set and there are negative numbers and positive numbers
                int sortMask = 1 << kList[0];
                int finalLeft = isUnsigned()
                        ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                        : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
                int n1 = finalLeft - start;
                int n2 = end - finalLeft;
                int[] aux = new int[Math.max(n1, n2)];
                if (n1 > 1) { //sort negative numbers
                    maskParts = MaskInfoInt.getMaskBit(array, start, finalLeft);
                    mask = maskParts.getMask();
                    sortBytes(array, start, finalLeft, aux, mask);
                }
                if (n2 > 1) { //sort positive numbers
                    maskParts = MaskInfoInt.getMaskBit(array, finalLeft, end);
                    mask = maskParts.getMask();
                    sortBytes(array, finalLeft, end, aux, mask);
                }
                return;
            } else {
                mask = MaskInfoInt.getMaskLastBits(kList, 0);
            }
        }
        int n = end - start;
        int[] aux = new int[n];
        sortBytes(array, start, end, aux, mask);
    }

    private void sortBytes(int[] array, int start, int end, int[] aux, int mask) {
        boolean s0 = (mask & 0xFF) != 0;
        boolean s8 = (mask & 0xFF00) != 0;
        boolean s16 = (mask & 0xFF0000) != 0;
        boolean s24 = (mask & 0xFF000000) != 0;
        int n = end - start;
        IntSection section = new IntSection();
        section.length = 8;
        int ops = 0;
        int[] arrayOrig = array;
        int startOrig = start;
        int startAux = 0;

        if (s0) {
            section.sortMask = 0xFF;
            IntSorterUtils.partitionStableLastBits(array, start, section, aux, n);

            //System.arraycopy(aux, 0, array, start, n);
            //swap array with aux and start with startAux
            int[] tempArray = array;
            array = aux;
            aux = tempArray;
            int temp = start;
            start = startAux;
            startAux = temp;
            ops++;

        }
        if (s8) {
            section.sortMask = 0xFF00;
            section.shiftRight = 8;
            if (startAux == 0) {
                IntSorterUtils.partitionStableOneGroupBits(array, start, section, aux, n);
            } else {
                IntSorterUtils.partitionStableOneGroupBits(array, start, section, aux, startAux, n);
            }

            //System.arraycopy(aux, 0, array, start, n);
            //swap array with aux and start with startAux
            int[] tempArray = array;
            array = aux;
            aux = tempArray;
            int temp = start;
            start = startAux;
            startAux = temp;
            ops++;

        }
        if (s16) {
            section.sortMask = 0xFF0000;
            section.shiftRight = 16;
            if (startAux == 0) {
                IntSorterUtils.partitionStableOneGroupBits(array, start, section, aux, n);
            } else {
                IntSorterUtils.partitionStableOneGroupBits(array, start, section, aux, startAux, n);
            }

            //System.arraycopy(aux, 0, array, start, n);
            //swap array with aux and start with startAux
            int[] tempArray = array;
            array = aux;
            aux = tempArray;
            int temp = start;
            start = startAux;
            startAux = temp;
            ops++;

        }
        if (s24) {
            section.sortMask = 0xFF000000;
            section.shiftRight = 24;
            if (startAux == 0) {
                IntSorterUtils.partitionStableOneGroupBits(array, start, section, aux, n);
            } else {
                IntSorterUtils.partitionStableOneGroupBits(array, start, section, aux, startAux, n);
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
