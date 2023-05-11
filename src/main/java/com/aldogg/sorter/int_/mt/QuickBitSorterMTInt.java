package com.aldogg.sorter.int_.mt;

import com.aldogg.parallel.ParallelRunner;
import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.SortingNetworks;
import com.aldogg.sorter.int_.IntBitMaskSorter;
import com.aldogg.sorter.int_.IntBitMaskSorterMT;
import com.aldogg.sorter.int_.IntSorterUtils;
import com.aldogg.sorter.int_.st.QuickBitSorterInt;

import static com.aldogg.parallel.ArrayParallelRunner.splitWork;
import static com.aldogg.sorter.MaskInfoInt.getMaskAsArray;
import static com.aldogg.sorter.int_.IntSorterUtils.sortShortK;

public class QuickBitSorterMTInt extends IntBitMaskSorterMT {

    @Override
    public void sort(int[] array, int start, int endP1, int[] bList, Object multiThreadParams) {
        //Number of Threads was multiplied by two to get back performance TODO investigate
        sortMT(array, start, endP1, bList, 0, false, ((Integer) multiThreadParams) * 2);
    }

    public void sortMT(final int[] array, final int start, final int endP1, int[] bList, int kIndex, boolean recalculate, int maxThreads) {
        final int n = endP1 - start;
        if (n < params.getDataSizeForThreads() || maxThreads == 1) {
            ((QuickBitSorterInt) getSTIntSorter()).sort(array, start, endP1, bList, kIndex, recalculate);
            return;
        }

        if (recalculate && kIndex < 3) {
            MaskInfoInt maskParts = MaskInfoInt.calculateMask(array, start, endP1);
            int mask = maskParts.getMask();
            bList = getMaskAsArray(mask);
            kIndex = 0;

        }

        int kDiff = bList.length - kIndex;
        if (kDiff <= params.getShortKBits()) {
            if (kDiff < 1) {
                return;
            }
            sortShortK(array, start, endP1, bList, kIndex);
            return;
        }

        int sortMask = 1 << bList[kIndex];
        int finalLeft = IntSorterUtils.partitionNotStable(array, start, endP1, sortMask);
        final boolean recalculateBitMask = (finalLeft == start || finalLeft == endP1);

        int[] finalbList = bList;
        int finalKIndex = kIndex;
        int n1 = finalLeft - start;
        int n2 = endP1 - finalLeft;

        int[] threadNumbers = splitWork(n1, n2, maxThreads);
        ParallelRunner.runTwoRunnable(
                n1 > 1 ? () -> {
                    sortMT(array, start, finalLeft, finalbList, finalKIndex + 1, recalculateBitMask, threadNumbers[0]);
                } : null, n1,
                n2 > 1 ? () -> {
                    sortMT(array, finalLeft, endP1, finalbList, finalKIndex + 1, recalculateBitMask, threadNumbers[1]);
                } : null, n2, params.getDataSizeForThreads(), maxThreads);
    }

    @Override
    public IntBitMaskSorter getSTIntSorter() {
        QuickBitSorterInt sorter = new QuickBitSorterInt();
        sorter.setUnsigned(isUnsigned());
        sorter.setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        return sorter;
    }
}
