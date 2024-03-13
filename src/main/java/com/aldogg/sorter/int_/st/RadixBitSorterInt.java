package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.FieldOptions;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;
import com.aldogg.sorter.shared.Section;
import com.aldogg.sorter.int_.BitMaskSorterInt;

import static com.aldogg.sorter.BitSorterParams.RADIX_SORT_MAX_BITS;
import static com.aldogg.sorter.int_.SorterUtilsInt.*;

public class RadixBitSorterInt extends BitMaskSorterInt {

    @Override
    public void sort(int[] array, int start, int endP1, FieldOptions options, int[] bList, Object params) {
        int[] aux = (int[]) params;
        radixSort(array, start, bList, 0, aux, 0, endP1 - start);
    }

    public static void radixSort(int[] array, int start, int[] bList, int bListStart, int[] aux, int startAux, int n) {
        int bListEnd = bList.length - 1;
        Section[] finalSectionList = BitSorterUtils.getProcessedSections(bList, bListStart, bListEnd, RADIX_SORT_MAX_BITS);

        if (finalSectionList.length == 1 && finalSectionList[0].bits == 1) {
            Section section = finalSectionList[0];
            int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
            partitionStable(array, start, start + n, mask, aux);
            return;
        }

        int ops = 0;
        int[] arrayOrig = array;
        int startOrig = start;
        for (Section section : finalSectionList) {
            if (!(section.shift == 0)) {
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
