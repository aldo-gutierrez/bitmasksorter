package com.aldogg.sorter.int_.collection.mt;

import com.aldogg.parallel.ArrayParallelRunner;
import com.aldogg.parallel.ParallelRunner;
import com.aldogg.sorter.*;
import com.aldogg.sorter.int_.IntSorterUtils;
import com.aldogg.sorter.int_.collection.IntComparator;
import com.aldogg.sorter.int_.collection.ObjectIntSorter;
import com.aldogg.sorter.int_.collection.st.RadixBitSorterObjectInt;

import static com.aldogg.parallel.ArrayParallelRunner.splitWork;
import static com.aldogg.sorter.BitSorterUtils.getMaskAsSections;
import static com.aldogg.sorter.MaskInfoInt.SIZE_FOR_PARALLEL_BIT_MASK;
import static com.aldogg.sorter.int_.IntSorterUtils.*;
import static com.aldogg.sorter.int_.collection.ObjectIntSorterUtils.*;
import static com.aldogg.sorter.int_.collection.ObjectIntSorterUtils.partitionReverseNotStable;

public class RadixBitSorterMTObjectInt implements ObjectIntSorter {

    protected final BitSorterMTParams params = BitSorterMTParams.getMTParams();

    boolean unsigned = false;
    boolean stable = false;

    @Override
    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

    @Override
    public boolean isStable() {
        return stable;
    }

    @Override
    public void setStable(boolean stable) {
        this.stable = stable;
    }

    @Override
    public void sort(Object[] oArray, int start, int endP1, IntComparator comparator) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        int maxThreads = params.getMaxThreads();
        if (n <= params.getDataSizeForThreads() || maxThreads <= 1) {
            RadixBitSorterObjectInt stSorter = new RadixBitSorterObjectInt();
            stSorter.setUnsigned(isUnsigned());
            stSorter.sort(oArray, start, endP1, comparator);
            return;
        }

        int[] array = new int[n];
        for (int i = 0; i < array.length; i++) {
            array[i] = comparator.value(oArray[i]);
        }

        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, endP1) : listIsOrderedSigned(array, start, endP1);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(array, start, endP1);
            ObjectSorterUtils.reverse(oArray, start, endP1);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        MaskInfoInt maskInfo;
        if (n >= SIZE_FOR_PARALLEL_BIT_MASK) {
            maskInfo = MaskInfoInt.calculateMaskInParallelBreakIfUpperBit(array, start, endP1, ArrayParallelRunner.APR_PARAMETERS_TWO_THREADS);
        } else {
            maskInfo = MaskInfoInt.calculateMaskBreakIfUpperBit(array, start, endP1, null);
        }

        if (maskInfo == null || (maskInfo.getMask() & 0x80000000) != 0) { //there are negative numbers and positive numbers
            int sortMask = 1 << MaskInfoInt.UPPER_BIT;
            int finalLeft = isStable()
                    ? (isUnsigned()
                    ? partitionStable(oArray, array, start, endP1, sortMask)
                    : partitionReverseStable(oArray, array, start, endP1, sortMask))
                    : (isUnsigned()
                    ? partitionNotStable(oArray, array, start, endP1, sortMask)
                    : partitionReverseNotStable(oArray, array, start, endP1, sortMask));

            int n1 = finalLeft - start;
            int n2 = endP1 - finalLeft;
            int[] threadNumbers = splitWork(n1, n2, maxThreads);
            ParallelRunner.runTwoRunnable(
                    n1 > 1 ? () -> { //sort negative numbers
                        int maxThreads1 = threadNumbers[0];
                        MaskInfoInt maskInfo1;
                        if (n1 >= SIZE_FOR_PARALLEL_BIT_MASK & maxThreads1 >= 2) {
                            maskInfo1 = MaskInfoInt.calculateMaskInParallel(array, start, finalLeft, ArrayParallelRunner.APR_PARAMETERS_TWO_THREADS);
                        } else {
                            maskInfo1 = MaskInfoInt.calculateMask(array, start, finalLeft);
                        }
                        int mask1 = maskInfo1.getMask();
                        int[] bList1 = MaskInfoInt.getMaskAsArray(mask1);
                        radixSort(oArray, array, start, finalLeft, bList1, maxThreads1);
                    } : null, n1,
                    n2 > 1 ? () -> { //sort positive numbers
                        int maxThreads2 = threadNumbers[1];
                        MaskInfoInt maskInfo2;
                        if (n2 >= SIZE_FOR_PARALLEL_BIT_MASK & maxThreads2 >= 2) {
                            maskInfo2 = MaskInfoInt.calculateMaskInParallel(array, finalLeft, endP1, ArrayParallelRunner.APR_PARAMETERS_TWO_THREADS);
                        } else {
                            maskInfo2 = MaskInfoInt.calculateMask(array, finalLeft, endP1);
                        }
                        int mask2 = maskInfo2.getMask();
                        int[] bList2 = MaskInfoInt.getMaskAsArray(mask2);
                        radixSort(oArray, array, finalLeft, endP1, bList2, maxThreads2);
                    } : null, n2, params.getDataSizeForThreads(), maxThreads);

        } else {
            int mask = maskInfo.getMask();
            if (mask != 0) {
                int[] bList = MaskInfoInt.getMaskAsArray(mask);
                radixSort(oArray, array, start, endP1, bList, maxThreads);
            }
        }
    }

    private void radixSort(Object[] oArray, int[] array, int start, int endP1, int[] bList, Object multiThreadParams) {
        int maxThreads = (Integer) multiThreadParams;
        int tBits = BitSorterUtils.logBase2(maxThreads);
        if (!(1 << tBits == maxThreads)) {
            tBits += 1;
        }
        int threadBits = Math.min(tBits, bList.length);
        int sortMask = IntSorterUtils.getIntMask(bList, 0, threadBits - 1);
        partitionStableNonConsecutiveBitsAndRadixSort(oArray, array, start, endP1, sortMask, threadBits, bList);
    }

    protected void partitionStableNonConsecutiveBitsAndRadixSort(Object[] oArray, final int[] array, final int start, final int endP1, int sortMask, int threadBits, int[] bList) {
        int n = endP1 - start;
        Object[] oAux = new Object[n];
        int[] aux = new int[n];

        int maxProcessNumber = 1 << threadBits;
        int remainingBits = bList.length - threadBits;

        int[] bListAux = MaskInfoInt.getMaskAsArray(sortMask);
        IntSectionsInfo sectionsInfo = getMaskAsSections(bListAux, 0, bListAux.length - 1);
        IntSection[] sections = sectionsInfo.sections;

        int[] leftX;

        if (sections.length == 1) {
            IntSection section = sections[0];
            if (!section.isSectionAtEnd()) {
                leftX = partitionStableGroupBits(oArray, array, start, section, oAux, aux, 0, n);
            } else {
                leftX = partitionStableLastBits(oArray, array, start, section, oAux, aux, 0, n);
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
                int lengthT = leftX[finalI] - (finalI == 0 ? 0 : leftX[finalI - 1]);
                if (lengthT > 1) {
                    Runnable r = () -> {
                        int endIBZ = leftX[finalI];
                        int startIBZ = endIBZ - lengthT;
                        RadixBitSorterObjectInt.radixSort(oArray, array, start + startIBZ, start + endIBZ, bList, threadBits, bList.length - 1, oAux, aux, startIBZ);
                    };
                    runner.preSubmit(r);
                }
            }
            runner.submit();
            runner.waitAll();
        }
    }


}
