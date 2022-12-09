package com.aldogg.sorter.intType.st;

import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.MaskInfo;
import com.aldogg.sorter.Section;
import com.aldogg.sorter.SectionsInfo;
import com.aldogg.sorter.intType.IntBitMaskSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

import static com.aldogg.sorter.intType.IntSorterUtils.*;

public class RadixBitSorterInt extends IntBitMaskSorter {

    @Override
    public void sort(int[] array, int start, int end, int[] kList) {
        if (kList[0] == 31) { //there are negative numbers and positive numbers
            MaskInfo maskInfo;
            int mask;
            int sortMask = 1 << kList[0];
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
            int n1 = finalLeft - start;
            int n2 = end - finalLeft;
            int[] aux = new int[Math.max(n1, n2)];
            if (n1 > 1) { //sort negative numbers
                maskInfo = MaskInfo.getMaskBit(array, start, finalLeft);
                mask = maskInfo.getMask();
                kList = MaskInfo.getMaskAsArray(mask);
                radixSort(array, start, finalLeft, kList, 0, kList.length - 1, aux);
            }
            if (n2 > 1) { //sort positive numbers
                maskInfo = MaskInfo.getMaskBit(array, finalLeft, end);
                mask = maskInfo.getMask();
                kList = MaskInfo.getMaskAsArray(mask);
                radixSort(array, finalLeft, end, kList, 0, kList.length - 1, aux);
            }
        } else {
            int[] aux = new int[end - start];
            radixSort(array, start, end, kList, 0, kList.length - 1, aux);
        }
    }

    public static void radixSort(int[] array, int start, int end, int[] kList, int kStart, int kEnd, int[] aux) {
        SectionsInfo sectionsInfo = BitSorterUtils.getOrderedSections(kList, kStart, kEnd);
        Section[] finalSectionList = sectionsInfo.sections;

        if (finalSectionList.length == 1 && finalSectionList[0].length == 1) {
            partitionStable(array, start, end, finalSectionList[0].sortMask, aux);
            return;
        }

        int maxSectionLength = sectionsInfo.maxLength;
        int n = end - start;
        int[] leftX = new int[1 << maxSectionLength];
        int startAux = 0;
        int ops = 0;
        int[] arrayOrig = array;
        int startOrig = start;
        for (Section section: finalSectionList) {
            leftX[0] = 0;
            if (!section.isSectionAtEnd()) {
                partitionStableOneGroupBits(array, start, section, leftX, aux, startAux, n);
            } else {
                partitionStableLastBits(array, start, section, leftX, aux, startAux, n);
            }

            //System.arraycopy(aux, 0, array, start, n);
            //swap array and aux and start with startAux
            int[] tempArray = array;
            array = aux;
            aux = tempArray;
            int temp = start;
            start = startAux;
            startAux = temp;
            ops++;
        }
        if (ops % 2 == 1) {
            System.arraycopy(array, start, arrayOrig, startOrig, n);
        }
    }

}
