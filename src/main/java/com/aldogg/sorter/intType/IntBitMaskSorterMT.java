package com.aldogg.sorter.intType;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.BitSorterMTParams;
import com.aldogg.sorter.SortingNetworks;

import java.util.concurrent.atomic.AtomicInteger;

import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.BitSorterUtils.getMaskAsArray;

public abstract class IntBitMaskSorterMT extends IntBitMaskSorter {
    protected final AtomicInteger numThreads = new AtomicInteger(1);

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

        int[] maskParts = getMaskBit(array, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsArray(mask);
        if (kList.length == 0) {
            return;
        }
        setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        stSorter.setSNFunctions(snFunctions);
        sort(array, start, end, kList);
    }

}
