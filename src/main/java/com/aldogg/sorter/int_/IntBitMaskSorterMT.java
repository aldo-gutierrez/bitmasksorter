package com.aldogg.sorter.int_;

import com.aldogg.parallel.ArrayParallelRunner;
import com.aldogg.parallel.ParallelRunner;
import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.BitSorterMTParams;
import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.SortingNetworks;

import java.util.concurrent.atomic.AtomicInteger;

import static com.aldogg.parallel.ArrayParallelRunner.splitWork;
import static com.aldogg.sorter.MaskInfoInt.*;
import static com.aldogg.sorter.int_.IntSorterUtils.listIsOrderedSigned;
import static com.aldogg.sorter.int_.IntSorterUtils.listIsOrderedUnSigned;

public abstract class IntBitMaskSorterMT extends IntBitMaskSorter {
    public static final int NUM_THREADS_INITIAL = 1;
    protected final AtomicInteger runningThreads = new AtomicInteger(NUM_THREADS_INITIAL);

    protected final BitSorterMTParams params = BitSorterMTParams.getMTParams();

    public abstract IntBitMaskSorter getSTIntSorter();

    @Override
    public void sort(int[] array, int start, int end) {
        int n = end - start;
        if (n < 2) {
            return;
        }
        int maxThreads = params.getMaxThreads();
        if (n <= params.getDataSizeForThreads() || maxThreads <= 1) {
            getSTIntSorter().sort(array, start, end);
            return;
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, end) : listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(array, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        runningThreads.set(NUM_THREADS_INITIAL);

        MaskInfoInt maskInfo;
        if (n >= SIZE_FOR_PARALLEL_BIT_MASK) {
            maskInfo = MaskInfoInt.getMaskBitDetectSignBitParallel(array, start, end, new ArrayParallelRunner.APRParameters(2));
        } else {
            maskInfo = MaskInfoInt.getMaskBitDetectSignBit(array, start, end, null);
        }

        if (maskInfo == null) { //there are negative numbers and positive numbers
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStableSignBit(array, start, end)
                    : IntSorterUtils.partitionReverseNotStableSignBit(array, start, end);
            int n1 = finalLeft - start;
            int n2 = end - finalLeft;
            int[] threadNumbers = splitWork(n1, n2, maxThreads);
            ParallelRunner.runTwoRunnable(
                    n1 > 1 ? () -> { //sort negative numbers
                        int maxThreads1 = threadNumbers[0];
                        if (maxThreads1 <= 1) {
                            getSTIntSorter().sort(array, start, end);
                            return;
                        }

                        MaskInfoInt maskInfo1;
                        if (n1 >= SIZE_FOR_PARALLEL_BIT_MASK) {
                            maskInfo1 = MaskInfoInt.getMaskBitParallel(array, start, finalLeft, new ArrayParallelRunner.APRParameters(2));
                        } else {
                            maskInfo1 = MaskInfoInt.getMaskBit(array, start, finalLeft);
                        }
                        int mask1 = maskInfo1.getMask();
                        int[] kList1 = MaskInfoInt.getMaskAsArray(mask1);
                        sort(array, start, finalLeft, kList1, maxThreads1);
                    } : null, n1,
                    n2 > 1 ? () -> { //sort positive numbers
                        int maxThreads2 = threadNumbers[1];
                        if (maxThreads2 <= 1) {
                            getSTIntSorter().sort(array, start, end);
                            return;
                        }
                        MaskInfoInt maskInfo2;
                        if (n2 >= SIZE_FOR_PARALLEL_BIT_MASK) {
                            maskInfo2 = MaskInfoInt.getMaskBitParallel(array, finalLeft, end, new ArrayParallelRunner.APRParameters(2));
                        } else {
                            maskInfo2 = MaskInfoInt.getMaskBit(array, finalLeft, end);
                        }
                        int mask2 = maskInfo2.getMask();
                        int[] kList2 = MaskInfoInt.getMaskAsArray(mask2);
                        sort(array, finalLeft, end, kList2, maxThreads2);
                    } : null, n2, params.getDataSizeForThreads(), maxThreads, runningThreads);

        } else {
            int mask = maskInfo.getMask();
            if (mask != 0) {
                int[] kList = MaskInfoInt.getMaskAsArray(mask);
                sort(array, start, end, kList, maxThreads);
            }
        }
    }

}
