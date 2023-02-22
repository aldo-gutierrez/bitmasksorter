package com.aldogg.sorter.intType;

import com.aldogg.parallel.SorterRunner;
import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.BitSorterMTParams;
import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.SortingNetworks;

import java.util.concurrent.atomic.AtomicInteger;

import static com.aldogg.parallel.ArrayParallelRunner.splitWork;
import static com.aldogg.sorter.MaskInfoInt.*;
import static com.aldogg.sorter.intType.IntSorterUtils.listIsOrderedSigned;
import static com.aldogg.sorter.intType.IntSorterUtils.listIsOrderedUnSigned;

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
        if (n <= params.getDataSizeForThreads() || params.getMaxThreads() <= 1) {
            stSorter.sort(array, start, end);
            return;
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, end) : listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(array, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        numThreads.set(NUM_THREADS_INITIAL);

        MaskInfoInt maskInfo;
        if (n >= SIZE_FOR_PARALLEL_BIT_MASK) {
            maskInfo = MaskInfoInt.getMaskBitDetectSignBitParallel(array, start, end, 2, null);
        } else {
            maskInfo = MaskInfoInt.getMaskBitDetectSignBit(array, start, end);
        }

        if (maskInfo == null) { //there are negative numbers and positive numbers
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStableSignBit(array, start, end)
                    : IntSorterUtils.partitionReverseNotStableSignBit(array, start, end);
            int n1 = finalLeft - start;
            int n2 = end - finalLeft;
            int[] threadNumbers = splitWork(n1, n2, params.getMaxThreads());
            SorterRunner.runTwoRunnable(
                    n1 > 1 ? () -> { //sort negative numbers
                        int maxThreads = threadNumbers[0];
                        MaskInfoInt maskInfo1;
                        if (n1 >= SIZE_FOR_PARALLEL_BIT_MASK & maxThreads >= 2) {
                            maskInfo1 = MaskInfoInt.getMaskBitParallel(array, start, finalLeft, 2, null);
                        } else {
                            maskInfo1 = MaskInfoInt.getMaskBit(array, start, finalLeft);
                        }
                        int mask1 = maskInfo1.getMask();
                        int[] kList1 = MaskInfoInt.getMaskAsArray(mask1);
                        sort(array, start, finalLeft, kList1, maxThreads);
                    } : null, n1,
                    n2 > 1 ? () -> { //sort positive numbers
                        int maxThreads = threadNumbers[1];
                        MaskInfoInt maskInfo2;
                        if (n2 >= SIZE_FOR_PARALLEL_BIT_MASK & maxThreads >= 2) {
                            maskInfo2 = MaskInfoInt.getMaskBitParallel(array, finalLeft, end, 2, null);
                        } else {
                            maskInfo2 = MaskInfoInt.getMaskBit(array, finalLeft, end);
                        }
                        int mask2 = maskInfo2.getMask();
                        int[] kList2 = MaskInfoInt.getMaskAsArray(mask2);
                        sort(array, finalLeft, end, kList2, maxThreads);
                    } : null, n2, params.getDataSizeForThreads(), params.getMaxThreads(), numThreads);

        } else {
            int mask = maskInfo.getMask();
            if (mask != 0) {
                int[] kList = MaskInfoInt.getMaskAsArray(mask);
                sort(array, start, end, kList, params.getMaxThreads());
            }
        }
    }

}
