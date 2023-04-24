package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.IntSection;
import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.int_.IntBitMaskSorter;
import com.aldogg.sorter.int_.IntSorterUtils;

import static com.aldogg.sorter.int_.IntSorterUtils.listIsOrderedSigned;
import static com.aldogg.sorter.int_.IntSorterUtils.listIsOrderedUnSigned;

public class RadixByteSorterInt extends IntBitMaskSorter {

    boolean calculateBitMaskOptimization = true;

    public void setCalculateBitMaskOptimization(boolean calculateBitMaskOptimization) {
        this.calculateBitMaskOptimization = calculateBitMaskOptimization;
    }

    @Override
    public void sort(int[] array, final int start, final int endP1) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, endP1) : listIsOrderedSigned(array, start, endP1);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(array, start, endP1);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        int[] kList = null;

        if (calculateBitMaskOptimization) {
            MaskInfoInt maskInfo = MaskInfoInt.calculateMask(array, start, endP1);
            int mask = maskInfo.getMask();
            kList = MaskInfoInt.getMaskAsArray(mask);
            if (kList.length == 0) {
                return;
            }
        }
        sort(array, start, endP1, kList, null);
    }

    @Override
    public void sort(int[] array, int start, int endP1, int[] kList, Object multiThreadParams) {
        int mask = 0xFFFFFFFF;
        if (calculateBitMaskOptimization) {
            if (kList.length == 0) {
                return;
            }
            MaskInfoInt maskParts;
            if (kList[0] == MaskInfoInt.UPPER_BIT && !isUnsigned()) { //sign bit is set and there are negative numbers and positive numbers
                int sortMask = 1 << kList[0];
                int finalLeft = isUnsigned()
                        ? IntSorterUtils.partitionNotStable(array, start, endP1, sortMask)
                        : IntSorterUtils.partitionReverseNotStable(array, start, endP1, sortMask);
                int n1 = finalLeft - start;
                int n2 = endP1 - finalLeft;
                int[] aux = new int[Math.max(n1, n2)];
                if (n1 > 1) { //sort negative numbers
                    maskParts = MaskInfoInt.calculateMask(array, start, finalLeft);
                    mask = maskParts.getMask();
                    sortBytes(array, start, finalLeft, aux, mask);
                }
                if (n2 > 1) { //sort positive numbers
                    maskParts = MaskInfoInt.calculateMask(array, finalLeft, endP1);
                    mask = maskParts.getMask();
                    sortBytes(array, finalLeft, endP1, aux, mask);
                }
                return;
            } else {
                mask = MaskInfoInt.getMaskLastBits(kList, 0);
            }
        }
        int n = endP1 - start;
        int[] aux = new int[n];
        sortBytes(array, start, endP1, aux, mask);
    }

    private void sortBytes(int[] array, int start, int endP1, int[] aux, int mask) {
        boolean s0 = (mask & 0xFF) != 0;
        boolean s8 = (mask & 0xFF00) != 0;
        boolean s16 = (mask & 0xFF0000) != 0;
        boolean s24 = (mask & 0xFF000000) != 0;
        int n = endP1 - start;
        IntSection section = new IntSection();
        section.length = 8;
        int ops = 0;
        int[] arrayOrig = array;
        int startOrig = start;
        int startAux = 0;

        if (s0) {
            section.sortMask = 0xFF;
            IntSorterUtils.partitionStableLastBits(array, start, section, aux, startAux, n);

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
            IntSorterUtils.partitionStableOneGroupBits(array, start, section, aux, startAux, n);

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
            IntSorterUtils.partitionStableOneGroupBits(array, start, section, aux, startAux, n);

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
            IntSorterUtils.partitionStableOneGroupBits(array, start, section, aux, startAux, n);
            array = aux;
            start = startAux;
            ops++;
        }
        if (ops % 2 == 1) {
            System.arraycopy(array, start, arrayOrig, startOrig, n);
        }
    }
}
