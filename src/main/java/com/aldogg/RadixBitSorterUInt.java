package com.aldogg;

import static com.aldogg.BitSorterUtils.getMaskBit;
import static com.aldogg.BitSorterUtils.getMaskAsList;

public class RadixBitSorterUInt extends RadixBitSorterUIntBase {

    @Override
    public void sort(int[] list) {
        if (list.length < 2) {
            return;
        }
        final int start = 0;
        final int end = list.length;
        int[] maskParts = getMaskBit(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);
        if (kList.length == 0) {
            return;
        }
        int length = end - start;
        int[] aux = new int[length];
        radixSort(list, start, end, aux, kList, kList.length - 1, 0);
    }

    public void radixSort(int[] list, int start, int end, int[] aux, int[] kList, int kIndexStart, int kIndexEnd) {
        for (int i = kIndexStart; i >= kIndexEnd; i--) {
            int kListI = kList[i];
            int sortMask1 = BitSorterUtils.getMaskBit(kListI);
            int bits = 1;
            int imm = 0;
            for (int j = 1; j <= 11; j++) { //11bits looks faster than 8 on AMD 4800H, 15 is slower
                if (i - j >= kIndexEnd) {
                    int kListIm1 = kList[i - j];
                    if (kListIm1 == kListI + j) {
                        int sortMask2 = BitSorterUtils.getMaskBit(kListIm1);
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
                IntSorterUtils.partitionStable(list, start, end, sortMask1, aux);
            } else {
                int lengthBitsToNumber = BitSorterParams.twoPowerX(bits);
                if (kListI == 0) {
                    partitionStableLastBits(list, start, end, lengthBitsToNumber, aux, sortMask1);
                } else {
                    partitionStableGroupBits(list, start, end, lengthBitsToNumber, aux, sortMask1, kListI);
                }
            }
        }
    }

    /**
     *  CPU: 3*N + 2^K
     *  MEM: N + 2*2^K
     */
    private void partitionStableLastBits(int[] list, int start, int end, int lengthBitsToNumber, int[] aux, int sortMask) {
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

    /**
     *  CPU: 3*N + 2^K
     *  MEM: N + 2*2^K
     */
    private void partitionStableGroupBits(int[] list, int start, int end, int lengthBitsToNumber, int[] aux, int sortMask, int shiftRight) {
        int[] leftX = new int[lengthBitsToNumber];
        int[] count = new int[lengthBitsToNumber];
        for (int i = start; i < end; i++) {
            int element = list[i];
            int elementShiftMasked = (element & sortMask) >>> shiftRight;
            count[elementShiftMasked]++;
        }
        for (int i = 1; i < lengthBitsToNumber; i++) {
            leftX[i] = leftX[i - 1] + count[i - 1];
        }
        for (int i = start; i < end; i++) {
            int element = list[i];
            int elementShiftMasked = (element & sortMask) >>> shiftRight;
            aux[leftX[elementShiftMasked]] = element;
            leftX[elementShiftMasked]++;
        }
        System.arraycopy(aux, 0, list, start, end - start);
    }

}
