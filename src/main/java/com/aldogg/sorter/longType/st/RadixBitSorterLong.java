package com.aldogg.sorter.longType.st;

import com.aldogg.sorter.*;
import com.aldogg.sorter.longType.LongBitMaskSorter;

import static com.aldogg.sorter.longType.LongSorterUtils.*;

public class RadixBitSorterLong extends LongBitMaskSorter {

    @Override
    public void sort(long[] array, int start, int end, int[] kList) {
        if (kList[0] == 63) { //there are negative numbers and positive numbers
            MaskInfoLong maskInfo;
            long mask;
            long sortMask = 1L << kList[0];
            int finalLeft = isUnsigned()
                    ? partitionNotStable(array, start, end, sortMask)
                    : partitionReverseNotStable(array, start, end, sortMask);
            int n1 = finalLeft - start;
            int n2 = end - finalLeft;
            long[] aux = new long[Math.max(n1, n2)];
            if (n1 > 1) { //sort negative numbers
                maskInfo = MaskInfoLong.getMaskBit(array, start, finalLeft);
                mask = maskInfo.getMask();
                kList = MaskInfoLong.getMaskAsArray(mask);
                radixSort(array, start, finalLeft, kList, 0, kList.length - 1, aux);
            }
            if (n2 > 1) { //sort positive numbers
                maskInfo = MaskInfoLong.getMaskBit(array, finalLeft, end);
                mask = maskInfo.getMask();
                kList = MaskInfoLong.getMaskAsArray(mask);
                radixSort(array, finalLeft, end, kList, 0, kList.length - 1, aux);
            }
        } else {
            long[] aux = new long[end - start];
            radixSort(array, start, end, kList, 0, kList.length - 1, aux);
        }
    }

    public static void radixSort(long[] array, int start, int end, int[] kList, int kStart, int kEnd, long[] aux) {
        LongSectionsInfo sectionsInfo = BitSorterUtils.getOrderedSectionsLong(kList, kStart, kEnd);
        LongSection[] finalSectionList = sectionsInfo.sections;

        if (finalSectionList.length == 1 && finalSectionList[0].length == 1) {
            partitionStable(array, start, end, finalSectionList[0].sortMask, aux);
            return;
        }

        int n = end - start;
        int startAux = 0;
        int ops = 0;
        long[] arrayOrig = array;
        int startOrig = start;
        for (LongSection section : finalSectionList) {
            if (!section.isSectionAtEnd()) {
                if (startAux == 0) {
                    partitionStableOneGroupBits(array, start, section, aux, n);
                } else {
                    partitionStableOneGroupBits(array, start, section, aux, startAux, n);
                }
            } else {
                if (startAux == 0) {
                    partitionStableLastBits(array, start, section, aux, n);
                } else {
                    partitionStableLastBits(array, start, section, aux, startAux, n);
                }
            }

            //System.arraycopy(aux, 0, array, start, n);
            //swap array and aux and start with startAux
            long[] tempArray = array;
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
