package com.aldogg.sorter.long_.st;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.LongSection;
import com.aldogg.sorter.MaskInfoLong;
import com.aldogg.sorter.long_.LongBitMaskSorter;
import com.aldogg.sorter.long_.LongSorterUtils;

import static com.aldogg.sorter.long_.LongSorterUtils.listIsOrderedSigned;
import static com.aldogg.sorter.long_.LongSorterUtils.listIsOrderedUnSigned;

public class RadixByteSorterLong extends LongBitMaskSorter {

    boolean calculateBitMaskOptimization = true;

    public void setCalculateBitMaskOptimization(boolean calculateBitMaskOptimization) {
        this.calculateBitMaskOptimization = calculateBitMaskOptimization;
    }

    @Override
    public void sort(long[] array, final int start, final int end) {
        int n = end - start;
        if (n < 2) {
            return;
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, end) : listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            LongSorterUtils.reverse(array, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        int[] kList = null;

        if (calculateBitMaskOptimization) {
            MaskInfoLong maskInfo = MaskInfoLong.getMaskBit(array, start, end);
            long mask = maskInfo.getMask();
            kList = MaskInfoLong.getMaskAsArray(mask);
            if (kList.length == 0) {
                return;
            }
        }
        sort(array, start, end, kList);
    }

    @Override
    public void sort(long[] array, int start, int end, int[] kList) {
        long mask = 0xFFFFFFFFFFFFFFFFL;
        if (calculateBitMaskOptimization) {
            if (kList.length == 0) {
                return;
            }
            MaskInfoLong maskParts;
            if (kList[0] == LONG_SIGN_BIT_POS && !isUnsigned()) { //sign bit is set and there are negative numbers and positive numbers
                int sortMask = 1 << kList[0];
                int finalLeft = isUnsigned()
                        ? LongSorterUtils.partitionNotStable(array, start, end, sortMask)
                        : LongSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
                int n1 = finalLeft - start;
                int n2 = end - finalLeft;
                long[] aux = new long[Math.max(n1, n2)];
                if (n1 > 1) { //sort negative numbers
                    maskParts = MaskInfoLong.getMaskBit(array, start, finalLeft);
                    mask = maskParts.getMask();
                    sortBytes(array, start, finalLeft, aux, mask);
                }
                if (n2 > 1) { //sort positive numbers
                    maskParts = MaskInfoLong.getMaskBit(array, finalLeft, end);
                    mask = maskParts.getMask();
                    sortBytes(array, finalLeft, end, aux, mask);
                }
                return;
            } else {
                mask = MaskInfoLong.getMaskLastBits(kList, 0);
            }
        }
        int n = end - start;
        long[] aux = new long[n];
        sortBytes(array, start, end, aux, mask);
    }

    private void sortBytes(long[] array, int start, int end, long[] aux, long mask) {
        int n = end - start;
        LongSection section = new LongSection();
        section.length = 8;
        int ops = 0;
        long[] arrayOrig = array;
        int startOrig = start;
        int startAux = 0;

        int[] shiftRights = new int[]{0, 8, 16, 24, 32, 40, 48, 56};
        long[] sortMasks = new long[]{0xFFL, 0xFF00L, 0xFF0000L, 0xFF000000L, 0xFF00000000L, 0xFF0000000000L, 0xFF000000000000L, 0xFF00000000000000L};

        long sortMask = sortMasks[0];
        if ((mask & sortMask) != 0) {
            section.sortMask = sortMask;
            LongSorterUtils.partitionStableLastBits(array, start, section, aux, startAux, n);

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
                section.sortMask = sortMask;
                section.shiftRight = shiftRights[i];
                LongSorterUtils.partitionStableOneGroupBits(array, start, section, aux, startAux, n);
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
            section.sortMask = sortMask;
            section.shiftRight = shiftRights[7];
            LongSorterUtils.partitionStableOneGroupBits(array, start, section, aux, startAux, n);
            array = aux;
            start = startAux;
            ops++;
        }
        if (ops % 2 == 1) {
            System.arraycopy(array, start, arrayOrig, startOrig, n);
        }
    }
}
