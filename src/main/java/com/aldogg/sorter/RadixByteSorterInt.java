package com.aldogg.sorter;

import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

import static com.aldogg.sorter.BitSorterUtils.*;

public class RadixByteSorterInt implements IntSorter {

    boolean unsigned = false;
    boolean stable = true;
    boolean calculateBitMaskOptimization = true;

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
            int[] maskParts = getMaskBit(array, start, end);
            int mask = maskParts[0] & maskParts[1];
            kList = getMaskAsArray(mask);
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
            int[] maskParts;
            int mask;
            if (kList[0] == 31 && !isUnsigned()) { //sign bit is set and there are negative numbers and positive numbers
                int sortMask = BitSorterUtils.getMaskBit(kList[0]);
                int finalLeft = isUnsigned()
                        ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                        : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
                if (finalLeft - start > 1) { //sort negative numbers
                    //int[] aux = new int[finalLeft - start];
                    maskParts = getMaskBit(array, start, finalLeft);
                    mask = maskParts[0] & maskParts[1];
                    s0 = (mask & 0xFF) != 0;
                    s8 = (mask & 0xFF00) != 0;
                    s16 = (mask & 0xFF0000) != 0;
                    s24 = (mask & 0xFF000000) != 0;
                    sortBytes(array, start, finalLeft, s0, s8, s16, s24);
                }
                if (end - finalLeft > 1) { //sort positive numbers
                    //int[] aux = new int[end - finalLeft];
                    maskParts = getMaskBit(array, finalLeft, end);
                    mask = maskParts[0] & maskParts[1];
                    s0 = (mask & 0xFF) != 0;
                    s8 = (mask & 0xFF00) != 0;
                    s16 = (mask & 0xFF0000) != 0;
                    s24 = (mask & 0xFF000000) != 0;
                    sortBytes(array, finalLeft, end, s0, s8, s16, s24);
                }
                return;
            } else {
                //TODO check if mask should be passed directly
                mask = BitSorterUtils.getMaskLastBits(kList, 0);
                s0 = (mask & 0xFF) != 0;
                s8 = (mask & 0xFF00) != 0;
                s16 = (mask & 0xFF0000) != 0;
                s24 = (mask & 0xFF000000) != 0;
            }
        }
        sortBytes(array, start, end, s0, s8, s16, s24);
    }

    private void sortBytes(int[] array, int start, int end, boolean s0, boolean s8, boolean s16, boolean s24) {
        int n = end - start;
        int[] aux = new int[n];
        int[] leftX = new int[256];
        if (s0) {
            int[] count = new int[256];
            for (int i = start; i < end; i++) {
                count[array[i] & 0xFF]++;
            }
            leftX[0] = 0;
            for (int i = 1; i < 256; i++) {
                leftX[i] = leftX[i - 1] + count[i - 1];
            }
            for (int i = start; i < end; i++) {
                int element = array[i];
                int elementShiftMasked = element & 0xFF;
                aux[leftX[elementShiftMasked]] = element;
                leftX[elementShiftMasked]++;
            }
            System.arraycopy(aux, 0, array, start, n);
        }
        if (s8) {
            int[] count = new int[256];
            for (int i = start; i < end; i++) {
                count[array[i] >> 8 & 0xFF]++;
            }
            leftX[0] = 0;
            for (int i = 1; i < 256; i++) {
                leftX[i] = leftX[i - 1] + count[i - 1];
            }
            for (int i = start; i < end; i++) {
                int element = array[i];
                int elementShiftMasked = element >> 8 & 0xFF;
                aux[leftX[elementShiftMasked]] = element;
                leftX[elementShiftMasked]++;
            }
            System.arraycopy(aux, 0, array, start, n);
        }
        if (s16) {
            int[] count = new int[256];
            for (int i = start; i < end; i++) {
                count[array[i] >> 16 & 0xFF]++;
            }
            leftX[0] = 0;
            for (int i = 1; i < 256; i++) {
                leftX[i] = leftX[i - 1] + count[i - 1];
            }
            for (int i = start; i < end; i++) {
                int element = array[i];
                int elementShiftMasked = element >> 16 & 0xFF;
                aux[leftX[elementShiftMasked]] = element;
                leftX[elementShiftMasked]++;
            }
            System.arraycopy(aux, 0, array, start, n);

        }
        if (s24) {
            int[] count = new int[256];
            for (int i = start; i < end; i++) {
                count[array[i] >> 24 & 0xFF]++;
            }
            leftX[0] = 0;
            for (int i = 1; i < 256; i++) {
                leftX[i] = leftX[i - 1] + count[i - 1];
            }
            int lengthPositive = leftX[128];
            for (int i = start; i < end; i++) {
                int element = array[i];
                int elementShiftMasked = element >> 24 & 0xFF;
                aux[leftX[elementShiftMasked]] = element;
                leftX[elementShiftMasked]++;
            }
            if (unsigned) {
                System.arraycopy(aux, 0, array, start, n);
            } else {
                if (lengthPositive < n) {
                    int lengthNegative = n - lengthPositive;
                    System.arraycopy(aux, lengthPositive, array, start, lengthNegative);
                    System.arraycopy(aux, 0, array, start + lengthNegative, lengthPositive);
                } else {
                    System.arraycopy(aux, 0, array, start, n);
                }
            }
        }
    }
}
