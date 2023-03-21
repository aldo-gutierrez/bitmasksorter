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
        int[] aux = new int[end - start];
        radixSort(array, start, end, kList, 0, kList.length - 1, aux, 0);
    }

    public static void radixSort(int[] array, int start, int end, int[] kList, int kStart, int kEnd, int[] aux, int startAux) {
        IntSectionsInfo sectionsInfo = BitSorterUtils.getOrderedSections(kList, kStart, kEnd);
        IntSection[] finalSectionList = sectionsInfo.sections;

        if (finalSectionList.length == 1 && finalSectionList[0].length == 1) {
            partitionStable(array, start, end, finalSectionList[0].sortMask, aux);
            return;
        }

        int n = end - start;
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
