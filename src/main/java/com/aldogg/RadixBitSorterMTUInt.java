package com.aldogg;

import java.util.ArrayList;
import java.util.List;

import static com.aldogg.BitSorterUtils.*;
import static com.aldogg.intType.IntSorterUtils.sortShortList;

public class RadixBitSorterMTUInt extends RadixBitSorterInt {
    protected final BitSorterParams params = BitSorterParams.getMTParams();

    @Override
    public void sort(int[] list) {
        if (list.length < 2) {
            return;
        }
        final int start = 0;
        final int end = list.length;
        //if (listIsOrdered(list, start, end)) return;

        int[] maskParts = getMaskBit(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);
        sort(list, start, end, kList, 0);
    }


    public void sort(final int[] list, final int start, final int end, int[] kList, int kIndex) {
        final int listLength = end - start;
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

        int bits = 0;
        int sortMask1 = 0;
        for (int i = params.getMaxThreadsBits() - 1; i >= 0; i--) {
            int kListIj = kList[i];
            int sortMaskij = getMaskBit(kListIj);
            sortMask1 = sortMask1 | sortMaskij;
            bits++;
        }
        int lengthBitsToNumber = BitSorterParams.twoPowerX(bits);
        partitionStableNonConsecutiveBitsAndRadixSort(list, start, end, lengthBitsToNumber, aux2, sortMask1, kList, kList.length - params.getMaxThreadsBits());
    }

    protected void partitionStableNonConsecutiveBitsAndRadixSort(final int[] list, final int start, final int end, int lengthBitsToNumber, final int[] aux, int sortMask, int[] kList, int remainingBits) {
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
        System.arraycopy(aux, 0, list, start, end - start);
        if (remainingBits > 0) {
            List<Thread> threadList = new ArrayList<>();
            for (int i = 0; i < lengthBitsToNumber; i++) {
                int finalI = i;
                int lengthT = count[finalI];
                if (lengthT > 1) {
                    Runnable r1 = () -> {
                        int endT = leftX[finalI];
                        int[] auxT = new int[lengthT];
                        if (kList.length - params.getMaxThreadsBits() <= params.getCountingSortBits()) {
                            sortShortList(list, endT - lengthT, endT,  kList, params.getMaxThreadsBits());
                        } else {
                            RadixBitSorterInt.radixSort(list, endT - lengthT, endT, auxT, kList, kList.length - 1, params.getMaxThreadsBits());
                        }
                    };
                    if (i < lengthBitsToNumber - 1) {
                        Thread t1 = new Thread(r1);
                        threadList.add(t1);
                        t1.start();
                    } else {
                        r1.run();
                    }
                }
            }

            for (int t = 0; t < threadList.size(); t++) {
                try {
                    threadList.get(t).join();
                    //numThreads.addAndGet(-1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
