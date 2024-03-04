package com.aldogg.sorter.int_.object.mt;

import com.aldogg.parallel.ArrayParallelRunner;
import com.aldogg.parallel.ParallelRunner;
import com.aldogg.sorter.*;
import com.aldogg.sorter.generic.SorterObjectInt;
import com.aldogg.sorter.generic.SorterUtilsGeneric;
import com.aldogg.sorter.int_.SorterUtilsInt;
import com.aldogg.sorter.int_.object.IntMapper;
import com.aldogg.sorter.int_.object.st.RadixBitSorterObjectInt;
import com.aldogg.sorter.shared.OrderAnalysisResult;
import com.aldogg.sorter.shared.Section;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;

import static com.aldogg.parallel.ArrayParallelRunner.splitWork;
import static com.aldogg.sorter.BitSorterUtils.getMaskAsSections;
import static com.aldogg.sorter.shared.int_mask.MaskInfoInt.SIZE_FOR_PARALLEL_BIT_MASK;
import static com.aldogg.sorter.int_.SorterUtilsInt.*;
import static com.aldogg.sorter.int_.object.SorterUtilsObjectInt.*;
import static com.aldogg.sorter.int_.object.SorterUtilsObjectInt.partitionReverseNotStable;

public class RadixBitSorterMTObjectInt<T> implements SorterObjectInt<T> {

    protected final BitSorterMTParams params = BitSorterMTParams.getMTParams();

    @Override
    public void sortNNA(T[] oArray, int oStart, int oEndP1, IntMapper<T> mapper) {
        int n = oEndP1 - oStart;
        if (n < 2) {
            return;
        }
        int maxThreads = params.getMaxThreads();
        if (n <= params.getDataSizeForThreads() || maxThreads <= 1) {
            RadixBitSorterObjectInt sorter = new RadixBitSorterObjectInt();
            sorter.sortNNA(oArray, oStart, oEndP1, mapper);
            return;
        }

        int[] array = new int[n];
        for (int i = 0; i < array.length; i++) {
            array[i] = mapper.value(oArray[oStart + i]);
        }

        int ordered = mapper.isUnsigned() ? listIsOrderedUnSigned(array, oStart, oEndP1) : listIsOrderedSigned(array, oStart, oEndP1);
        if (ordered == OrderAnalysisResult.DESCENDING) {
            SorterUtilsInt.reverse(array, 0, n);
            SorterUtilsGeneric.reverse(oArray, oStart, oEndP1);
        }
        if (ordered != OrderAnalysisResult.UNORDERED) return;

        MaskInfoInt maskInfo;
        if (n >= SIZE_FOR_PARALLEL_BIT_MASK) {
            maskInfo = MaskInfoInt.calculateMaskInParallelBreakIfUpperBit(array, 0, n, ArrayParallelRunner.APR_PARAMETERS_TWO_THREADS);
        } else {
            maskInfo = MaskInfoInt.calculateMaskBreakIfUpperBit(array, 0, n, null);
        }

        if (maskInfo.isUpperBitMaskSet()) { //there are negative numbers and positive numbers
            int sortMask = 1 << MaskInfoInt.UPPER_BIT;
            int oFinalLeft = ((FieldOptions) mapper).isStable()
                    ? (mapper.isUnsigned()
                    ? partitionStable(oArray, oStart, array, sortMask, n)
                    : partitionReverseStable(oArray, oStart, array, sortMask, n))
                    : (mapper.isUnsigned()
                    ? partitionNotStable(oArray, oStart, array, sortMask, n)
                    : partitionReverseNotStable(oArray, oStart, array, sortMask, n));

            int n1 = oFinalLeft - oStart;
            int n2 = oEndP1 - oFinalLeft;
            int[] threadNumbers = splitWork(n1, n2, maxThreads);
            ParallelRunner.runTwoRunnable(
                    n1 > 1 ? () -> { //sort negative numbers
                        int maxThreads1 = threadNumbers[0];
                        MaskInfoInt maskInfo1;
                        if (n1 >= SIZE_FOR_PARALLEL_BIT_MASK & maxThreads1 >= 2) {
                            maskInfo1 = MaskInfoInt.calculateMaskInParallel(array, 0, oFinalLeft - oStart, ArrayParallelRunner.APR_PARAMETERS_TWO_THREADS);
                        } else {
                            maskInfo1 = MaskInfoInt.calculateMask(array, 0, oFinalLeft - oStart);
                        }
                        int mask1 = maskInfo1.getMask();
                        int[] bList1 = MaskInfoInt.getMaskAsArray(mask1);
                        radixSort(oArray, oStart, array, 0, bList1, n1, maxThreads1);
                    } : null, n1,
                    n2 > 1 ? () -> { //sort positive numbers
                        int maxThreads2 = threadNumbers[1];
                        MaskInfoInt maskInfo2;
                        if (n2 >= SIZE_FOR_PARALLEL_BIT_MASK & maxThreads2 >= 2) {
                            maskInfo2 = MaskInfoInt.calculateMaskInParallel(array, oFinalLeft - oStart, n, ArrayParallelRunner.APR_PARAMETERS_TWO_THREADS);
                        } else {
                            maskInfo2 = MaskInfoInt.calculateMask(array, oFinalLeft - oStart, n);
                        }
                        int mask2 = maskInfo2.getMask();
                        int[] bList2 = MaskInfoInt.getMaskAsArray(mask2);
                        radixSort(oArray, oFinalLeft, array, n1, bList2, n2, maxThreads2);
                    } : null, n2, params.getDataSizeForThreads(), maxThreads);

        } else {
            int mask = maskInfo.getMask();
            if (mask != 0) {
                int[] bList = MaskInfoInt.getMaskAsArray(mask);
                radixSort(oArray, oStart, array, 0, bList, n, maxThreads);
            }
        }
    }

