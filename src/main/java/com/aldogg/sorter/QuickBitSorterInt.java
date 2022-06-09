package com.aldogg.sorter;

import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

import java.util.Map;
import java.util.function.BiConsumer;

import static com.aldogg.sorter.BitSorterParams.*;
import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.intType.IntSorterUtils.sortShortK;

public class QuickBitSorterInt implements IntSorter {
    protected BitSorterParams params = BitSorterParams.getSTParams();
    boolean unsigned = false;
    private Map<Integer, BiConsumer<int[], Integer>> snFunction;

    public void setParams(BitSorterParams params) {
        this.params = params;
    }


    @Override
    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

    @Override
    public void sort(int[] array) {
        if (array.length < 2) {
            return;
        }
        final int start = 0;
        final int end = array.length;
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, end) : listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(array, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        int[] maskParts = getMaskBit(array, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsArray(mask);
        if (kList.length == 0) {
            return;
        }
        snFunction = unsigned ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions;

        if (kList[0] == 31) { //there are negative numbers
            int sortMask = BitSorterUtils.getMaskBit(kList[0]);
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
            if (finalLeft - start > 1) {
                sort(array, start, finalLeft, kList, 1, true);
            }
            if (end - finalLeft > 1) {
                sort(array, finalLeft, end, kList, 1, true);
            }
        } else {
            sort(array, start, end, kList, 0, false);
        }
    }


    public void sort(final int[] array, final int start, final int end, int[] kList, int kIndex, boolean recalculate) {
        final int n = end - start;
        if (n <= VERY_SMALL_N_SIZE) {
            snFunction.get(n).accept(array, start);
            return;
        }

        if (recalculate && kIndex < 3) {
            int[] maskParts = getMaskBit(array, start, end);
            int mask = maskParts[0] & maskParts[1];
            kList = getMaskAsArray(mask);
            kIndex = 0;
        }

        int kDiff = kList.length - kIndex;
        if (kDiff < 1) {
            return;
        }

        if (kDiff <= params.getShortKBits()) {
            sortShortK(array, start, end, kList, kIndex);
            return;
        }

        int sortMask = getMaskBit(kList[kIndex]);
        int finalLeft = IntSorterUtils.partitionNotStable(array, start, end, sortMask);
        if (recalculate) {
            if (finalLeft - start > 1) {
                sort(array, start, finalLeft, kList, kIndex + 1);
            }
            if (end - finalLeft > 1) {
                sort(array, finalLeft, end, kList, kIndex + 1);
            }
        } else {
            boolean recalculateBitMask = (finalLeft == start || finalLeft == end);

            if (finalLeft - start > 1) {
                sort(array, start, finalLeft, kList, kIndex + 1, recalculateBitMask);
            }
            if (end - finalLeft > 1) {
                sort(array, finalLeft, end, kList, kIndex + 1, recalculateBitMask);
            }
        }
    }

    public void sort(final int[] array, final int start, final int end, int[] kList, int kIndex) {
        final int n = end - start;
        if (n <= VERY_SMALL_N_SIZE) {
            snFunction.get(n).accept(array, start);
            return;
        }

        int kDiff = kList.length - kIndex;
        if (kDiff < 1) {
            return;
        }

        if (kDiff <= params.getShortKBits()) {
            sortShortK(array, start, end, kList, kIndex);
            return;
        }

        int sortMask = getMaskBit(kList[kIndex]);
        int finalLeft = IntSorterUtils.partitionNotStable(array, start, end, sortMask);

        if (finalLeft - start > 1) {
            sort(array, start, finalLeft, kList, kIndex + 1);
        }
        if (end - finalLeft > 1) {
            sort(array, finalLeft, end, kList, kIndex + 1);
        }
    }

}
