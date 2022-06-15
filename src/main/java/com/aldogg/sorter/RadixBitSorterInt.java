package com.aldogg.sorter;

import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.intType.IntSorterUtils.partitionStableGroupBits;
import static com.aldogg.sorter.intType.IntSorterUtils.partitionStableLastBits;

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
    public void sort(int[] array, int start, int end) {
        int n = end - start;
        if (n < 2) {
            return;
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, end) : listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(array, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        int[] maskParts = getMaskBit(array, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsArray(mask);
        if (kList.length == 0) {
            return;
        }
        sort(array, start, end, kList);
    }

    @Override
    public void sort(int[] array, int start, int end, int[] kList) {
        if (kList[0] == 31) { //there are negative numbers and positive numbers
            int[] maskParts;
            int mask;
            int sortMask = 1 << kList[0];
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
            if (finalLeft - start > 1) { //sort negative numbers
                int[] aux = new int[finalLeft - start];
                maskParts = getMaskBit(array, start, finalLeft);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsArray(mask);
                radixSort(array, start, finalLeft, kList, kList.length - 1, 0, aux);
            }
            if (end - finalLeft > 1) { //sort positive numbers
                int[] aux = new int[end - finalLeft];
                maskParts = getMaskBit(array, finalLeft, end);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsArray(mask);
                radixSort(array, finalLeft, end, kList, kList.length - 1, 0, aux);
            }
        } else {
            int[] aux = new int[end - start];
            radixSort(array, start, end, kList, kList.length - 1, 0, aux);
        }
    }

    public static void radixSort(int[] array, int start, int end, int[] kList, int kIndexStart, int kIndexEnd, int[] aux) {
        for (int i = kIndexStart; i >= kIndexEnd; i--) {
            int kListI = kList[i];
            int maskI = 1 << kListI;
            int bits = 1;
            int imm = 0;
            for (int j = 1; j <= 11; j++) { //11bits looks faster than 8 on AMD 4800H, 15 is slower
                if (i - j >= kIndexEnd) {
                    int kListIm1 = kList[i - j];
                    if (kListIm1 == kListI + j) {
                        int maskIm1 = 1 << kListIm1;
                        maskI = maskI | maskIm1;
                        bits++;
                        imm++;
                    } else {
                        break;
                    }
                }
            }
            i -= imm;
            if (bits == 1) {
                IntSorterUtils.partitionStable(array, start, end, maskI, aux);
            } else {
                int twoPowerBits = 1 << bits;
                if (kListI == 0) {
                    partitionStableLastBits(array, start, end, maskI, twoPowerBits, aux);
                } else {
                    partitionStableGroupBits(array, start, end, maskI, kListI, twoPowerBits, aux);
                }
            }
        }
    }

}
