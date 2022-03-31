package com.aldogg.sorter;

import com.aldogg.sorter.intType.IntSorterUtils;

import java.util.ArrayList;
import java.util.List;

import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.intType.IntSorterUtils.sortShortList;

public class RadixBitSorterMTInt extends RadixBitSorterInt {
    public static final int MIN_SIZE_FOR_THREADS = 65536;
    protected final BitSorterParams params = BitSorterParams.getMTParams();

    @Override
    public void sort(int[] list) {
        if (list.length < 2) {
            return;
        }
        if (list.length <= MIN_SIZE_FOR_THREADS) {
            (new RadixBitSorterInt()).sort(list);
            return;
        }
        final int start = 0;
        final int end = list.length;
        //if (listIsOrdered(list, start, end)) return;

        int[] maskParts = getMaskBit(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);

        if (kList.length == 0) {
            return;
        }

        if (!isUnsigned() && kList[0] == 31) { //there are negative numbers and positive numbers
            int sortMask = BitSorterUtils.getMaskBit(kList[0]);
            int finalLeft = IntSorterUtils.partitionReverseNotStable(list, start, end, sortMask);
            if (finalLeft - start > 1) { //sort negative numbers
                maskParts = getMaskBit(list, start, finalLeft);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsList(mask);
                sort(list, start, finalLeft, kList, 0);
            }
            if (end - finalLeft > 1) { //sort positive numbers
                maskParts = getMaskBit(list, finalLeft, end);
                mask = maskParts[0] & maskParts[1];
                kList = getMaskAsList(mask);
                sort(list, finalLeft, end, kList, 0);
            }
        } else {
            sort(list, start, end, kList, 0);
        }
    }


    public void sort(final int[] list, final int start, final int end, int[] kList, int kIndex) {
        int kDiff = kList.length - kIndex;
        if (kDiff < 1) {
            return;
        }

        if (kDiff <= params.getCountingSortBits()) {
            sortShortList(list, start, end, kList, kIndex);
            return;
        }

        int length = end - start;
        int[] aux2 = new int[length];

        int threadBits = 0;
        int sortMask1 = 0;
        //we are using intentionaly the double of threads
        int maxThreadBits = Math.min(params.getMaxThreadsBits() - 1, kList.length);
        for (int i = maxThreadBits; i >= 0; i--) {
            int kListI = kList[i];
            int sortMaskI = getMaskBit(kListI);
            sortMask1 = sortMask1 | sortMaskI;
            threadBits++;
        }
        partitionStableNonConsecutiveBitsAndRadixSort(list, start, end, threadBits, aux2, sortMask1, kList);
    }

    protected void partitionStableNonConsecutiveBitsAndRadixSort(final int[] list, final int start, final int end, int threadBits, final int[] aux, int sortMask, int[] kList) {
        int maxProcessNumber = twoPowerX(threadBits);
        int remainingBits = kList.length - threadBits;

        int[] kListAux = getMaskAsList(sortMask);
        int[][] sections = getMaskAsSections(kListAux);


        int[] leftX = new int[maxProcessNumber];
        int[] count = new int[maxProcessNumber];


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

        for (int i = 1; i < maxProcessNumber; i++) {
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
        System.arraycopy(aux, 0, list, start, end - start);
        if (remainingBits > 0) {
            List<Runnable> runInThreadList = new ArrayList<>();
            List<Thread> threadList = new ArrayList<>();
            for (int i = 0; i < maxProcessNumber; i++) {
                int finalI = i;
                int lengthT = count[finalI];
                if (lengthT > 1) {
                    Runnable r = () -> {
                        int endT = leftX[finalI];
                        if (remainingBits <= params.getCountingSortBits()) {
                            sortShortList(list, start + endT - lengthT, start + endT, kList, threadBits);
                        } else {
                            int[] auxT = new int[lengthT];
                            RadixBitSorterInt.radixSort(list, start + endT - lengthT, start + endT, auxT, kList, kList.length - 1,threadBits);
                        }
                    };
                    runInThreadList.add(r);
                }
            }

            for (int i = 0; i < runInThreadList.size(); i++) {
                Runnable r = runInThreadList.get(i);
                if (i == runInThreadList.size() -1)  {
                    r.run();
                } else {
                    Thread t = new Thread(r);
                    t.start();
                    threadList.add(t);
                }
            }



            for (int t = 0; t < threadList.size(); t++) {
                try {
                    threadList.get(t).join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
