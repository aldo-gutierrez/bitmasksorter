package com.aldogg;

import static com.aldogg.BitSorterUtils.getMask;
import static com.aldogg.BitSorterUtils.getMaskAsList;

public class RadixBitUIntSorter3 extends RadixBitUIntSorter {
    int[] aux2;
    int[] leftX;
    int[] count;

    @Override
    public void sort(int[] list) {
        final int start = 0;
        final int end = list.length;
        int[] maskParts = getMask(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);
        aux2 = new int[list.length];
        aux = aux2;
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
            if (i - 3 > 0) {
                int kListIm3 = kList[i - 3];
                if (kListIm3 == kListI + 3) {
                    int sortMask4 = getMask(kListIm3);
                    sortMask1 = sortMask1 | sortMask4;
                    bits++;
                    imm++;
                }
            }
            if (i - 4 > 0) {
                int kListIm2 = kList[i - 4];
                if (kListIm2 == kListI + 4) {
                    int sortMask5 = getMask(kListIm2);
                    sortMask1 = sortMask1 | sortMask5;
                    bits++;
                    imm++;
                }
            }
            if (i - 5 > 0) {
                int kListIm2 = kList[i - 5];
                if (kListIm2 == kListI + 5) {
                    int sortMask6 = getMask(kListIm2);
                    sortMask1 = sortMask1 | sortMask6;
                    bits++;
                    imm++;
                }
            }
            if (i - 6 > 0) {
                int kListIm2 = kList[i - 6];
                if (kListIm2 == kListI + 6) {
                    int sortMask7 = getMask(kListIm2);
                    sortMask1 = sortMask1 | sortMask7;
                    bits++;
                    imm++;
                }
            }
            if (i - 7 > 0) {
                int kListIm2 = kList[i - 7];
                if (kListIm2 == kListI + 7) {
                    int sortMask8 = getMask(kListIm2);
                    sortMask1 = sortMask1 | sortMask8;
                    bits++;
                    imm++;
                }
            }
            i-=imm;
            if (bits == 1) {
                stablePartition(list, start, end, sortMask1);
            } else {
                stablePartition(list, start, end, sortMask1, bits, 32 - bits - kListI, 32 - bits);
            }
        }
        aux2 = null;
    }

    protected void stablePartition(final int[] list, final int start, final int end, int sortMask, final int bits, final int shiftLeft, final int shiftRight) {
        int lengthBitsToNumber = (int) Math.pow(2, bits);
        leftX = new int[lengthBitsToNumber];
        count = new int[lengthBitsToNumber];
        for (int i = 0; i < lengthBitsToNumber; i++) {
            leftX[i] = 0;
            count[i] = 0;
        }
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
        System.arraycopy(aux2, start, list, start, end - start);
        return;
    }

    private int shiftPre(int i, int shiftLeft, int shiftRight) {
        return i << shiftLeft >>> shiftRight;
    }

}
