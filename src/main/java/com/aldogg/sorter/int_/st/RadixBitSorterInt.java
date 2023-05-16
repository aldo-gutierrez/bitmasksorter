package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.Section;
import com.aldogg.sorter.int_.IntBitMaskSorter;

import static com.aldogg.sorter.BitSorterParams.RADIX_SORT_MAX_BITS;
import static com.aldogg.sorter.int_.IntSorterUtils.*;

public class RadixBitSorterInt extends IntBitMaskSorter {
    int[] aux = null;

    @Override
    public void sort(int[] array, int start, int endP1, int[] bList, Object multiThreadParams) {
        if (aux == null) {
            aux = new int[auxSize];
        }
        radixSort(array, start, endP1, bList, 0, bList.length - 1, aux, 0);
        aux = null;
    }

    public static void radixSort(int[] array, int start, int endP1, int[] bList, int bListStart, int bListEnd, int[] aux, int startAux) {
        Section[] finalSectionList = BitSorterUtils.getProcessedSections(bList, bListStart, bListEnd, RADIX_SORT_MAX_BITS);

        if (finalSectionList.length == 1 && finalSectionList[0].bits == 1) {
            Section section = finalSectionList[0];
            int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
            partitionStable(array, start, endP1, mask, aux);
            return;
        }

        int n = endP1 - start;
        int ops = 0;
        int[] arrayOrig = array;
        int startOrig = start;
        for (Section section : finalSectionList) {
            if (!section.isSectionAtEnd()) {
                partitionStableOneGroupBits(array, start, section, aux, startAux, n);
            } else {
                partitionStableLastBits(array, start, section, aux, startAux, n);
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
