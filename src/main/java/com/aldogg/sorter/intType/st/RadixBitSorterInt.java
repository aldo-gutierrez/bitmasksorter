package com.aldogg.sorter.intType.st;

import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.MaskInfo;
import com.aldogg.sorter.Section;
import com.aldogg.sorter.intType.IntBitMaskSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

import static com.aldogg.sorter.BitSorterParams.MAX_BITS_RADIX_SORT;
import static com.aldogg.sorter.intType.IntSorterUtils.*;

public class RadixBitSorterInt extends IntBitMaskSorter {

    @Override
    public void sort(int[] array, int start, int end, int[] kList) {
        if (kList[0] == 31) { //there are negative numbers and positive numbers
            MaskInfo maskInfo;
            int mask;
            int sortMask = 1 << kList[0];
            int finalLeft = isUnsigned()
                    ? partitionNotStable(array, start, end, sortMask)
                    : partitionReverseNotStable(array, start, end, sortMask);
            int n1 = finalLeft - start;
            int n2 = end - finalLeft;
            int[] aux = new int[Math.max(n1, n2)];
            if (n1 > 1) { //sort negative numbers
                maskInfo = MaskInfo.getMaskBit(array, start, finalLeft);
                mask = maskInfo.getMask();
                kList = MaskInfo.getMaskAsArray(mask);
                radixSort(array, start, finalLeft, kList, 0, kList.length - 1, aux, 0);
            }
            if (n2 > 1) { //sort positive numbers
                maskInfo = MaskInfo.getMaskBit(array, finalLeft, end);
                mask = maskInfo.getMask();
                kList = MaskInfo.getMaskAsArray(mask);
                radixSort(array, finalLeft, end, kList, 0, kList.length - 1, aux, 0);
            }
        } else {
            int[] aux = new int[end - start];
            radixSort(array, start, end, kList, 0, kList.length - 1, aux, 0);
        }
    }
    public static void radixSort(int[] array, int start, int end, int[] kList, int kStart, int kEnd, int[] aux, int startAux) {
        Section[] sections = BitSorterUtils.getMaskAsSections(kList, kStart, kEnd);
        int n = end - start;
        if (!(sections.length == 1 && sections[0].length == 1)) {
            for (int i = sections.length - 1; i >= 0; i--) {
                Section section = sections[i];
                Section[] sSections = BitSorterUtils.splitSection(section);
                for (int j = sSections.length - 1; j >= 0; j--) {
                    Section sSection = sSections[j];
                    int twoPowerK = 1 << sSection.length;
                    int[] leftX = new int[twoPowerK];
                    int[] count = new int[twoPowerK];
                    if (sSection.isSectionAtEnd()) {
                        partitionStableLastBits(array, start, aux, startAux, n, sSection, leftX, count);
                    } else {
                        partitionStableOneGroupBits(array, start, aux, startAux, n, sSection, leftX, count);
                    }
                    System.arraycopy(aux, startAux, array, start, n);
                }
            }
        } else {
            partitionStable(array, start, end, sections[0].sortMask, aux, startAux);
        }
    }

}
