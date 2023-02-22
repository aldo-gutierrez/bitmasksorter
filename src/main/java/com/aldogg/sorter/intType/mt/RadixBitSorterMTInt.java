package com.aldogg.sorter.intType.mt;

import com.aldogg.parallel.SorterRunner;
import com.aldogg.sorter.*;
import com.aldogg.sorter.intType.IntBitMaskSorter;
import com.aldogg.sorter.intType.IntBitMaskSorterMT;
import com.aldogg.sorter.intType.IntSorterUtils;
import com.aldogg.sorter.intType.st.RadixBitSorterInt;

import java.util.ArrayList;
import java.util.List;

import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.intType.IntSorterUtils.sortShortK;

public class RadixBitSorterMTInt extends IntBitMaskSorterMT {

    @Override
    public void sort(int[] array, int start, int end, int[] kList) {
        int maxThreadsBits = params.getMaxThreadsBits();
        if (kList[0] == SIGN_BIT_POS) { //there are negative numbers and positive numbers
            int sortMask = 1 << kList[0];
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
            int n1 = finalLeft - start;
            int n2 = end - finalLeft;
            SorterRunner.runTwoRunnable(
                    n1 > 1 ? () -> { //sort negative numbers
                        MaskInfoInt maskParts1 = MaskInfoInt.getMaskBit(array, start, finalLeft);
                        int mask1 = maskParts1.getMask();
                        int[] kList1 = MaskInfoInt.getMaskAsArray(mask1);
                        sort(array, start, finalLeft, kList1, 0, maxThreadsBits - 1);
                    } : null, n1,
                    n2 > 1 ? () -> { //sort positive numbers
                        MaskInfoInt maskParts2 = MaskInfoInt.getMaskBit(array, finalLeft, end);
                        int mask2 = maskParts2.getMask();
                        int[] kList2 = MaskInfoInt.getMaskAsArray(mask2);
                        sort(array, finalLeft, end, kList2, 0, maxThreadsBits - 1);
                    } : null, n2, params.getDataSizeForThreads(), 0, null);

        } else {
            sort(array, start, end, kList, 0, maxThreadsBits);
        }
    }

    public void sort(final int[] array, final int start, final int end, int[] kList, int kIndex, int paramsMaxThreadBits) {
        int kDiff = kList.length - kIndex;
        if (kDiff <= params.getShortKBits()) {
            if (kDiff < 1) {
                return;
            }
            sortShortK(array, start, end, kList, kIndex);
            return;
        }

        int n = end - start;
        int[] aux2 = new int[n];

        int threadBits = 0;
        int sortMask1 = 0;
        int maxThreadBits = Math.min(Math.max(paramsMaxThreadBits, 0), kList.length) - 1;
        for (int i = maxThreadBits; i >= 0; i--) {
            int kListI = kList[i];
            int sortMaskI = 1 << kListI;
            sortMask1 = sortMask1 | sortMaskI;
            threadBits++;
        }
        partitionStableNonConsecutiveBitsAndRadixSort(array, start, end, sortMask1, threadBits, kList, aux2);
    }

    protected void partitionStableNonConsecutiveBitsAndRadixSort(final int[] array, final int start, final int end, int sortMask, int threadBits, int[] kList, final int[] aux) {
        int maxProcessNumber = 1 << threadBits;
        int remainingBits = kList.length - threadBits;

        int[] kListAux = MaskInfoInt.getMaskAsArray(sortMask);
        IntSectionsInfo sectionsInfo = getMaskAsSections(kListAux, 0, kListAux.length - 1);
        IntSection[] sections = sectionsInfo.sections;

        int[] leftX;

        int n = end - start;
        if (sections.length == 1) {
            IntSection section = sections[0];
            if (section.isSectionAtEnd()) {
                leftX = IntSorterUtils.partitionStableLastBits(array, start, section, aux, n);
                System.arraycopy(aux, 0, array, start, n);
            } else {
                leftX = IntSorterUtils.partitionStableOneGroupBits(array, start, section, aux, n);
                System.arraycopy(aux, 0, array, start, n);
            }
        } else {
            //TODO code never reaches this path in test, add more tests
            leftX = IntSorterUtils.partitionStableNGroupBits(array, start, sectionsInfo, aux, n);
            System.arraycopy(aux, 0, array, start, n);
        }


        if (remainingBits > 0) {
            List<Runnable> runInThreadList = new ArrayList<>();
            List<Thread> threadList = new ArrayList<>();
            for (int i = 0; i < maxProcessNumber; i++) {
                int finalI = i;
                int lengthT = leftX[finalI] - (finalI == 0 ? 0 : leftX[finalI - 1]);
                if (lengthT > 1) {
                    Runnable r = () -> {
                        int endT = leftX[finalI];
                        if (remainingBits <= params.getShortKBits()) {
                            sortShortK(array, start + endT - lengthT, start + endT, kList, threadBits);
                        } else {
                            int[] auxT = new int[lengthT];
                            RadixBitSorterInt.radixSort(array, start + endT - lengthT, start + endT, kList, threadBits, kList.length - 1, auxT);
                        }
                    };
                    runInThreadList.add(r);
                }
            }

            for (int i = 0; i < runInThreadList.size(); i++) {
                Runnable r = runInThreadList.get(i);
                if (i == runInThreadList.size() - 1) {
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

    @Override
    public IntBitMaskSorter getSTIntSorter() {
        RadixBitSorterInt sorter = new RadixBitSorterInt();
        sorter.setUnsigned(isUnsigned());
        sorter.setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        return sorter;
    }
}
