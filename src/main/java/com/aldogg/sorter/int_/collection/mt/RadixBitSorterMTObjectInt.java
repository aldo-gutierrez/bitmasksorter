package com.aldogg.sorter.int_.collection.mt;

import com.aldogg.parallel.ArrayParallelRunner;
import com.aldogg.parallel.ParallelRunner;
import com.aldogg.sorter.*;
import com.aldogg.sorter.int_.IntSorter;
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
    public void sort(Object[] oArray, int start, int end, IntComparator comparator) {
        int n = end - start;
        if (n < 2) {
            return;
        }
        int maxThreads = params.getMaxThreads();
        if (n <= params.getDataSizeForThreads() || maxThreads <= 1) {
            RadixBitSorterObjectInt stSorter = new RadixBitSorterObjectInt();
            stSorter.setUnsigned(isUnsigned());
            stSorter.sort(oArray, start, end, comparator);
            return;
        }

        int[] array = new int[n];
        for (int i = 0; i < array.length; i++) {
            array[i] = comparator.value(oArray[i]);
        }

        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, end) : listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(array, start, end);
            ObjectSorterUtils.reverse(oArray, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        MaskInfoInt maskInfo;
        if (n >= SIZE_FOR_PARALLEL_BIT_MASK) {
            maskInfo = MaskInfoInt.getMaskBitDetectSignBitParallel(array, start, end, new ArrayParallelRunner.APRParameters(2));
        } else {
            maskInfo = MaskInfoInt.getMaskBitDetectSignBit(array, start, end, null);
        }

        if (maskInfo == null) { //there are negative numbers and positive numbers
            int sortMask = 1 << IntSorter.SIGN_BIT_POS;
            int finalLeft = isStable()
                    ? (isUnsigned()
                    ? partitionStable(oArray, array, start, end, sortMask)
                    : partitionReverseStable(oArray, array, start, end, sortMask))
                    : (isUnsigned()
                    ? partitionNotStable(oArray, array, start, end, sortMask)
                    : partitionReverseNotStable(oArray, array, start, end, sortMask));

            int n1 = finalLeft - start;
            int n2 = end - finalLeft;
            int[] threadNumbers = splitWork(n1, n2, maxThreads);
            ParallelRunner.runTwoRunnable(
                    n1 > 1 ? () -> { //sort negative numbers
                        int maxThreads1 = threadNumbers[0];
                        MaskInfoInt maskInfo1;
                        if (n1 >= SIZE_FOR_PARALLEL_BIT_MASK & maxThreads1 >= 2) {
                            maskInfo1 = MaskInfoInt.getMaskBitParallel(array, start, finalLeft, new ArrayParallelRunner.APRParameters(2));
                        } else {
                            maskInfo1 = MaskInfoInt.getMaskBit(array, start, finalLeft);
                        }
                        int mask1 = maskInfo1.getMask();
                        int[] kList1 = MaskInfoInt.getMaskAsArray(mask1);
                        radixSort(oArray, array, start, finalLeft, kList1, maxThreads1);
                    } : null, n1,
                    n2 > 1 ? () -> { //sort positive numbers
                        int maxThreads2 = threadNumbers[1];
                        MaskInfoInt maskInfo2;
                        if (n2 >= SIZE_FOR_PARALLEL_BIT_MASK & maxThreads2 >= 2) {
                            maskInfo2 = MaskInfoInt.getMaskBitParallel(array, finalLeft, end, new ArrayParallelRunner.APRParameters(2));
                        } else {
                            maskInfo2 = MaskInfoInt.getMaskBit(array, finalLeft, end);
                        }
                        int mask2 = maskInfo2.getMask();
                        int[] kList2 = MaskInfoInt.getMaskAsArray(mask2);
                        radixSort(oArray, array, finalLeft, end, kList2, maxThreads2);
                    } : null, n2, params.getDataSizeForThreads(), maxThreads);

        } else {
            int mask = maskInfo.getMask();
            if (mask != 0) {
                int[] kList = MaskInfoInt.getMaskAsArray(mask);
                radixSort(oArray, array, start, end, kList, maxThreads);
            }
        }
    }

    private void radixSort(Object[] oArray, int[] array, int start, int end, int[] kList, Object multiThreadParams) {

        int maxThreadsBits = params.getMaxThreadsBits();
        maxThreadsBits = params.getMaxThreads() == (Integer) multiThreadParams ? maxThreadsBits : maxThreadsBits - 1;

        int threadBits = 0;
        int sortMask1 = 0;
        int maxThreadBits = Math.min(Math.max(maxThreadsBits, 0), kList.length) - 1;
        for (int i = maxThreadBits; i >= 0; i--) {
            sortMask1 = sortMask1 | 1 << kList[i];
            threadBits++;
        }
        partitionStableNonConsecutiveBitsAndRadixSort(oArray, array, start, end, sortMask1, threadBits, kList);

    }

    protected void partitionStableNonConsecutiveBitsAndRadixSort(Object[] oArray, final int[] array, final int start, final int end, int sortMask, int threadBits, int[] kList) {
        int n = end - start;
        Object[] oAux = new Object[n];
        int[] aux = new int[n];

        int maxProcessNumber = 1 << threadBits;
        int remainingBits = kList.length - threadBits;

        int[] kListAux = MaskInfoInt.getMaskAsArray(sortMask);
        IntSectionsInfo sectionsInfo = getMaskAsSections(kListAux, 0, kListAux.length - 1);
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
                        RadixBitSorterObjectInt.radixSort(oArray, array, start + startIBZ, start + endIBZ,kList, threadBits, kList.length - 1, oAux, aux, startIBZ);
                    };
                    runner.preSubmit(r);
                }
            }
            runner.submit();
            runner.waitAll();
        }
    }


}
