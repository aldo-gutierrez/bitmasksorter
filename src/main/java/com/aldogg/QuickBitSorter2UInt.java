package com.aldogg;

import static com.aldogg.BitSorterUtils.*;

public class QuickBitSorter2UInt extends QuickBitSorterUInt implements IntSorter {

    protected BitSorterParams params = BitSorterParams.getSTParams();

    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    public QuickBitSorter2UInt() {

    }

    public QuickBitSorter2UInt(int size) {
        super();
        this.params.setCountingSortBits(size);
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
            sort(list, start, end, listK, 0);
        }
    }

    public void sort(final int[] list, final int start, final int end, final int[] kList, final int kIndex) {
        final int listLength = end - start;
        if (listLength <= 8) {
            SortingNetworks.sortSmallList(list, start, end);
            return;
        }
        if (kIndex > kList.length - 1) {
            return;
        }
        if (kList.length - kIndex <= params.getCountingSortBits()) {
            CountSort.countSort(list, start, end, kList, kIndex);
            return;
        }

        int sortMask = getMask(kList[kIndex]);
        int finalLeft = IntSorterUtils.partition(list, start, end, sortMask);
        if (finalLeft - start > 1) {
            sort(list, start, finalLeft, kList, kIndex + 1);
        }
        if (end - finalLeft > 1) {
            sort(list, finalLeft, end, kList, kIndex + 1);
        }
    }

}
