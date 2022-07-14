package com.aldogg.sorter.intType.st;

import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.MaskInfo;
import com.aldogg.sorter.Section;
import com.aldogg.sorter.intType.IntBitMaskSorter;

import static com.aldogg.sorter.intType.IntSorterUtils.*;

public class RadixBitSorterInt extends IntBitMaskSorter {

    @Override
    public void sort(int[] array, int start, int end, int[] kList) {
        int[] aux = new int[end - start];
        if (kList[0] == 31) { //there are negative numbers and positive numbers
            MaskInfo maskInfo;
            int mask;
            int sortMask = 1 << kList[0];
            int finalLeft;
            finalLeft = isUnsigned()
                    ? partitionNotStable(array, start, end, sortMask)
                    : partitionReverseNotStable(array, start, end, sortMask);
            if (finalLeft - start > 1) { //sort negative numbers
                maskInfo = MaskInfo.getMaskBit(array, start, finalLeft);
                mask = maskInfo.getMask();
                kList = MaskInfo.getMaskAsArray(mask);
                radixSort(array, start, finalLeft, kList, 0, kList.length - 1, aux, start);
            }
            if (end - finalLeft > 1) { //sort positive numbers
                maskInfo = MaskInfo.getMaskBit(array, finalLeft, end);
                mask = maskInfo.getMask();
                kList = MaskInfo.getMaskAsArray(mask);
                radixSort(array, finalLeft, end, kList, 0, kList.length - 1, aux, finalLeft);
            }
        } else {
            radixSort(array, start, end, kList, 0, kList.length - 1, aux, start);
        }
    }

    public static void radixSort(int[] array, int start, int end, int[] kList, int kStart, int kEnd, int[] aux, int startAux) {

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
                        partitionStableLastBits(array, start, aux, startAux, n, sSection, leftX, count);
                    } else {
                        partitionStableOneGroupBits(array, start, aux, startAux, n, sSection, leftX, count);
                    }
                    System.arraycopy(aux, startAux, array, start, n);
                } else {
                    partitionStable(array, start, end, sSection.sortMask, aux, startAux);
                    //partitionNotStable(array, start, end, sSection.sortMask);
                }
            }
        }
    }

}
