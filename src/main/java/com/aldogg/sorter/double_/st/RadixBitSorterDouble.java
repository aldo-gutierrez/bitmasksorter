package com.aldogg.sorter.double_.st;

import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.MaskInfoLong;
import com.aldogg.sorter.Section;
import com.aldogg.sorter.double_.DoubleBitMaskSorter;

import static com.aldogg.sorter.double_.DoubleSorterUtils.*;
import static com.aldogg.sorter.MaskInfoLong.UPPER_BIT;

public class RadixBitSorterDouble extends DoubleBitMaskSorter {

    @Override
    public void sort(double[] array, int start, int endP1, int[] bList) {
        if (bList[0] == UPPER_BIT) { //there are negative numbers and positive numbers
            MaskInfoLong maskInfo;
            long mask;
            long sortMask = 1L << bList[0];
            int finalLeft = partitionReverseNotStable(array, start, endP1, sortMask);
            int n1 = finalLeft - start;
            int n2 = endP1 - finalLeft;
            double[] aux = new double[Math.max(n1, n2)];
            if (n1 > 1) { //sort negative numbers
                maskInfo = MaskInfoLong.calculateMask(array, start, finalLeft);
                mask = maskInfo.getMask();
                bList = MaskInfoLong.getMaskAsArray(mask);
                radixSort(array, start, finalLeft, bList, 0, bList.length - 1, aux);
                reverse(array, start, finalLeft);
            }
            if (n2 > 1) { //sort positive numbers
                maskInfo = MaskInfoLong.calculateMask(array, finalLeft, endP1);
                mask = maskInfo.getMask();
                bList = MaskInfoLong.getMaskAsArray(mask);
                radixSort(array, finalLeft, endP1, bList, 0, bList.length - 1, aux);
            }
        } else {
            double[] aux = new double[endP1 - start];
            radixSort(array, start, endP1, bList, 0, bList.length - 1, aux);
            if (array[0] < 0) { //all negative numbers
                reverse(array, start, endP1);
            }
        }
    }

    public static void radixSort(double[] array, int start, int endP1, int[] bList, int bListStart, int bListEnd, double[] aux) {
        Section[] finalSectionList = BitSorterUtils.getOrderedSections(bList, bListStart, bListEnd);

        if (finalSectionList.length == 1 && finalSectionList[0].length == 1) {
            Section section = finalSectionList[0];
            long mask = MaskInfoLong.getMaskRangeBits(section.start, section.shift);
            partitionStable(array, start, endP1, mask, aux);
            return;
        }

        int n = endP1 - start;
        int startAux = 0;
        int ops = 0;
        double[] arrayOrig = array;
        int startOrig = start;
        for (Section section : finalSectionList) {
            if (!section.isSectionAtEnd()) {
                partitionStableOneGroupBits(array, start, section, aux, startAux, n);
            } else {
                partitionStableLastBits(array, start, section, aux, startAux, n);
            }
            //System.arraycopy(aux, 0, array, start, n);
            //swap array and aux and start with startAux
            double[] tempArray = array;
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

