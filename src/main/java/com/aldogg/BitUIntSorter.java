package com.aldogg;

import static com.aldogg.BitSorterUtils.getMask;
import static com.aldogg.BitSorterUtils.getMaskAsList;
import static com.aldogg.IntSorterUtils.sortList2to5Elements;

/**
 * Experimental Bit Sorter
 */
public class BitUIntSorter extends  RadixBitUIntSorter3{
    @Override
    public void sort(int[] list) {
        final int start = 0;
        final int end = list.length;
        int[] maskParts = getMask(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);
        if (kList.length <= params.getCountingSortBits()) {
            CountSort.countSort(list, start, end, kList, 0);
        } else {
            sort(list, start, end, kList, 0, 0);
        }
    }


    public void sort(final int[] list, final int start, final int end, int[] kList, int kIndex, int level) {
        final int listLength = end - start;
        //if (listLength <= 5) {
        //    sortList2to5Elements(list, start, end);
        //    return;
        //}
        if (kIndex > kList.length - 1) {
            return;
        }

        if (kList.length - kIndex <= params.getCountingSortBits()) {
            CountSort.countSort(list, start, end, kList, kIndex);
            return;
        }

        if (level == 0) {
            //int newKIndex = kIndex + kList.length - params.getCountingSortBits() -1;
            //CountSort.countSort(list, start, end, kList, newKIndex);
            //radixSort(list, start, end, kList, kList.length -  params.getCountingSortBits() - 1, kIndex );
            radixSort(list, start, end, kList, kList.length - 1, kIndex );
        } else {
            int sortMask = getMask(kList[kIndex]);
            int finalLeft = partition(list, start, end, sortMask);

            if (finalLeft - start > 1) {
                sort(list, start, finalLeft, kList, kIndex + 1, level + 1);
            }
            if (end - finalLeft > 1) {
                sort(list, finalLeft, end, kList, kIndex + 1, level + 1);
            }
        }
    }

}
