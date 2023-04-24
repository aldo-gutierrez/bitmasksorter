package com.aldogg.sorter.double_.st;

import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.LongSection;
import com.aldogg.sorter.LongSectionsInfo;
import com.aldogg.sorter.MaskInfoLong;
import com.aldogg.sorter.double_.DoubleBitMaskSorter;

import static com.aldogg.sorter.double_.DoubleSorterUtils.*;
import static com.aldogg.sorter.long_.LongSorter.LONG_SIGN_BIT_POS;

public class RadixBitSorterDouble extends DoubleBitMaskSorter {

    @Override
    public void sort(double[] array, int start, int endP1, int[] kList) {
        if (kList[0] == LONG_SIGN_BIT_POS) { //there are negative numbers and positive numbers
            MaskInfoLong maskInfo;
            long mask;
            long sortMask = 1L << kList[0];
            int finalLeft = partitionReverseNotStable(array, start, endP1, sortMask);
            int n1 = finalLeft - start;
            int n2 = endP1 - finalLeft;
            double[] aux = new double[Math.max(n1, n2)];
            if (n1 > 1) { //sort negative numbers
                maskInfo = MaskInfoLong.getMaskInfo(array, start, finalLeft);
                mask = maskInfo.getMask();
                kList = MaskInfoLong.getMaskAsArray(mask);
                radixSort(array, start, finalLeft, kList, 0, kList.length - 1, aux);
                reverse(array, start, finalLeft);
            }
            if (n2 > 1) { //sort positive numbers
                maskInfo = MaskInfoLong.getMaskInfo(array, finalLeft, endP1);
                mask = maskInfo.getMask();
                kList = MaskInfoLong.getMaskAsArray(mask);
                radixSort(array, finalLeft, endP1, kList, 0, kList.length - 1, aux);
            }
        } else {
            double[] aux = new double[endP1 - start];
            radixSort(array, start, endP1, kList, 0, kList.length - 1, aux);
            if (array[0] < 0) { //all negative numbers
                reverse(array, start, endP1);
            }
        }
    }

    public static void radixSort(double[] array, int start, int endP1, int[] kList, int kStart, int kEnd, double[] aux) {
        LongSectionsInfo sectionsInfo = BitSorterUtils.getOrderedSectionsLong(kList, kStart, kEnd);
        LongSection[] finalSectionList = sectionsInfo.sections;

        if (finalSectionList.length == 1 && finalSectionList[0].length == 1) {
            partitionStable(array, start, endP1, finalSectionList[0].sortMask, aux);
            return;
        }

        int n = endP1 - start;
        int startAux = 0;
        int ops = 0;
        double[] arrayOrig = array;
        int startOrig = start;
        for (LongSection section : finalSectionList) {
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

