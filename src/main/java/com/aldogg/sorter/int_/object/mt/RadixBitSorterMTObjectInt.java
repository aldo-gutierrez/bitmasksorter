package com.aldogg.sorter.int_.object.mt;

import com.aldogg.parallel.ArrayParallelRunner;
import com.aldogg.parallel.ParallelRunner;
import com.aldogg.sorter.*;
import com.aldogg.sorter.int_.SorterObjectInt;
import com.aldogg.sorter.generic.SorterUtilsGeneric;
import com.aldogg.sorter.int_.SorterUtilsInt;
import com.aldogg.sorter.int_.object.IntMapper;
import com.aldogg.sorter.int_.object.st.RadixBitSorterObjectInt;
import com.aldogg.sorter.shared.OrderAnalysisResult;
import com.aldogg.sorter.shared.Section;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;

import static com.aldogg.parallel.ArrayParallelRunner.splitWork;
import static com.aldogg.sorter.BitSorterUtils.getMaskAsSections;
import static com.aldogg.sorter.shared.FieldType.UNSIGNED_INTEGER;
import static com.aldogg.sorter.shared.int_mask.MaskInfoInt.SIZE_FOR_PARALLEL_BIT_MASK;
import static com.aldogg.sorter.int_.SorterUtilsInt.*;
import static com.aldogg.sorter.int_.object.SorterUtilsObjectInt.*;
import static com.aldogg.sorter.int_.object.SorterUtilsObjectInt.partitionReverseNotStable;

public class RadixBitSorterMTObjectInt<T> implements SorterObjectInt<T> {

    protected final BitSorterMTParams params = BitSorterMTParams.getMTParams();

    @Override
    public void sortNNA(T[] oArray, IntMapper<T> mapper, int oStart, int oEndP1, FieldSortOptions fieldSortOptions) {
        int n = oEndP1 - oStart;
        if (n < 2) {
            return;
        }
        int maxThreads = params.getMaxThreads();
        if (n <= params.getDataSizeForThreads() || maxThreads <= 1) {
            RadixBitSorterObjectInt sorter = new RadixBitSorterObjectInt();
            sorter.sortNNA(oArray, mapper, oStart, oEndP1, fieldSortOptions);
            return;
        }

        int[] array = new int[n];
        for (int i = 0; i < array.length; i++) {
            array[i] = mapper.value(oArray[oStart + i]);
        }

        int ordered = fieldSortOptions.getFieldType().equals(UNSIGNED_INTEGER) ? listIsOrderedUnSigned(array, oStart, oEndP1) : listIsOrderedSigned(array, oStart, oEndP1);
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

        RuntimeOptionsInt runtime = new RuntimeOptionsInt();
        runtime.array = array;
        runtime.oArray = oArray;
        if (maskInfo.isUpperBitMaskSet()) { //there are negative numbers and positive numbers
            int sortMask = MaskInfoInt.getUpperBitMask();
            int oFinalLeft = fieldSortOptions.isStable()
                    ? (fieldSortOptions.getFieldType().equals(UNSIGNED_INTEGER)
                    ? partitionStable(runtime, oStart, sortMask, n)
                    : partitionReverseStable(runtime, oStart, sortMask, n))
                    : (fieldSortOptions.getFieldType().equals(UNSIGNED_INTEGER)
                    ? partitionNotStable(oArray, oStart, array, 0, sortMask, n)
                    : partitionReverseNotStable(oArray, oStart, array, 0, sortMask, n));

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
                        if (mask1 != 0) {
                            RuntimeOptionsInt runtime1 = new RuntimeOptionsInt();
                            runtime1.oArray = runtime.oArray;
                            runtime1.array = runtime.array;
                            if (runtime.oAux == null) {
                                runtime1.aux = new int[n1];
                                runtime1.oAux = new Object[n1];
                            } else {
                                runtime1.aux = runtime.aux;
                                runtime1.oAux = runtime.oAux;
                            }
                            int[] bList1 = MaskInfoInt.getMaskAsArray(mask1);
                            radixSortParallel(runtime1, oStart, 0, bList1, n1, 0, maxThreads1);
                        }
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
                        if (mask2 != 0) {
                            int startAux;
                            RuntimeOptionsInt runtime2 = new RuntimeOptionsInt();
                            runtime2.oArray = runtime.oArray;
                            runtime2.array = runtime.array;
                            if (runtime.oAux == null) {
                                runtime2.aux = new int[n2];
                                runtime2.oAux = new Object[n2];
                                startAux = 0;
                            } else {
                                runtime2.aux = runtime.aux;
                                runtime2.oAux = runtime.oAux;
                                startAux = n1;
                            }
                            int[] bList2 = MaskInfoInt.getMaskAsArray(mask2);
                            radixSortParallel(runtime2, oFinalLeft, n1, bList2, n2, startAux, maxThreads2);
                        }
                    } : null, n2, params.getDataSizeForThreads(), maxThreads);

        } else {
            int mask = maskInfo.getMask();
            if (mask != 0) {
                if (runtime.oAux == null) {
                    runtime.aux = new int[n];
                    runtime.oAux = new Object[n];
                }
                int[] bList = MaskInfoInt.getMaskAsArray(mask);
                radixSortParallel(runtime, oStart, 0, bList, n, 0, maxThreads);
            }
        }
    }

    private void radixSortParallel(RuntimeOptionsInt runtime, int oStart, int aStart, int[] bList, int n, int startAux, int maxThreads) {
        int tBits = BitSorterUtils.logBase2(maxThreads);
        if (!(1 << tBits == maxThreads)) {
            tBits += 1;
        }
        int threadBits = Math.min(tBits, bList.length);
        int sortMask = MaskInfoInt.getMask(bList, 0, threadBits - 1);
        int maxProcessNumber = 1 << threadBits;
        int remainingBits = bList.length - threadBits;

        int[] bListAux = MaskInfoInt.getMaskAsArray(sortMask);
        Section[] sections = getMaskAsSections(bListAux, 0, bListAux.length - 1);
        int[] count;

        if (sections.length == 1) {
            Section section = sections[0];
            if (!(section.shift == 0)) {
                count = partitionStableOneGroupBits(runtime, oStart, aStart, section, startAux, n);
            } else {
                count = partitionStableLastBits(runtime, oStart, aStart, section, startAux, n);
            }
        } else {
            count = partitionStableNGroupBits(runtime, oStart, aStart, sections, startAux, n);
        }


        if (remainingBits > 0) {
            ParallelRunner runner = new ParallelRunner();
            runner.init(maxProcessNumber, 1);

            for (int i = 0; i < count.length; i++) {
                int finalI = i;
                int lengthT = count[finalI] - (finalI == 0 ? 0 : count[finalI - 1]);
                if (lengthT > 1) {
                    Runnable r = () -> {
                        int endIBZ = count[finalI];
                        int startIBZ = endIBZ - lengthT; //auxStart is BAD
                        RadixBitSorterObjectInt.radixSort(runtime, oStart + startIBZ, aStart + startIBZ, bList, threadBits, startAux + startIBZ, lengthT);
                    };
                    runner.preSubmit(r);
                }
            }
            runner.submit();
            runner.waitAll();
        }
    }


}
