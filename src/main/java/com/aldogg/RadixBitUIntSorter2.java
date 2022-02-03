package com.aldogg;

import static com.aldogg.BitSorterUtils.getMask;
import static com.aldogg.BitSorterUtils.getMaskAsList;

public class RadixBitUIntSorter2  extends  RadixBitUIntSorter{

    @Override
    public void sort(int[] list) {
        final int start = 0;
        final int end = list.length;
        int[] maskParts = getMask(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);
        int[][] aux2 = new int[8][];
        for (int i = 0; i < 8; i++) {
            try {
                aux2[i] = new int[list.length];
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
        int[] aux = aux2[0];
        for (int i = kList.length - 1; i >= 0; i--) {
            int kListI = kList[i];
            int sortMask1 = getMask(kListI);
            int bits = 1;
            int imm = 0;
            if (i - 1 > 0) {
                int kListIm1 = kList[i - 1];
                if (kListIm1 == kListI + 1) {
                    int sortMask2 = getMask(kListIm1);
                    sortMask1 = sortMask1 | sortMask2;
                    bits++;
                    imm++;
                }
            }
            if (i - 2 > 0) {
                int kListIm2 = kList[i - 2];
                if (kListIm2 == kListI + 2) {
                    int sortMask3 = getMask(kListIm2);
                    sortMask1 = sortMask1 | sortMask3;
                    bits++;
                    imm++;
                }
            }
            i-=imm;
            if (bits == 1) {
                stablePartition(list, start, end, sortMask1, aux);
            } else {
                stablePartition(list, start, end, sortMask1, aux2, bits, 32 - bits - kListI, 32 - bits);
            }
        }
    }

    protected int stablePartition(final int[] list, final int start, final int end, int sortMask, int[][] aux2, final int bits, final int shiftLeft, final int shiftRight) {
        int lengthBitsToNumber = (int) Math.pow(2, bits);
        int[] leftX = new int[8];

        sortMask = shiftPre(sortMask, shiftLeft, shiftRight);
        int left = start;
        for (int i = start; i < end; i++) {
            int element = list[i];
            int elementShift = shiftPre(element, shiftLeft, shiftRight);
            int elementMasked = elementShift & sortMask;
            aux2[elementMasked][leftX[elementMasked]] = element;
            leftX[elementMasked]++;
        }
        for (int i = 0; i < lengthBitsToNumber; i++) {
            System.arraycopy(aux2[i], 0, list, left, leftX[i]);
            left += leftX[i];
        }
        return left;
    }

    private int shiftPre(int i, int shiftLeft, int shiftRight) {
        return i << shiftLeft >>> shiftRight;
    }

}
