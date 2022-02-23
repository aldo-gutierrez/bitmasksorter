package com.aldogg;

import static com.aldogg.BitSorterUtils.*;

public class QuickBitSorter3UInt extends QuickBitSorter2UInt implements IntSorter {
    protected BitSorterParams params = BitSorterParams.getSTParams();

    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    @Override
    public void sort(int[] list) {
        final int start = 0;
        final int end = list.length;
        if (list.length < 1) {
            return;
        }
        //if (listIsOrdered(list, start, end)) return;
        int[] maskParts = getMaskBit(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] listK = getMaskAsList(mask);
        sort(list, start, end, listK, 0, false);
    }

    public void sort(final int[] list, final int start, final int end, int[] kList, int kIndex, boolean recalculate) {
        final int listLength = end - start;
        if (listLength <= 8) {
            SortingNetworks.sortSmallList(list, start, end);
            return;
        }
        if (kIndex > kList.length - 1) {
            return;
        }

        if (recalculate) {
            int[] maskParts = getMaskBit(list, start, end);
            int mask = maskParts[0] & maskParts[1];
            kList = getMaskAsList(mask);
            kIndex = 0;

            if (kIndex > kList.length - 1) {
                return;
            }
        }

        if (kList.length - kIndex <= params.getCountingSortBits()) {
            if (listLength < params.getCountingSortBufferSize()>>4 ) {
                int[] aux = new int[listLength];
                for (int i = kList.length - 1; i >= kIndex; i--) {
                    int sortMask = BitSorterUtils.getMaskBit(kList[i]);
                    IntSorterUtils.partitionStable(list, start, end, sortMask, aux);
                }
            } else {
                CountSort.countSort(list, start, end, kList, kIndex);
            }
            return;
        }

        int sortMask = getMaskBit(kList[kIndex]);
        int finalLeft = IntSorterUtils.partition(list, start, end, sortMask);
        boolean recalculateBitMask = (finalLeft == start || finalLeft == end);

        if (finalLeft - start > 1) {
            sort(list, start, finalLeft, kList, kIndex + 1, recalculateBitMask);
        }
        if (end - finalLeft > 1) {
            sort(list, finalLeft, end, kList, kIndex + 1, recalculateBitMask);
        }
    }

}
