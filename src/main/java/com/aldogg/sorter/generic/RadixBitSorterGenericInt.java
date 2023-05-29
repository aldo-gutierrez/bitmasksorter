package com.aldogg.sorter.generic;

import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.Section;
import com.aldogg.sorter.int_.object.IntMapper;

import static com.aldogg.sorter.BitSorterParams.RADIX_SORT_MAX_BITS;
import static com.aldogg.sorter.generic.SorterUtilsGenericInt.*;

public class RadixBitSorterGenericInt<T> extends BitMaskSorterGenericInt<T> {

    @Override
    public void sort(T[] array, int start, int endP1, int[] bList, Object params) {
        Object[] p = (Object[]) params;
        T[] aux = (T[]) p[0];
        IntMapper mapper = (IntMapper) p[1];
        radixSort(array, start, endP1, bList, 0, bList.length - 1, aux, 0, mapper);
    }

    public void radixSort(T[] array, int start, int endP1, int[] bList, int bListStart, int bListEnd, T[] aux, int startAux, IntMapper mapper) {
        Section[] finalSectionList = BitSorterUtils.getProcessedSections(bList, bListStart, bListEnd, RADIX_SORT_MAX_BITS);

        if (finalSectionList.length == 1 && finalSectionList[0].bits == 1) {
            Section section = finalSectionList[0];
            int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
            partitionStable(array, start, endP1, mask, aux, mapper);
            return;
        }

        int n = endP1 - start;
        int ops = 0;
        T[] arrayOrig = array;
        int startOrig = start;
        for (Section section : finalSectionList) {
            if (!(section.shift == 0)) {
                partitionStableOneGroupBits(array, start, section, aux, startAux, n, mapper);
            } else {
                partitionStableLastBits(array, start, section, aux, startAux, n, mapper);
            }

            //System.arraycopy(aux, 0, array, start, n);
            //swap array and aux and start with startAux
            T[] tempArray = array;
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
