package com.aldogg.sorter.int_.mt;

import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.SortingNetworks;
import com.aldogg.sorter.int_.IntBitMaskSorter;
import com.aldogg.sorter.int_.IntBitMaskSorterMT;
import com.aldogg.sorter.int_.IntSorterUtils;
import com.aldogg.sorter.int_.st.QuickBitSorterInt;

import static com.aldogg.sorter.MaskInfoInt.getMaskAsArray;
import static com.aldogg.sorter.MaskInfoInt.getMaskBit;
import static com.aldogg.sorter.int_.IntSorterUtils.sortShortK;

public class QuickBitSorterMTInt extends IntBitMaskSorterMT {

    @Override
    public void sort(int[] array, int start, int end, int[] kList, Object multiThreadParams) {
        sortMT(array, start, end, kList, 0, false);
    }

    public void sortMT(final int[] array, final int start, final int end, int[] kList, int kIndex, boolean recalculate) {
        final int n = end - start;
        if (n < params.getDataSizeForThreads()) {
            ((QuickBitSorterInt) getSTIntSorter()).sort(array, start, end, kList, kIndex, recalculate);
            return;
        }

        if (recalculate && kIndex < 3) {
            MaskInfoInt maskParts = getMaskBit(array, start, end);
            int mask = maskParts.getMask();
            kList = getMaskAsArray(mask);
            kIndex = 0;

        }

        int kDiff = kList.length - kIndex;
        if (kDiff <= params.getShortKBits()) {
            if (kDiff < 1) {
                return;
            }
            sortShortK(array, start, end, kList, kIndex);
            return;
        }

        int sortMask = 1 << kList[kIndex];
        int finalLeft = IntSorterUtils.partitionNotStable(array, start, end, sortMask);
        final boolean recalculateBitMask = (finalLeft == start || finalLeft == end);

        int[] finalKList = kList;
        int finalKIndex = kIndex;
        Thread t1 = null;
        int n1 = finalLeft - start;
        if (n1 > 1) {
            if (n1 > params.getDataSizeForThreads() && runningThreads.get() < params.getMaxThreads() + 1) {
                Runnable r1 = () -> sortMT(array, start, finalLeft, finalKList, finalKIndex + 1, recalculateBitMask);
                t1 = new Thread(r1);
                t1.start();
                runningThreads.addAndGet(1);
            } else {
                sortMT(array, start, finalLeft, finalKList, finalKIndex + 1, recalculateBitMask);
            }
        }
        int n2 = end - finalLeft;
        if (n2 > 1) {
            sortMT(array, finalLeft, end, kList, kIndex + 1, recalculateBitMask);
        }
        if (t1 != null) {
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                runningThreads.addAndGet(-1);
            }
        }
    }

    @Override
    public IntBitMaskSorter getSTIntSorter() {
        QuickBitSorterInt sorter = new QuickBitSorterInt();
        sorter.setUnsigned(isUnsigned());
        sorter.setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        return sorter;
    }
}
