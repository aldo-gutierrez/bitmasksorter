package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.FieldSortOptions;
import com.aldogg.sorter.shared.OrderAnalysisResult;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;
import com.aldogg.sorter.shared.Section;
import com.aldogg.sorter.int_.BitMaskSorterInt;
import com.aldogg.sorter.int_.SorterUtilsInt;

import static com.aldogg.sorter.int_.SorterUtilsInt.listIsOrderedSigned;
import static com.aldogg.sorter.int_.SorterUtilsInt.listIsOrderedUnSigned;
import static com.aldogg.sorter.shared.FieldType.UNSIGNED_INTEGER;

public class RadixByteSorterInt extends BitMaskSorterInt {

    boolean calculateBitMaskOptimization = true;

    public void setCalculateBitMaskOptimization(boolean calculateBitMaskOptimization) {
        this.calculateBitMaskOptimization = calculateBitMaskOptimization;
    }

    @Override
    public void sort(int[] array, final int start, final int endP1, FieldSortOptions options) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        int ordered = options.getFieldType().equals(UNSIGNED_INTEGER) ? listIsOrderedUnSigned(array, start, endP1) : listIsOrderedSigned(array, start, endP1);
        if (ordered == OrderAnalysisResult.DESCENDING) {
            SorterUtilsInt.reverse(array, start, endP1);
        }
        if (ordered != OrderAnalysisResult.UNORDERED) return;

        int[] bList = null;

        if (calculateBitMaskOptimization) {
            MaskInfoInt maskInfo = MaskInfoInt.calculateMask(array, start, endP1);
            int mask = maskInfo.getMask();
            bList = MaskInfoInt.getMaskAsArray(mask);
            if (bList.length == 0) {
                return;
            }
        }
        sort(array, start, endP1, options, bList, null);
    }

    @Override
    public void sort(int[] array, int start, int endP1, FieldSortOptions options, int[] bList, Object params) {
        int mask = 0xFFFFFFFF;
        if (calculateBitMaskOptimization) {
            if (bList.length == 0) {
                return;
            }
            if (bList[0] == MaskInfoInt.UPPER_BIT && !options.getFieldType().equals(UNSIGNED_INTEGER)) { //sign bit is set and there are negative numbers and positive numbers
                int sortMask = 1 << bList[0];
                int finalLeft = options.getFieldType().equals(UNSIGNED_INTEGER)
                        ? SorterUtilsInt.partitionNotStable(array, start, endP1, sortMask)
                        : SorterUtilsInt.partitionReverseNotStable(array, start, endP1, sortMask);
                int n1 = finalLeft - start;
                int n2 = endP1 - finalLeft;
                int mask1 = 0;
                int mask2 = 0;
                if (n1 > 1) { //sort negative numbers
                    MaskInfoInt maskParts1 = MaskInfoInt.calculateMask(array, start, finalLeft);
                    mask1 = maskParts1.getMask();
                    if (mask1 == 0) {
                        n1 = 0;
                    }
                }
                if (n2 > 1) { //sort positive numbers
                    MaskInfoInt maskParts2 = MaskInfoInt.calculateMask(array, finalLeft, endP1);
                    mask2 = maskParts2.getMask();
                    if (mask2 == 0) {
                        n2 = 0;
                    }
                }
                int[] aux = new int[Math.max(n1, n2)];
                if (n1 > 1) { //sort negative numbers
                    sortBytes(array, start, finalLeft, aux, mask1);
                }
                if (n2 > 1) { //sort positive numbers
                    sortBytes(array, finalLeft, endP1, aux, mask2);
                }
                return;
            } else {
                mask = MaskInfoInt.getMaskLastBits(bList, 0);
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
        int ops = 0;
        int[] arrayOrig = array;
        int startOrig = start;
        int startAux = 0;

        if (s0) {
            Section section = new Section(8, 0);
            SorterUtilsInt.partitionStableLastBits(array, start, section, aux, startAux, n);

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
            Section section = new Section(8, 8);
            SorterUtilsInt.partitionStableOneGroupBits(array, start, section, aux, startAux, n);

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
            Section section = new Section(8, 16);
            SorterUtilsInt.partitionStableOneGroupBits(array, start, section, aux, startAux, n);

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
            Section section = new Section(8, 24);
            SorterUtilsInt.partitionStableOneGroupBits(array, start, section, aux, startAux, n);
            array = aux;
            start = startAux;
            ops++;
        }
        if (ops % 2 == 1) {
            System.arraycopy(array, start, arrayOrig, startOrig, n);
        }
    }
}
