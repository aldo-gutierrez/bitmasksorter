package com.aldogg.sorter;

import com.aldogg.parallel.SorterRunner;
import com.aldogg.sorter.intType.IntSorterUtils;

import java.util.ArrayList;
import java.util.List;

import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.intType.IntSorterUtils.sortShortKList;

public class RadixBitSorterMTInt extends RadixBitSorterInt {
    protected final BitSorterParams params = BitSorterParams.getMTParams();

    @Override
    public void sort(int[] list) {
        if (list.length < 2) {
            return;
        }
        if (list.length <= params.getDataSizeForThreads() || params.getMaxThreadsBits() == 0) {
            RadixBitSorterInt radixBitSorterInt = new RadixBitSorterInt();
            radixBitSorterInt.setUnsigned(isUnsigned());
            radixBitSorterInt.sort(list);
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

        if (kList[0] == 31) { //there are negative numbers and positive numbers
            int sortMask = BitSorterUtils.getMaskBit(kList[0]);
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(list, start, end, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(list, start, end, sortMask);
            int size1 = finalLeft - start;
            int size2 = end - finalLeft;
            SorterRunner.runTwoRunnable(
                    size1 > 1 ? (Runnable) () -> { //sort negative numbers
                        int[] maskParts1 = getMaskBit(list, start, finalLeft);
                        int mask1 = maskParts1[0] & maskParts1[1];
                        int[] kList1 = getMaskAsList(mask1);
                        sort(list, start, finalLeft, kList1, 0, params.getMaxThreadsBits() - 1);
                    } : null, size1,
                    size2 > 1 ? (Runnable) () -> { //sort positive numbers
                        int[] maskParts2 = getMaskBit(list, finalLeft, end);
                        int mask2 = maskParts2[0] & maskParts2[1];
                        int[] kList2 = getMaskAsList(mask2);
                        sort(list, finalLeft, end, kList2, 0, params.getMaxThreadsBits() - 1);
                    } : null, size2, params.getDataSizeForThreads(),0, null);

        } else {
            sort(list, start, end, kList, 0, params.getMaxThreadsBits());
        }
    }


    public void sort(final int[] list, final int start, final int end, int[] kList, int kIndex, int paramsMaxThreadBits) {
        int kDiff = kList.length - kIndex;
        if (kDiff < 1) {
            return;
        }

        if (kDiff <= params.getCountingSortBits()) {
            sortShortKList(list, start, end, kList, kIndex);
            return;
        }

        int length = end - start;
        int[] aux2 = new int[length];

        int threadBits = 0;
        int sortMask1 = 0;
        int maxThreadBits = Math.min(Math.max(paramsMaxThreadBits, 0), kList.length) - 1;
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
            for (int i = 1; i < maxProcessNumber; i++) {
                leftX[i] = leftX[i - 1] + count[i - 1];
            }
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
                count[elementMaskedShifted]++;
            }
            for (int i = 1; i < maxProcessNumber; i++) {
                leftX[i] = leftX[i - 1] + count[i - 1];
            }
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
                            sortShortKList(list, start + endT - lengthT, start + endT, kList, threadBits);
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
