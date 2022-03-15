package com.aldogg;

import com.aldogg.intType.CountSort;
import com.aldogg.intType.IntSorterUtils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.aldogg.BitSorterParams.*;
import static com.aldogg.BitSorterUtils.*;
import static com.aldogg.intType.IntSorterUtils.sortShortList;

/**
 * Experimental Bit Sorter
 */
public class MixedBitSorterMTUInt extends RadixBitSorterInt {
    final AtomicInteger numThreads = new AtomicInteger(1);
    protected final BitSorterParams params = BitSorterParams.getMTParams();

    @Override
    public void sort(int[] list) {
        final int start = 0;
        final int end = list.length;
        if (list.length < 2) {
            return;
        }
        //if (listIsOrdered(list, start, end)) return;

        int[] maskParts = getMaskBit(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);
        sort(list, start, end, kList, 0);
    }



    public void sort(final int[] list, final int start, final int end, int[] kList, int kIndex) {
        final int listLength = end - start;
        if (listLength <= SMALL_LIST_SIZE) {
            SortingNetworks.sortSmallList(list, start, end);
            return;
        }
        int kDiff = kList.length - kIndex;
        if (kDiff < 1) {
            return;
        }

        if (kDiff  <= params.getCountingSortBits()) {
            sortShortList(list, start, end, kList, kIndex);
            return;
        }

        //kIndex == level, starting with 0
        if (kIndex >= params.getMaxThreadsBits() - 1) {
            radixCountSort(list, start, end, kList, kIndex);
        } else {
            int sortMask = getMaskBit(kList[kIndex]);
            int finalLeft = IntSorterUtils.partitionNotStable(list, start, end, sortMask);
            Thread t1 = null;
            int size1 = finalLeft - start;
            if (size1 > 1) {
                if (numThreads.get() < params.getMaxThreads() + 1) {
                    Runnable r1 = () -> sort(list, start, finalLeft, kList, kIndex + 1);
                    t1 = new Thread(r1);
                    t1.start();
                    numThreads.addAndGet(1);
                } else {
                    sort(list, start, finalLeft, kList, kIndex + 1);
                }
            }
            int size2 = end - finalLeft;
            if (size2 > 1) {
                sort(list, finalLeft, end, kList, kIndex + 1);
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
            int sortMaskij = getMaskBit(kListIj);
            sortMask1 = sortMask1 | sortMaskij;
            bits++;
        }
        int lengthBitsToNumber = BitSorterParams.twoPowerX(bits);
        partitionStableNonConsecutiveBitsAndCountSort(list, start, end, lengthBitsToNumber, aux2, sortMask1, kList, kIndexCountSort);
    }

    //partitionStableLastBits
    protected void partitionStableNonConsecutiveBitsAndCountSort(final int[] list, final int start, final int end, int lengthBitsToNumber, final int[] aux, int sortMask, int[] kList, int kIndex) {
        int[] kListAux = getMaskAsList(sortMask);
        int[][] sections = getMaskAsSections(kListAux);


        int[] leftX = new int[lengthBitsToNumber];
        int[] count = new int[lengthBitsToNumber];

        if (sections.length == 1) {
            for (int i = start; i < end; i++) {
                int element = list[i];
                int elementMaskedShifted = getKeySec1(element, sections[0]);
                count[elementMaskedShifted]++;
            }
        } else {
            for (int i = start; i < end; i++) {
                int element = list[i];
                int elementMaskedShifted = getKeySN(element, sections);
                count[elementMaskedShifted]++;
            }
        }

        for (int i = 1; i < lengthBitsToNumber; i++) {
            leftX[i] = leftX[i - 1] + count[i - 1];
        }

        if (sections.length == 1) {
            for (int i = start; i < end; i++) {
                int element = list[i];
                int elementMaskedShifted = getKeySec1(element, sections[0]);
                aux[leftX[elementMaskedShifted]] = element;
                leftX[elementMaskedShifted]++;
            }

        } else {
            for (int i = start; i < end; i++) {
                int element = list[i];
                int elementMaskedShifted = getKeySN(element, sections);
                aux[leftX[elementMaskedShifted]] = element;
                leftX[elementMaskedShifted]++;
            }
        }

        if (kIndex > 0) {
            final int[] kListCountS = Arrays.copyOfRange(kList, kIndex, kList.length);
            final int kIndexCountS = 0;
            final int bufferCountSSize = BitSorterParams.twoPowerX(kListCountS.length - kIndexCountS);
            final int[][] sectionsCountS = getMaskAsSections(kListCountS);
            final int sortMaskCountS = getMaskLastBits(kListCountS, kIndexCountS);
            final int[] zeroBuffer = new int[bufferCountSSize];
            if (numThreads.get() < params.getMaxThreads() + 1) {
                Runnable r1 = () -> {
                    int[] bufferCount = new int[bufferCountSSize];
                    int[] bufferSize = new int[bufferCountSSize];
                    for (int i = 0; i < lengthBitsToNumber / 2; i++) {
                        smallListUtil(aux, leftX[i] - count[i], leftX[i], kListCountS, sectionsCountS, sortMaskCountS, bufferCount, bufferSize, zeroBuffer);
                    }
                };
                Thread t1 = new Thread(r1);
                t1.start();
                numThreads.addAndGet(1);
                int[] bufferCount = new int[bufferCountSSize];
                int[] bufferSize = new int[bufferCountSSize];
                for (int i = lengthBitsToNumber / 2; i < lengthBitsToNumber; i++) {
                    smallListUtil(aux, leftX[i] - count[i], leftX[i], kListCountS, sectionsCountS, sortMaskCountS, bufferCount, bufferSize, zeroBuffer);
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
                    smallListUtil(aux, leftX[i] - count[i], leftX[i], kListCountS,sectionsCountS, sortMaskCountS, bufferCount, bufferSize, zeroBuffer);
                }
            }

        }
        System.arraycopy(aux, 0, list, start, end - start);
    }

    private void smallListUtil(final int[] list, final int start, final int end, int[] kList, final int[][] sections, final int sortMask, final int[] bufferCount, final int[] bufferNumber, final int[] zeroBuffer) {
        int length = end - start;
        if (length > SMALL_LIST_SIZE) {
            int bufferLength = bufferCount.length;
            if (length < bufferLength >>COUNT_SORT_SMALL_NUMBER_SHIFT ) {
                int[] aux = new int[length];
                for (int i = kList.length - 1; i >= 0; i--) {
                    IntSorterUtils.partitionStable(list, start, end, BitSorterUtils.getMaskBit(kList[i]), aux);
                }
            } else {
                CountSort.countSort(list, start, end, sortMask, sections, bufferCount, bufferNumber);
                System.arraycopy(zeroBuffer, 0, bufferCount, 0, bufferLength);
                System.arraycopy(zeroBuffer, 0, bufferNumber, 0, bufferLength);
            }

        } else if (length > 1) {
            SortingNetworks.sortSmallList(list, start, end);
        }
    }

}