    private void radixSort(Object[] oArray, int oStart, int[] array, int aStart, int[] bList, int n, Object multiThreadParams) {
        int maxThreads = (Integer) multiThreadParams;
        int tBits = BitSorterUtils.logBase2(maxThreads);
        if (!(1 << tBits == maxThreads)) {
            tBits += 1;
        }
        int threadBits = Math.min(tBits, bList.length);
        int sortMask = MaskInfoInt.getMask(bList, 0, threadBits - 1);
        partitionStableNonConsecutiveBitsAndRadixSort(oArray, oStart, array, aStart, sortMask, threadBits, bList, n);
    }

    protected void partitionStableNonConsecutiveBitsAndRadixSort(Object[] oArray, final int oStart, final int[] array, int aStart, int sortMask, int threadBits, int[] bList, final int n) {
        Object[] oAux = new Object[n];
        int[] aux = new int[n];

        int maxProcessNumber = 1 << threadBits;
        int remainingBits = bList.length - threadBits;

        int[] bListAux = MaskInfoInt.getMaskAsArray(sortMask);
        Section[] sections = getMaskAsSections(bListAux, 0, bListAux.length - 1);

        int[] count;

        if (sections.length == 1) {
            Section section = sections[0];
            if (!(section.shift == 0)) {
                count = partitionStableOneGroupBits(oArray, oStart, array, aStart, section, oAux, aux, 0, n);
            } else {
                count = partitionStableLastBits(oArray, oStart, array, aStart, section, oAux, aux, 0, n);
            }
        } else {
            throw new UnsupportedOperationException("");
            //leftX = partitionStableNGroupBits(oArray, array, start, sectionsInfo, oAux, aux, 0, n);
        }


        if (remainingBits > 0) {
            ParallelRunner runner = new ParallelRunner();
            runner.init(maxProcessNumber, 1);

            for (int i = 0; i < maxProcessNumber; i++) {
                int finalI = i;
                int lengthT = count[finalI] - (finalI == 0 ? 0 : count[finalI - 1]);
                if (lengthT > 1) {
                    Runnable r = () -> {
                        int endIBZ = count[finalI];
                        int startIBZ = endIBZ - lengthT;
                        RadixBitSorterObjectInt.radixSort(oArray, oStart + startIBZ, array, aStart + startIBZ, bList, threadBits, bList.length - 1, oAux, aux, startIBZ, lengthT);
                    };
                    runner.preSubmit(r);
                }
            }
            runner.submit();
            runner.waitAll();
        }
    }


}
