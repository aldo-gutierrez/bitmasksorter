package com.aldogg.sorter.intType.st;

import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.Section;
import com.aldogg.sorter.intType.IntBitMaskSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

import static com.aldogg.sorter.BitSorterParams.MAX_BITS_RADIX_SORT;
import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.intType.IntSorterUtils.*;

public class RadixBitSorterInt extends IntBitMaskSorter {

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
                radixSort(array, start, finalLeft, kList, 0, kList.length - 1, aux);
            }
            if (end - finalLeft > 1) { //sort positive numbers
                int[] aux = new int[end - finalLeft];
                maskParts = getMaskBit(array, finalLeft, end);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsArray(mask);
                radixSort(array, finalLeft, end, kList, 0, kList.length - 1, aux);
            }
        } else {
            int[] aux = new int[end - start];
            radixSort(array, start, end, kList, 0, kList.length - 1, aux);
        }
    }

    public static void radixSort(int[] array, int start, int end, int[] kList, int kStart, int kEnd, int[] aux) {

        Section[] sections = BitSorterUtils.getMaskAsSections(kList, kStart, kEnd);
        int n = end - start;
        for (int i = sections.length - 1; i >= 0; i--) {
            Section section = sections[i];
            Section[] sSections = BitSorterUtils.splitSection(section);
            for (int j = sSections.length - 1; j >= 0; j--) {
                Section sSection = sSections[j];
                if (sSection.length > 1) {
                    int twoPowerK = 1 << sSection.length;
                    int[] leftX = new int[twoPowerK];
                    int[] count = new int[twoPowerK];
                    if (sSection.isSectionAtEnd()) {
                        partitionStableLastBits(array, start, end, sSection, leftX, count, aux);
                    } else {
                        partitionStableOneGroupBits(array, start, end, sSection, leftX, count, aux);
                    }
                    System.arraycopy(aux, 0, array, start, n);
                } else {
                    partitionStable(array, start, end, sSection.sortMask, aux);
                }
            }
        }
    }

    public static void radixSortOld(int[] array, int start, int end, int[] kList, int kStart, int kEnd, int[] aux) {
        for (int i = kEnd; i >= kStart; i--) {
            int kListI = kList[i];
            int maskI = 1 << kListI;
            int bits = 1;
            int imm = 0;
            for (int j = 1; j < MAX_BITS_RADIX_SORT; j++) {
                if (i - j >= kStart) {
                    int kListIm1 = kList[i - j];
                    if (kListIm1 == kListI + j) {
                        maskI = maskI | 1 << kListIm1;
                        bits++;
                        imm++;
                    } else {
                        break;
                    }
                }
            }
            i -= imm;
            Section sSection = new Section();
            sSection.sortMask = maskI;
            sSection.length = bits;
            if (bits == 1) {
                partitionStable(array, start, end, sSection.sortMask, aux);
            } else {
                int twoPowerK = 1 << sSection.length;
                int[] leftX = new int[twoPowerK];
                int[] count = new int[twoPowerK];

                if (kListI == 0) {
                    partitionStableLastBits(array, start, end, sSection, leftX, count, aux);
                } else {
                    sSection.shiftRight = kListI;
                    partitionStableOneGroupBits(array, start, end, sSection, leftX, count, aux);
                }
                System.arraycopy(aux, 0, array, start, end - start);
            }
        }
    }

}
