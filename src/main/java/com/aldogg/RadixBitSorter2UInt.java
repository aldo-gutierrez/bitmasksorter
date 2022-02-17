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
            for (int j = 1; j <= 7; j++) {
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
                stablePartition(list, start, end, sortMask1, aux, bits, 32 - bits - kListI, 32 - bits);
            }
        }
    }

    protected void stablePartition(final int[] list, final int start, final int end, int sortMask, final int[] aux2, final int bits, final int shiftLeft, final int shiftRight) {
        int lengthBitsToNumber = (int) Math.pow(2, bits);

        int[] leftX = new int[lengthBitsToNumber];
        int[] count = new int[lengthBitsToNumber];

        sortMask = shiftPre(sortMask, shiftLeft, shiftRight);
        for (int i = start; i < end; i++) {
            int element = list[i];
            int elementShift = shiftPre(element, shiftLeft, shiftRight);
            int elementMasked = elementShift & sortMask;
            count[elementMasked]++;
        }
        for (int i = 1; i < lengthBitsToNumber; i++) {
            leftX[i] = leftX[i - 1] + count[i - 1];
        }

        for (int i = start; i < end; i++) {
            int element = list[i];
            int elementShift = shiftPre(element, shiftLeft, shiftRight);
            int elementMasked = elementShift & sortMask;
            aux2[leftX[elementMasked]] = element;
            leftX[elementMasked]++;
        }
        System.arraycopy(aux2, 0, list, start, end - start);
    }

    private int shiftPre(int i, int shiftLeft, int shiftRight) {
        return i << shiftLeft >>> shiftRight;
    }

}
