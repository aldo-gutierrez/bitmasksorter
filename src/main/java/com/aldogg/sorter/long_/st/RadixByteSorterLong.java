package com.aldogg.sorter.long_.st;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.MaskInfoLong;
import com.aldogg.sorter.Section;
import com.aldogg.sorter.long_.BitMaskSorterLong;
import com.aldogg.sorter.long_.SorterUtilsLong;

import static com.aldogg.sorter.long_.SorterUtilsLong.listIsOrderedSigned;
import static com.aldogg.sorter.long_.SorterUtilsLong.listIsOrderedUnSigned;

public class RadixByteSorterLong extends BitMaskSorterLong {

    boolean calculateBitMaskOptimization = true;

    public void setCalculateBitMaskOptimization(boolean calculateBitMaskOptimization) {
        this.calculateBitMaskOptimization = calculateBitMaskOptimization;
    }

    @Override
    public void sort(long[] array, final int start, final int endP1) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, endP1) : listIsOrderedSigned(array, start, endP1);
        if (ordered == AnalysisResult.DESCENDING) {
            SorterUtilsLong.reverse(array, start, endP1);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        int[] bList = null;

        if (calculateBitMaskOptimization) {
            MaskInfoLong maskInfo = MaskInfoLong.calculateMask(array, start, endP1);
            long mask = maskInfo.getMask();
            bList = MaskInfoLong.getMaskAsArray(mask);
            if (bList.length == 0) {
                return;
            }
        }
        sort(array, start, endP1, bList);
    }

    @Override
    public void sort(long[] array, int start, int endP1, int[] bList) {
        long mask = 0xFFFFFFFFFFFFFFFFL;
        if (calculateBitMaskOptimization) {
            if (bList.length == 0) {
                return;
            }
            MaskInfoLong maskParts;
            if (bList[0] == MaskInfoLong.UPPER_BIT && !isUnsigned()) { //sign bit is set and there are negative numbers and positive numbers
                int sortMask = 1 << bList[0];
                int finalLeft = isUnsigned()
                        ? SorterUtilsLong.partitionNotStable(array, start, endP1, sortMask)
                        : SorterUtilsLong.partitionReverseNotStable(array, start, endP1, sortMask);
                int n1 = finalLeft - start;
                int n2 = endP1 - finalLeft;
                long[] aux = new long[Math.max(n1, n2)];
                if (n1 > 1) { //sort negative numbers
                    maskParts = MaskInfoLong.calculateMask(array, start, finalLeft);
                    mask = maskParts.getMask();
                    sortBytes(array, start, finalLeft, aux, mask);
                }
                if (n2 > 1) { //sort positive numbers
                    maskParts = MaskInfoLong.calculateMask(array, finalLeft, endP1);
                    mask = maskParts.getMask();
                    sortBytes(array, finalLeft, endP1, aux, mask);
                }
                return;
            } else {
                mask = MaskInfoLong.getMaskLastBits(bList, 0);
            }
        }
        int n = endP1 - start;
        long[] aux = new long[n];
        sortBytes(array, start, endP1, aux, mask);
    }

    private void sortBytes(long[] array, int start, int endP1, long[] aux, long mask) {
        int n = endP1 - start;
        Section section = new Section();
        section.bits = 8;
        int ops = 0;
        long[] arrayOrig = array;
        int startOrig = start;
        int startAux = 0;

        int[] shiftRights = new int[]{0, 8, 16, 24, 32, 40, 48, 56};
        long[] sortMasks = new long[]{0xFFL, 0xFF00L, 0xFF0000L, 0xFF000000L, 0xFF00000000L, 0xFF0000000000L, 0xFF000000000000L, 0xFF00000000000000L};

        long sortMask = sortMasks[0];
        if ((mask & sortMask) != 0) {
            section.start = shiftRights[0] + 7;
            SorterUtilsLong.partitionStableLastBits(array, start, section, aux, startAux, n);

            //System.arraycopy(aux, 0, array, start, n);
            //swap array with aux and start with startAux
            long[] tempArray = array;
            array = aux;
            aux = tempArray;
            int temp = start;
            start = startAux;
            startAux = temp;
            ops++;

        }
        for (int i = 0; i < shiftRights.length; ++i) {
            sortMask = sortMasks[i];
            if ((mask & sortMask) != 0) {
                section.start = shiftRights[i] + 7;
                section.shift = shiftRights[i];
                SorterUtilsLong.partitionStableOneGroupBits(array, start, section, aux, startAux, n);
                long[] tempArray = array;
                array = aux;
                aux = tempArray;
                int temp = start;
                start = startAux;
                startAux = temp;
                ops++;

            }
        }

        sortMask = sortMasks[7];
        if ((mask & sortMask) != 0) {
            section.start = shiftRights[7] + 7;
            section.shift = shiftRights[7];
            SorterUtilsLong.partitionStableOneGroupBits(array, start, section, aux, startAux, n);
            array = aux;
            start = startAux;
            ops++;
        }
        if (ops % 2 == 1) {
            System.arraycopy(array, start, arrayOrig, startOrig, n);
        }
    }
}
