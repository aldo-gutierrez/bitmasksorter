package com.aldogg;

import com.aldogg.intType.IntSorter;

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
    public void sort(int[] list) {
        int[] aux = new int[list.length];
        int[] leftX = new int[256];
        {
            int[] count = new int[256];
            for (int i =0; i < list.length; i++) {
                count[list[i] & 0xFF]++;
            }
            leftX[0] = 0;
            for (int i = 1; i < 256; i++) {
                leftX[i] = leftX[i - 1] + count[i - 1];
            }
            for (int i =0; i < list.length; i++) {
                int element = list[i];
                int elementShiftMasked = element & 0xFF;
                aux[leftX[elementShiftMasked]] = element;
                leftX[elementShiftMasked]++;
            }
            System.arraycopy(aux, 0, list, 0, list.length);
        }
        for (int shift =8; shift <= 16; shift+=8) {
            int[] count = new int[256];
            for (int i =0; i < list.length; i++) {
                count[list[i] >>shift & 0xFF]++;
            }
            leftX[0] = 0;
            for (int i = 1; i < 256; i++) {
                leftX[i] = leftX[i - 1] + count[i - 1];
            }
            for (int i =0; i < list.length; i++) {
                int element = list[i];
                int elementShiftMasked = element>>shift & 0xFF;
                aux[leftX[elementShiftMasked]] = element;
                leftX[elementShiftMasked]++;
            }
            System.arraycopy(aux, 0, list, 0, list.length);
        }
        {
            int[] count = new int[256];
            for (int i =0; i < list.length; i++) {
                count[list[i] >>24 & 0xFF]++;
            }
            leftX[0] = 0;
            for (int i = 1; i < 256; i++) {
                leftX[i] = leftX[i - 1] + count[i - 1];
            }
            int lengthPositive = leftX[128];
            for (int i =0; i < list.length; i++) {
                int element = list[i];
                int elementShiftMasked = element>>24 & 0xFF;
                aux[leftX[elementShiftMasked]] = element;
                leftX[elementShiftMasked]++;
            }
            if (unsigned) {
                System.arraycopy(aux, 0, list, 0, list.length);
            } else {
                if (lengthPositive < list.length) {
                    int lengthNegative = list.length - lengthPositive;
                    System.arraycopy(aux, lengthPositive, list, 0, lengthNegative);
                    System.arraycopy(aux, 0, list, lengthNegative, lengthPositive);
                } else {
                    System.arraycopy(aux, 0, list, 0, list.length);
                }
            }
        }
    }
}
