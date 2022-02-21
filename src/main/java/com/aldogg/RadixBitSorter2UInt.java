package com.aldogg;

import static com.aldogg.BitSorterUtils.getMask;
import static com.aldogg.BitSorterUtils.getMaskAsList;

public class RadixBitSorter2UInt extends RadixBitSorterUInt {

    @Override
    public void sort(int[] list) {
        final int start = 0;
        final int end = list.length;
        int[] maskParts = getMask(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);
        radixSort(list, start, end, kList, kList.length - 1, 0);
    }

    protected void radixSort(int[] list, int start, int end, int[] kList, int kIndexStart, int kIndexEnd) {
        int length = end - start;
        int[] aux = new int[length];
        for (int i = kIndexStart; i >= kIndexEnd; i--) {
            int kListI = kList[i];
            int sortMask1 = getMask(kListI);
            int bits = 1;
            int imm = 0;
            for (int j = 1; j <= 11; j++) { //11bits looks faster than 8 on AMD 4800H, 15 is slower
                if (i - j >= kIndexEnd) {
                    int kListIm1 = kList[i - j];
                    if (kListIm1 == kListI + j) {
                        int sortMask2 = getMask(kListIm1);
                        sortMask1 = sortMask1 | sortMask2;
                        bits++;
                        imm++;
                    } else {
                        break;
                    }
                }
            }
            i -= imm;
            if (bits == 1) {
                stablePartition(list, start, end, sortMask1, aux);
            } else {
                int lengthBitsToNumber = (int) Math.pow(2, bits);
                int shiftLeft =  32 - bits - kListI;
                int shiftRight = 32 - bits;
                boolean eqShift = shiftLeft == shiftRight;
                if (eqShift) {
                    stablePartition1(list, start, end, lengthBitsToNumber, aux, sortMask1);
                } else {
                    stablePartition2(list, start, end, lengthBitsToNumber, aux, shiftLeft, shiftRight);
                }
            }
        }
    }

    private void stablePartition2(int[] list, int start, int end, int lengthBitsToNumber, int[] aux, int shiftLeft, int shiftRight) {
        int[] leftX = new int[lengthBitsToNumber];
        int[] count = new int[lengthBitsToNumber];
        for (int i = start; i < end; i++) {
            int element = list[i];
            int elementShiftMasked = shiftPre(element, shiftLeft, shiftRight);
            count[elementShiftMasked]++;
        }
        for (int i = 1; i < lengthBitsToNumber; i++) {
            leftX[i] = leftX[i - 1] + count[i - 1];
        }
        for (int i = start; i < end; i++) {
            int element = list[i];
            int elementShiftMasked = shiftPre(element, shiftLeft, shiftRight);
            aux[leftX[elementShiftMasked]] = element;
            leftX[elementShiftMasked]++;
        }
        System.arraycopy(aux, 0, list, start, end - start);
    }

    private void stablePartition1(int[] list, int start, int end, int lengthBitsToNumber, int[] aux, int sortMask) {
        int[] leftX = new int[lengthBitsToNumber];
        int[] count = new int[lengthBitsToNumber];
        for (int i = start; i < end; i++) {
            int elementShiftMasked = list[i] & sortMask;
            count[elementShiftMasked]++;
        }
        for (int i = 1; i < lengthBitsToNumber; i++) {
            leftX[i] = leftX[i - 1] + count[i - 1];
        }
        for (int i = start; i < end; i++) {
            int element = list[i];
            int elementShiftMasked = element & sortMask;
            aux[leftX[elementShiftMasked]] = element;
            leftX[elementShiftMasked]++;
        }
        System.arraycopy(aux, 0, list, start, end - start);
    }

    private int shiftPre(int i, int shiftLeft, int shiftRight) {
        return i << shiftLeft >>> shiftRight;
    }

}
