package com.aldogg;

import com.aldogg.intType.IntSorter;
import com.aldogg.intType.IntSorterUtils;

import static com.aldogg.BitSorterParams.*;
import static com.aldogg.BitSorterUtils.*;
import static com.aldogg.intType.IntSorterUtils.sortShortList;

public class QuickBitSorterUInt extends QuickBitSorterUIntBase implements IntSorter {
    protected BitSorterParams params = BitSorterParams.getSTParams();

    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    @Override
    public void sort(int[] list) {
        if (list.length < 2) {
            return;
        }
        final int start = 0;
        final int end = list.length;
        //if (listIsOrdered(list, start, end)) return;
        int[] maskParts = getMaskBit(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] listK = getMaskAsList(mask);
        sort(list, start, end, listK, 0, false);
    }

    public void sort(final int[] list, final int start, final int end, int[] kList, int kIndex, boolean recalculate) {
        final int listLength = end - start;
        if (listLength <= SMALL_LIST_SIZE) {
            SortingNetworks.sortSmallList(list, start, end);
            return;
        }

        if (recalculate && kIndex < 3) {
            int[] maskParts = getMaskBit(list, start, end);
            int mask = maskParts[0] & maskParts[1];
            kList = getMaskAsList(mask);
            kIndex = 0;
        }

        int kDiff = kList.length - kIndex;
        if (kDiff < 1) {
            return;
        }

        if (kDiff <= params.getCountingSortBits()) {
            sortShortList(list, start, end, kList, kIndex);
            return;
        }

        int sortMask = getMaskBit(kList[kIndex]);
        int finalLeft = IntSorterUtils.partition(list, start, end, sortMask);
        if (recalculate) {
            if (finalLeft - start > 1) {
                sort(list, start, finalLeft, kList, kIndex + 1);
            }
            if (end - finalLeft > 1) {
                sort(list, finalLeft, end, kList, kIndex + 1);
            }
        } else {
            boolean recalculateBitMask = (finalLeft == start || finalLeft == end);

            if (finalLeft - start > 1) {
                sort(list, start, finalLeft, kList, kIndex + 1, recalculateBitMask);
            }
            if (end - finalLeft > 1) {
                sort(list, finalLeft, end, kList, kIndex + 1, recalculateBitMask);
            }
        }
    }

    public void sort(final int[] list, final int start, final int end, int[] kList, int kIndex) {
        final int listLength = end - start;
        if (listLength <= SMALL_LIST_SIZE) {
            SortingNetworks.sortSmallList(list, start, end);
            return;
        }

        int kDiff = kList.length - kIndex;
        if (kDiff < 1) {
            return;
        }

        if (kDiff <= params.getCountingSortBits()) {
            sortShortList(list, start, end, kList, kIndex);
            return;
        }

        int sortMask = getMaskBit(kList[kIndex]);
        int finalLeft = IntSorterUtils.partition(list, start, end, sortMask);

        if (finalLeft - start > 1) {
            sort(list, start, finalLeft, kList, kIndex + 1);
        }
        if (end - finalLeft > 1) {
            sort(list, finalLeft, end, kList, kIndex + 1);
        }
    }

}
