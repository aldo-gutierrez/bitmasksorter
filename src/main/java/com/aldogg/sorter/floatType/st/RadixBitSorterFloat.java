package com.aldogg.sorter.floatType.st;

import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.IntSection;
import com.aldogg.sorter.IntSectionsInfo;
import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.floatType.FloatBitMaskSorter;

import static com.aldogg.sorter.floatType.FloatSorterUtils.*;
import static com.aldogg.sorter.intType.IntSorter.SIGN_BIT_POS;


public class RadixBitSorterFloat extends FloatBitMaskSorter {

    @Override
    public void sort(float[] array, int start, int end, int[] kList) {
        if (kList[0] == SIGN_BIT_POS) { //there are negative numbers and positive numbers
            MaskInfoInt maskInfo;
            int mask;
            int sortMask = 1 << kList[0];
            int finalLeft = partitionReverseNotStable(array, start, end, sortMask);
            int n1 = finalLeft - start;
            int n2 = end - finalLeft;
            float[] aux = new float[Math.max(n1, n2)];
            if (n1 > 1) { //sort negative numbers
                maskInfo = MaskInfoInt.getMaskBit(array, start, finalLeft);
                mask = maskInfo.getMask();
                kList = MaskInfoInt.getMaskAsArray(mask);
                radixSort(array, start, finalLeft, kList, 0, kList.length - 1, aux);
                reverse(array, start, finalLeft);
            }
            if (n2 > 1) { //sort positive numbers
                maskInfo = MaskInfoInt.getMaskBit(array, finalLeft, end);
                mask = maskInfo.getMask();
                kList = MaskInfoInt.getMaskAsArray(mask);
                radixSort(array, finalLeft, end, kList, 0, kList.length - 1, aux);
            }
        } else {
            float[] aux = new float[end - start];
            radixSort(array, start, end, kList, 0, kList.length - 1, aux);
            if (array[0] < 0) { //all negative numbers
                reverse(array, start, end);
            }
        }
    }

    public static void radixSort(float[] array, int start, int end, int[] kList, int kStart, int kEnd, float[] aux) {
        IntSectionsInfo sectionsInfo = BitSorterUtils.getOrderedSections(kList, kStart, kEnd);
        IntSection[] finalSectionList = sectionsInfo.sections;

        if (finalSectionList.length == 1 && finalSectionList[0].length == 1) {
            partitionStable(array, start, end, finalSectionList[0].sortMask, aux);
            return;
        }

        int n = end - start;
        int startAux = 0;
        int ops = 0;
        float[] arrayOrig = array;
        int startOrig = start;
        for (IntSection section : finalSectionList) {
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
            float[] tempArray = array;
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
