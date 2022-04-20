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
    public void sort(int[] array) {
        if (array.length < 2) {
            return;
        }
        final int start = 0;
        final int end = array.length;
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, end) : listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(array, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        boolean s0 = true;
        boolean s8 = true;
        boolean s16 = true;
        boolean s24 = true;
        if (calculateBitMaskOptimization) {
            int[] maskParts = getMaskBit(array, start, end);
            int mask = maskParts[0] & maskParts[1];
            int[] kList = getMaskAsList(mask);

            if (kList.length == 0) {
                return;
            }
            s0 = (mask & 0xFF) != 0;
            s8 = (mask & 0xFF00) != 0;
            s16 = (mask & 0xFF0000) != 0;
            s24 = (mask & 0xFF000000) != 0;
        }

        int length = array.length;
        int[] aux = new int[length];
        int[] leftX = new int[256];
        if (s0) {
            int[] count = new int[256];
            for (int i = 0; i < length; i++) {
                count[array[i] & 0xFF]++;
            }
            leftX[0] = 0;
            for (int i = 1; i < 256; i++) {
                leftX[i] = leftX[i - 1] + count[i - 1];
            }
            for (int i = 0; i < length; i++) {
                int element = array[i];
                int elementShiftMasked = element & 0xFF;
                aux[leftX[elementShiftMasked]] = element;
                leftX[elementShiftMasked]++;
            }
            System.arraycopy(aux, 0, array, 0, length);
        }
        if (s8){
            int[] count = new int[256];
            for (int i = 0; i < length; i++) {
                count[array[i] >> 8 & 0xFF]++;
            }
            leftX[0] = 0;
            for (int i = 1; i < 256; i++) {
                leftX[i] = leftX[i - 1] + count[i - 1];
            }
            for (int i = 0; i < length; i++) {
                int element = array[i];
                int elementShiftMasked = element >> 8 & 0xFF;
                aux[leftX[elementShiftMasked]] = element;
                leftX[elementShiftMasked]++;
            }
            System.arraycopy(aux, 0, array, 0, length);
        }
        if (s16) {
            int[] count = new int[256];
            for (int i = 0; i < length; i++) {
                count[array[i] >> 16 & 0xFF]++;
            }
            leftX[0] = 0;
            for (int i = 1; i < 256; i++) {
                leftX[i] = leftX[i - 1] + count[i - 1];
            }
            for (int i = 0; i < length; i++) {
                int element = array[i];
                int elementShiftMasked = element >> 16 & 0xFF;
                aux[leftX[elementShiftMasked]] = element;
                leftX[elementShiftMasked]++;
            }
            System.arraycopy(aux, 0, array, 0, length);

        }
        if (s24) {
            int[] count = new int[256];
            for (int i = 0; i < length; i++) {
                count[array[i] >>24 & 0xFF]++;
            }
            leftX[0] = 0;
            for (int i = 1; i < 256; i++) {
                leftX[i] = leftX[i - 1] + count[i - 1];
            }
            int lengthPositive = leftX[128];
            for (int i = 0; i < length; i++) {
                int element = array[i];
                int elementShiftMasked = element>>24 & 0xFF;
                aux[leftX[elementShiftMasked]] = element;
                leftX[elementShiftMasked]++;
            }
            if (unsigned) {
                System.arraycopy(aux, 0, array, 0, length);
            } else {
                if (lengthPositive < length) {
                    int lengthNegative = length - lengthPositive;
                    System.arraycopy(aux, lengthPositive, array, 0, lengthNegative);
                    System.arraycopy(aux, 0, array, lengthNegative, lengthPositive);
                } else {
                    System.arraycopy(aux, 0, array, 0, length);
                }
            }
        }
    }
}
