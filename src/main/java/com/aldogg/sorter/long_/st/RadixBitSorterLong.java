package com.aldogg.sorter.long_.st;

import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.FieldSortOptions;
import com.aldogg.sorter.shared.long_mask.MaskInfoLong;
import com.aldogg.sorter.shared.Section;
import com.aldogg.sorter.long_.BitMaskSorterLong;

import static com.aldogg.sorter.BitSorterParams.RADIX_SORT_MAX_BITS;
import static com.aldogg.sorter.long_.SorterUtilsLong.*;
import static com.aldogg.sorter.shared.FieldType.UNSIGNED_INTEGER;

public class RadixBitSorterLong extends BitMaskSorterLong {

    @Override
    public void sort(long[] array, int start, int endP1, FieldSortOptions options, int[] bList) {
        if (bList[0] == MaskInfoLong.UPPER_BIT) { //there are negative numbers and positive numbers
            MaskInfoLong maskInfo;
            long mask;
            long sortMask = 1L << bList[0];
            int finalLeft = options.getFieldType().equals(UNSIGNED_INTEGER)
                    ? partitionNotStable(array, start, endP1, sortMask)
                    : partitionReverseNotStable(array, start, endP1, sortMask);
            int n1 = finalLeft - start;
            int n2 = endP1 - finalLeft;
            long[] aux = new long[Math.max(n1, n2)];
            if (n1 > 1) { //sort negative numbers
                maskInfo = MaskInfoLong.calculateMask(array, start, finalLeft);
                mask = maskInfo.getMask();
                bList = MaskInfoLong.getMaskAsArray(mask);
                radixSort(array, start, finalLeft, bList, 0, bList.length - 1, aux);
            }
            if (n2 > 1) { //sort positive numbers
                maskInfo = MaskInfoLong.calculateMask(array, finalLeft, endP1);
                mask = maskInfo.getMask();
                bList = MaskInfoLong.getMaskAsArray(mask);
                radixSort(array, finalLeft, endP1, bList, 0, bList.length - 1, aux);
            }
        } else {
            long[] aux = new long[endP1 - start];
            radixSort(array, start, endP1, bList, 0, bList.length - 1, aux);
        }
    }

    public static void radixSort(long[] array, int start, int endP1, int[] bList, int bListStart, int bListEnd, long[] aux) {
        Section[] finalSectionList = BitSorterUtils.getProcessedSections(bList, bListStart, bListEnd, RADIX_SORT_MAX_BITS);

        if (finalSectionList.length == 1 && finalSectionList[0].bits == 1) {
            Section section = finalSectionList[0];
            long mask = MaskInfoLong.getMaskRangeBits(section.start, section.shift);
            partitionStable(array, start, endP1, mask, aux);
            return;
        }

        int n = endP1 - start;
        int startAux = 0;
        int ops = 0;
        long[] arrayOrig = array;
        int startOrig = start;
        for (Section section : finalSectionList) {
            if (!(section.shift == 0)) {
                partitionStableOneGroupBits(array, start, section, aux, startAux, n);
            } else {
                partitionStableLastBits(array, start, section, aux, startAux, n);
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
