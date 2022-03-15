package com.aldogg;

import com.aldogg.intType.IntSorter;
import com.aldogg.intType.IntSorterUtils;

import static com.aldogg.BitSorterUtils.getMaskBit;
import static com.aldogg.BitSorterUtils.getMaskAsList;
import static com.aldogg.intType.IntSorterUtils.partitionStableGroupBits;
import static com.aldogg.intType.IntSorterUtils.partitionStableLastBits;

public class RadixBitSorterInt implements IntSorter {

    boolean unsigned = false;

    @Override
    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

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
        if (!isUnsigned() && kList[0] == 31) { //there are negative numbers and positive numbers
            int sortMask = BitSorterUtils.getMaskBit(kList[0]);
            int finalLeft = IntSorterUtils.partitionReverseNotStable(list, start, end, sortMask);
            if (finalLeft - start > 1) { //sort negative numbers
                int[] aux = new int[finalLeft - start];
                maskParts = getMaskBit(list, start, finalLeft);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsList(mask);
                radixSort(list, start, finalLeft, aux, kList, kList.length - 1, 0);
            }
            if (end - finalLeft > 1) { //sort positive numbers
                int[] aux = new int[end - finalLeft];
                maskParts = getMaskBit(list, finalLeft, end);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsList(mask);
                radixSort(list, finalLeft, end, aux, kList, kList.length - 1, 0);
            }
        } else {
            int[] aux = new int[end - start];
            radixSort(list, start, end, aux, kList, kList.length - 1, 0);
        }
    }

    public static void radixSort(int[] list, int start, int end, int[] aux, int[] kList, int kIndexStart, int kIndexEnd) {
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
                int lengthBitsToNumber = BitSorterUtils.twoPowerX(bits);
                if (kListI == 0) {
                    partitionStableLastBits(list, start, end, lengthBitsToNumber, aux, sortMask1);
                } else {
                    partitionStableGroupBits(list, start, end, lengthBitsToNumber, aux, sortMask1, kListI);
                }
            }
        }
    }

}
