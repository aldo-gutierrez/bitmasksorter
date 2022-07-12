package com.aldogg.sorter.intType;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.BitSorterMTParams;
import com.aldogg.sorter.MaskInfo;
import com.aldogg.sorter.SortingNetworks;

import java.util.concurrent.atomic.AtomicInteger;

import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.MaskInfo.getMaskAsArray;

public abstract class IntBitMaskSorterMT extends IntBitMaskSorter {
    public static final int NUM_THREADS_INITIAL = 1;
    protected final AtomicInteger numThreads = new AtomicInteger(NUM_THREADS_INITIAL);

    protected final BitSorterMTParams params = BitSorterMTParams.getMTParams();

    protected IntBitMaskSorter stSorter;

    public abstract IntBitMaskSorter getSTIntSorter();

    @Override
    public void sort(int[] array, int start, int end) {
        int n = end - start;
        if (n < 2) {
            return;
        }
        stSorter = getSTIntSorter();
        stSorter.setUnsigned(isUnsigned());
        if (n <= params.getDataSizeForThreads() || params.getMaxThreads() <= 1) {
            stSorter.sort(array, start, end);
            return;
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, end) : listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(array, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        MaskInfo maskInfo;
        if (n >= 8388608) {
            numThreads.set(0);
            maskInfo = MaskInfo.getMaskBitParallel(array, start, end, params.getMaxThreads(), numThreads);
        } else {
            maskInfo = MaskInfo.getMaskBit(array, start, end);
        }
        int mask = maskInfo.getMask();
        int[] kList = getMaskAsArray(mask);
        if (kList.length == 0) {
            return;
        }
        setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        stSorter.setSNFunctions(snFunctions);
        numThreads.set(NUM_THREADS_INITIAL);
        sort(array, start, end, kList);
    }

}
