package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.IntSection;
import com.aldogg.sorter.IntSectionsInfo;
import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.int_.IntBitMaskSorter;
import com.aldogg.sorter.int_.IntSorterUtils;

import static com.aldogg.sorter.int_.IntSorterUtils.*;

public class RadixBitSorterInt extends IntBitMaskSorter {

    @Override
    public void sort(int[] array, int start, int end, int[] kList, Object multiThreadParams) {
        if (kList[0] == SIGN_BIT_POS) { //there are negative numbers and positive numbers
            MaskInfoInt maskInfo;
            int mask;
            int sortMask = 1 << kList[0];
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
            int n1 = finalLeft - start;
            int n2 = end - finalLeft;
            int[] aux = new int[Math.max(n1, n2)];
            if (n1 > 1) { //sort negative numbers
                maskInfo = MaskInfoInt.getMaskBit(array, start, finalLeft);
                mask = maskInfo.getMask();
                kList = MaskInfoInt.getMaskAsArray(mask);
                radixSort(array, start, finalLeft, kList, 0, kList.length - 1, aux);
            }
            if (n2 > 1) { //sort positive numbers
                maskInfo = MaskInfoInt.getMaskBit(array, finalLeft, end);
                mask = maskInfo.getMask();
                kList = MaskInfoInt.getMaskAsArray(mask);
                radixSort(array, finalLeft, end, kList, 0, kList.length - 1, aux);
            }
        } else {
            int[] aux = new int[end - start];
            radixSort(array, start, end, kList, 0, kList.length - 1, aux);
        }
    }

    public static void radixSort(int[] array, int start, int end, int[] kList, int kStart, int kEnd, int[] aux) {
        IntSectionsInfo sectionsInfo = BitSorterUtils.getOrderedSections(kList, kStart, kEnd);
        IntSection[] finalSectionList = sectionsInfo.sections;

        if (finalSectionList.length == 1 && finalSectionList[0].length == 1) {
            partitionStable(array, start, end, finalSectionList[0].sortMask, aux);
            return;
        }

        int n = end - start;
        int startAux = 0;
        int ops = 0;
        int[] arrayOrig = array;
        int startOrig = start;
        for (IntSection section: finalSectionList) {
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
