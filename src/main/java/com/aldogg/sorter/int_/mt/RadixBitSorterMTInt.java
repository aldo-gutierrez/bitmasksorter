package com.aldogg.sorter.int_.mt;

import com.aldogg.parallel.ParallelRunner;
import com.aldogg.sorter.*;
import com.aldogg.sorter.int_.IntBitMaskSorter;
import com.aldogg.sorter.int_.IntBitMaskSorterMT;
import com.aldogg.sorter.int_.IntSorterUtils;
import com.aldogg.sorter.int_.st.RadixBitSorterInt;

import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.int_.IntSorterUtils.sortShortK;

public class RadixBitSorterMTInt extends IntBitMaskSorterMT {

    @Override
    public void sort(int[] array, int start, int end, int[] kList, Object multiThreadParams) {
        int maxThreadsBits = params.getMaxThreadsBits();
        maxThreadsBits = params.getMaxThreads() == (Integer) multiThreadParams ? maxThreadsBits : maxThreadsBits - 1;
        sort(array, start, end, kList, 0, maxThreadsBits);
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
            int sortMaskI = 1 << kList[i];
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
                if (n > 2000000) {
                    leftX = IntSorterUtils.partitionStableLastBitsParallel(array, start, section, aux, n);
                } else {
                    leftX = IntSorterUtils.partitionStableLastBits(array, start, section, aux, n);
                }
                System.arraycopy(aux, 0, array, start, n);
            } else {
                if (n > 2000000) {
                    leftX = IntSorterUtils.partitionStableOneGroupBitsParallel(array, start, section, aux, n);
                } else {
                    leftX = IntSorterUtils.partitionStableOneGroupBits(array, start, section, aux, n);
                }
                System.arraycopy(aux, 0, array, start, n);
            }
        } else {
            //TODO code never reaches this path in test, add more tests
            leftX = IntSorterUtils.partitionStableNGroupBits(array, start, sectionsInfo, aux, n);
            System.arraycopy(aux, 0, array, start, n);
        }


        if (remainingBits > 0) {
            ParallelRunner runner = new ParallelRunner();
            runner.init(maxProcessNumber, 1);

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
                    runner.preSubmit(r);
                }
            }
            runner.submit();
            runner.waitAll();
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
