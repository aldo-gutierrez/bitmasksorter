package com.aldogg.sorter;

import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

import static com.aldogg.sorter.BitSorterUtils.*;

public class RadixByteSorterInt implements IntSorter {

    boolean unsigned = false;
    boolean stable = true;

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

        int length = array.length;
        int[] aux = new int[length];
        int[] leftX = new int[256];
        {
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
        for (int shift =8; shift <= 16; shift+=8) {
            int[] count = new int[256];
            for (int i = 0; i < length; i++) {
                count[array[i] >>shift & 0xFF]++;
            }
            leftX[0] = 0;
            for (int i = 1; i < 256; i++) {
                leftX[i] = leftX[i - 1] + count[i - 1];
            }
            for (int i = 0; i < length; i++) {
                int element = array[i];
                int elementShiftMasked = element>>shift & 0xFF;
                aux[leftX[elementShiftMasked]] = element;
                leftX[elementShiftMasked]++;
            }
            System.arraycopy(aux, 0, array, 0, length);
        }
        {
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
