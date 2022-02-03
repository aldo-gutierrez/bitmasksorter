package com.aldogg;

import static com.aldogg.BitSorterUtils.getMask;
import static com.aldogg.BitSorterUtils.getMaskAsList;
import static com.aldogg.IntSorterUtils.sortList2to5Elements;

public class BitSorterUIntOptimized2 extends BitSorterUIntOptimized implements IntSorter {
    protected BitSorterParams params = BitSorterParams.getSTParams();

    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    @Override
    public void sort(int[] list) {
        final int start = 0;
        final int end = list.length;
        int[] maskParts = getMask(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] listK = getMaskAsList(mask);
        if (listK.length <= params.getCountingSortBits()) {
            CountSort.countSort(list, start, end, listK, 0);
        } else {
            sort(list, start, end, listK, 0, false);
        }
    }

    public void sort(final int[] list, final int start, final int end, int[] kList, int kIndex, boolean recalculate) {
        final int listLength = end - start;
        if (listLength <= 5) {
            sortList2to5Elements(list, start, end);
            return;
        }
        if (kIndex > kList.length - 1) {
            return;
        }

        if (recalculate) {
            int[] maskParts = getMask(list, start, end);
            int mask = maskParts[0] & maskParts[1];
            kList = getMaskAsList(mask);
            kIndex = 0;

            if (kIndex > kList.length - 1) {
                return;
            }
        }

        if (kList.length - kIndex <= params.getCountingSortBits()) {
            CountSort.countSort(list, start, end, kList, kIndex);
            return;
        }

        int sortMask = getMask(kList[kIndex]);
        int finalLeft = partition(list, start, end, sortMask);
        boolean recalculateBitMask = (finalLeft == start || finalLeft == end);

        if (finalLeft - start > 1) {
            sort(list, start, finalLeft, kList, kIndex + 1, recalculateBitMask);
        }
        if (end - finalLeft > 1) {
            sort(list, finalLeft, end, kList, kIndex + 1, recalculateBitMask);
        }
    }

}
