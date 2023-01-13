package com.aldogg.sorter.doubleType.st;

import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.LongSection;
import com.aldogg.sorter.LongSectionsInfo;
import com.aldogg.sorter.MaskInfoLong;
import com.aldogg.sorter.doubleType.DoubleBitMaskSorter;

import static com.aldogg.sorter.doubleType.DoubleSorterUtils.*;

public class RadixBitSorterDouble extends DoubleBitMaskSorter {

    @Override
    public void sort(double[] array, int start, int end, int[] kList) {
        if (kList[0] == 63) { //there are negative numbers and positive numbers
            MaskInfoLong maskInfo;
            long mask;
            long sortMask = 1L << kList[0];
            int finalLeft = partitionReverseNotStable(array, start, end, sortMask);
            int n1 = finalLeft - start;
            int n2 = end - finalLeft;
            double[] aux = new double[Math.max(n1, n2)];
            if (n1 > 1) { //sort negative numbers
                maskInfo = MaskInfoLong.getMaskBit(array, start, finalLeft);
                mask = maskInfo.getMask();
                kList = MaskInfoLong.getMaskAsArray(mask);
                radixSort(array, start, finalLeft, kList, 0, kList.length - 1, aux);
                reverse(array, start, finalLeft);
            }
            if (n2 > 1) { //sort positive numbers
                maskInfo = MaskInfoLong.getMaskBit(array, finalLeft, end);
                mask = maskInfo.getMask();
                kList = MaskInfoLong.getMaskAsArray(mask);
                radixSort(array, finalLeft, end, kList, 0, kList.length - 1, aux);
            }
        } else {
            double[] aux = new double[end - start];
            radixSort(array, start, end, kList, 0, kList.length - 1, aux);
            if (array[0] < 0) { //all negative numbers
                reverse(array, start, end);
            }
        }
    }

    public static void radixSort(double[] array, int start, int end, int[] kList, int kStart, int kEnd, double[] aux) {
        LongSectionsInfo sectionsInfo = BitSorterUtils.getOrderedSectionsLong(kList, kStart, kEnd);
        LongSection[] finalSectionList = sectionsInfo.sections;

        if (finalSectionList.length == 1 && finalSectionList[0].length == 1) {
            partitionStable(array, start, end, finalSectionList[0].sortMask, aux);
            return;
        }

        int maxSectionLength = sectionsInfo.maxLength;
        int n = end - start;
        int[] leftX = new int[1 << maxSectionLength];
        int startAux = 0;
        int ops = 0;
        double[] arrayOrig = array;
        int startOrig = start;
        for (LongSection section : finalSectionList) {
            leftX[0] = 0;
            if (!section.isSectionAtEnd()) {
                if (startAux == 0) {
                    partitionStableOneGroupBits(array, start, section, leftX, aux, n);
                } else {
                    partitionStableOneGroupBits(array, start, section, leftX, aux, startAux, n);
                }
            } else {
                if (startAux == 0) {
                    partitionStableLastBits(array, start, section, leftX, aux, n);
                } else {
                    partitionStableLastBits(array, start, section, leftX, aux, startAux, n);
                }
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

