package com.aldogg.sorter;

import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

import static com.aldogg.sorter.BitSorterParams.*;
import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.intType.IntSorterUtils.sortShortKList;

public class QuickBitSorterInt implements IntSorter {
    protected BitSorterParams params = BitSorterParams.getSTParams();

    public void setParams(BitSorterParams params) {
        this.params = params;
    }

    boolean unsigned = false;

    @Override
    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
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
        int[] kList = getMaskAsList(mask);
        if (kList.length == 0) {
            return;
        }
        if (kList[0] == 31) { //there are negative numbers
            int sortMask = BitSorterUtils.getMaskBit(kList[0]);
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(list, start, end, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(list, start, end, sortMask);
            if (finalLeft - start > 1) {
                sort(list, start, finalLeft, kList, 1, true);
            }
            if (end - finalLeft > 1) {
                sort(list, finalLeft, end, kList, 1, true);
            }
        } else {
            sort(list, start, end, kList, 0, false);
        }
    }

    public void sort(final int[] list, final int start, final int end, int[] kList, int kIndex, boolean recalculate) {
        final int listLength = end - start;
        if (listLength <= SMALL_LIST_SIZE) {
            if (unsigned) {
                SortingNetworks.sortVerySmallListUnSigned(list, start, end);
            } else {
                SortingNetworks.sortVerySmallListSigned(list, start, end);
            }
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
            sortShortKList(list, start, end, kList, kIndex);
            return;
        }

        int sortMask = getMaskBit(kList[kIndex]);
        int finalLeft = IntSorterUtils.partitionNotStable(list, start, end, sortMask);
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
            if (unsigned) {
                SortingNetworks.sortVerySmallListUnSigned(list, start, end);
            } else {
                SortingNetworks.sortVerySmallListSigned(list, start, end);
            }
            return;
        }

        int kDiff = kList.length - kIndex;
        if (kDiff < 1) {
            return;
        }

        if (kDiff <= params.getCountingSortBits()) {
            sortShortKList(list, start, end, kList, kIndex);
            return;
        }

        int sortMask = getMaskBit(kList[kIndex]);
        int finalLeft = IntSorterUtils.partitionNotStable(list, start, end, sortMask);

        if (finalLeft - start > 1) {
            sort(list, start, finalLeft, kList, kIndex + 1);
        }
        if (end - finalLeft > 1) {
            sort(list, finalLeft, end, kList, kIndex + 1);
        }
    }

}
