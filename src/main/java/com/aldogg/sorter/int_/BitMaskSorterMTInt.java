package com.aldogg.sorter.int_;

import com.aldogg.parallel.ArrayParallelRunner;
import com.aldogg.parallel.ParallelRunner;
import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.BitSorterMTParams;
import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.SortingNetworks;

import static com.aldogg.parallel.ArrayParallelRunner.splitWork;
import static com.aldogg.sorter.MaskInfoInt.*;
import static com.aldogg.sorter.int_.SorterUtilsInt.listIsOrderedSigned;
import static com.aldogg.sorter.int_.SorterUtilsInt.listIsOrderedUnSigned;

public abstract class BitMaskSorterMTInt extends BitMaskSorterInt {
    protected final BitSorterMTParams params = BitSorterMTParams.getMTParams();

    public abstract BitMaskSorterInt getSTIntSorter();

    @Override
    public void sort(int[] array, int start, int endP1) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        int maxThreads = params.getMaxThreads();
        if (n <= params.getDataSizeForThreads() || maxThreads <= 1) {
            getSTIntSorter().sort(array, start, endP1);
            return;
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, endP1) : listIsOrderedSigned(array, start, endP1);
        if (ordered == AnalysisResult.DESCENDING) {
            SorterUtilsInt.reverse(array, start, endP1);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);

        MaskInfoInt maskInfo;
        if (n >= SIZE_FOR_PARALLEL_BIT_MASK) {
            maskInfo = MaskInfoInt.calculateMaskInParallelBreakIfUpperBit(array, start, endP1, ArrayParallelRunner.APR_PARAMETERS_TWO_THREADS);
        } else {
            maskInfo = MaskInfoInt.calculateMaskBreakIfUpperBit(array, start, endP1, null);
        }

        if (maskInfo.isUpperBitMaskSet()) { //there are negative numbers and positive numbers
            int finalLeft = isUnsigned()
                    ? SorterUtilsInt.partitionNotStableUpperBit(array, start, endP1)
                    : SorterUtilsInt.partitionReverseNotStableUpperBit(array, start, endP1);
            int n1 = finalLeft - start;
            int n2 = endP1 - finalLeft;
            int[] threadNumbers = splitWork(n1, n2, maxThreads);
            ParallelRunner.runTwoRunnable(
                    n1 > 1 ? () -> { //sort negative numbers
                        int maxThreads1 = threadNumbers[0];
                        if (maxThreads1 <= 1) {
                            getSTIntSorter().sort(array, start, endP1);
                            return;
                        }

                        MaskInfoInt maskInfo1;
                        if (n1 >= SIZE_FOR_PARALLEL_BIT_MASK) {
                            maskInfo1 = MaskInfoInt.calculateMaskInParallel(array, start, finalLeft, ArrayParallelRunner.APR_PARAMETERS_TWO_THREADS);
                        } else {
                            maskInfo1 = MaskInfoInt.calculateMask(array, start, finalLeft);
                        }
                        int mask1 = maskInfo1.getMask();
                        int[] bList1 = MaskInfoInt.getMaskAsArray(mask1);
                        sort(array, start, finalLeft, bList1, maxThreads1);
                    } : null, n1,
                    n2 > 1 ? () -> { //sort positive numbers
                        int maxThreads2 = threadNumbers[1];
                        if (maxThreads2 <= 1) {
                            getSTIntSorter().sort(array, start, endP1);
                            return;
                        }
                        MaskInfoInt maskInfo2;
                        if (n2 >= SIZE_FOR_PARALLEL_BIT_MASK) {
                            maskInfo2 = MaskInfoInt.calculateMaskInParallel(array, finalLeft, endP1, ArrayParallelRunner.APR_PARAMETERS_TWO_THREADS);
                        } else {
                            maskInfo2 = MaskInfoInt.calculateMask(array, finalLeft, endP1);
                        }
                        int mask2 = maskInfo2.getMask();
                        int[] bList2 = MaskInfoInt.getMaskAsArray(mask2);
                        sort(array, finalLeft, endP1, bList2, maxThreads2);
                    } : null, n2, params.getDataSizeForThreads(), maxThreads);

        } else {
            int mask = maskInfo.getMask();
            if (mask != 0) {
                int[] bList = MaskInfoInt.getMaskAsArray(mask);
                sort(array, start, endP1, bList, maxThreads);
            }
        }
    }

}
