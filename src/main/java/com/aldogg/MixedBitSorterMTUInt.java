package com.aldogg;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.aldogg.BitSorterUtils.*;

/**
 * Experimental Bit Sorter
 */
public class MixedBitSorterMTUInt extends RadixBitSorter2UInt {
    AtomicInteger numThreads = new AtomicInteger(1);
    protected BitSorterParams params = BitSorterParams.getMTParams();

    @Override
    public void sort(int[] list) {
        final int start = 0;
        final int end = list.length;
        if (list.length < 1) {
            return;
        }
        //if (listIsOrdered(list, start, end)) return;

        int[] maskParts = getMask(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);
        if (kList.length <= params.getCountingSortBits()) {
            CountSort.countSort(list, start, end, kList, 0);
        } else {
            sort(list, start, end, kList, 0, 0);
        }
    }



    public void sort(final int[] list, final int start, final int end, int[] kList, int kIndex, int level) {
        final int listLength = end - start;
        if (listLength <= 8) {
            SortingNetworks.sortSmallList(list, start, end);
            return;
        }
        if (kIndex > kList.length - 1) {
            return;
        }

        if (kList.length - kIndex <= params.getCountingSortBits()) {
            CountSort.countSort(list, start, end, kList, kIndex);
            return;
        }

//        if (level == 0) {
        if (level >= params.getMaxThreadsBits() && (kList.length - kIndex) >= params.getCountingSortBits()) {
            radixCountSort(list, start, end, kList, kIndex);
        } else {
            int sortMask = getMask(kList[kIndex]);
            int finalLeft = IntSorterUtils.partition(list, start, end, sortMask);
            Thread t1 = null;
            int size1 = finalLeft - start;
            if (size1 > 1) {
                if (numThreads.get() < params.getMaxThreads() + 1) {
                    Runnable r1 = () -> sort(list, start, finalLeft, kList, kIndex + 1, level + 1);
                    t1 = new Thread(r1);
                    t1.start();
                    numThreads.addAndGet(1);
                } else {
                    sort(list, start, finalLeft, kList, kIndex + 1, level + 1);
                }
            }
            int size2 = end - finalLeft;
            if (size2 > 1) {
                sort(list, finalLeft, end, kList, kIndex + 1, level + 1);
            }

            if (t1 != null) {
                try {
                    t1.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    numThreads.addAndGet(-1);
                }
            }
        }
    }

    protected void radixCountSort(int[] list, int start, int end, int[] kList, int kIndexEnd) {
        int length = end - start;
        int[] aux2 = new int[length];
        //assert kIndexEnd  - kIndexStart >max 8
        int kIndexCountSort = kList.length - params.getCountingSortBits();
        int bits = 0;
        int sortMask1 = 0;
        for (int i = kIndexCountSort - 1; i >= kIndexEnd; i--) {
            int kListIj = kList[i];
            int sortMaskij = getMask(kListIj);
            sortMask1 = sortMask1 | sortMaskij;
            bits++;
        }
        stableRadixCountPartition(list, start, end, sortMask1, aux2, bits, kList, kIndexCountSort);
    }

    protected void stableRadixCountPartition(final int[] list, final int start, final int end, int sortMask, final int[] aux2, final int bits, int[] kList, int kIndex) {
        int[] kListAux = getMaskAsList(sortMask);
        int[][] sections = getMaskAsSections(kListAux);

        int lengthBitsToNumber = (int) Math.pow(2, bits);

        int[] leftX = new int[lengthBitsToNumber];
        int[] leftX2 = new int[lengthBitsToNumber];
        int[] count = new int[lengthBitsToNumber];

        for (int i = start; i < end; i++) {
            int element = list[i];
            int elementMaskedShifted = BitSorterUtils.getKey(element & sortMask, sections);
            count[elementMaskedShifted]++;
        }
        for (int i = 1; i < lengthBitsToNumber; i++) {
            leftX[i] = leftX[i - 1] + count[i - 1];
            leftX2[i] = leftX[i];
        }

        for (int i = start; i < end; i++) {
            int element = list[i];
            int elementMaskedShifted = BitSorterUtils.getKey(element & sortMask, sections);
            aux2[leftX[elementMaskedShifted]] = element;
            leftX[elementMaskedShifted]++;
        }

        if (kIndex > 0) {
            final int[] kListCountS = Arrays.copyOfRange(kList, kIndex, kList.length);
            final int kIndexCountS = 0;
            final int bufferCountSSize = (int) Math.pow(2, kListCountS.length - kIndexCountS);
            final int[][] sectionsCountS = getMaskAsSections(kListCountS);
            final int sortMaskCountS = getMask(kListCountS, kIndexCountS);
            final int[] zeroBuffer = new int[bufferCountSSize];
            if (numThreads.get() < params.getMaxThreads() + 1) {
                Runnable r1 = () -> {
                    int[] bufferCount = new int[bufferCountSSize];
                    int[] bufferSize = new int[bufferCountSSize];
                    for (int i = 0; i < lengthBitsToNumber / 2; i++) {
                        countSortUtil(aux2, leftX2[i], leftX[i], count[i], bufferCountSSize, sectionsCountS, sortMaskCountS, bufferCount, bufferSize, zeroBuffer);
                    }
                };
                Thread t1 = new Thread(r1);
                t1.start();
                numThreads.addAndGet(1);
                int[] bufferCount = new int[bufferCountSSize];
                int[] bufferSize = new int[bufferCountSSize];
                for (int i = lengthBitsToNumber / 2; i < lengthBitsToNumber; i++) {
                    countSortUtil(aux2, leftX2[i], leftX[i], count[i], bufferCountSSize, sectionsCountS, sortMaskCountS, bufferCount, bufferSize, zeroBuffer);
                }
                try {
                    t1.join();
                    numThreads.addAndGet(-1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                int[] bufferCount = new int[bufferCountSSize];
                int[] bufferSize = new int[bufferCountSSize];
                for (int i = 0; i < lengthBitsToNumber; i++) {
                    countSortUtil(aux2, leftX2[i], leftX[i], count[i], bufferCountSSize, sectionsCountS, sortMaskCountS, bufferCount, bufferSize, zeroBuffer);
                }
            }

        }
        System.arraycopy(aux2, 0, list, start, end - start);
    }

    private void countSortUtil(final int[] list, final int start , final int end, final int length, final int bufferSize, final int[][] sections, final int sortMask, final int[] bufferCount, final int[] bufferNumber, final int[] zeroBuffer) {
        if (length > 8) {
            CountSort.countSort(list, start, end, sortMask, sections, bufferCount, bufferNumber);
            System.arraycopy(zeroBuffer, 0, bufferCount, 0, bufferSize);
            System.arraycopy(zeroBuffer, 0, bufferNumber, 0, bufferSize);
        } else if (length > 1) {
            SortingNetworks.sortSmallList(list, start, end);
        }
    }

}
