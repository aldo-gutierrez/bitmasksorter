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
        int kDiff = kList.length;
        if (kDiff <= params.getShortKBits()) {
            if (kDiff < 1) {
                return;
            }
            sortShortK(array, start, end, kList, 0);
            return;
        }


        int maxThreads = (Integer) multiThreadParams;
        int tBits = BitSorterUtils.logBase2(maxThreads);
        if (!(1 << tBits == maxThreads)) {
            tBits += 1;
        }
        int threadBits = Math.min(tBits, kList.length);
        int sortMask = IntSorterUtils.getIntMask(kList, 0, threadBits - 1);

        partitionStableNonConsecutiveBitsAndRadixSort(array, start, end, sortMask, threadBits, kList);
    }

    protected void partitionStableNonConsecutiveBitsAndRadixSort(final int[] array, final int start, final int end, int sortMask, int threadBits, int[] kList) {
        int n = end - start;
        int[] aux = new int[n];

        int remainingBits = kList.length - threadBits;

        int[] kListAux = MaskInfoInt.getMaskAsArray(sortMask);
        IntSectionsInfo sectionsInfo = getMaskAsSections(kListAux, 0, kListAux.length - 1);
        IntSection[] sections = sectionsInfo.sections;

        int[] leftX;

        if (sections.length == 1) {
            IntSection section = sections[0];
            if (section.isSectionAtEnd()) {
                if (n > 2000000) {
                    leftX = IntSorterUtils.partitionStableLastBitsParallel(array, start, section, aux, n);
                } else {
                    leftX = IntSorterUtils.partitionStableLastBits(array, start, section, aux, 0, n);
                }
                System.arraycopy(aux, 0, array, start, n);
            } else {
                if (n > 2000000) {
                    leftX = IntSorterUtils.partitionStableOneGroupBitsParallel(array, start, section, aux, n);
                } else {
                    leftX = IntSorterUtils.partitionStableOneGroupBits(array, start, section, aux, 0, n);
                }
                System.arraycopy(aux, 0, array, start, n);
            }
        } else {
            //TODO code never reaches this path in test, add more tests
            leftX = IntSorterUtils.partitionStableNGroupBits(array, start, sectionsInfo, aux, 0, n);
            System.arraycopy(aux, 0, array, start, n);
        }


        if (remainingBits > 0) {
            int maxParts = 1 << threadBits;
            ParallelRunner runner = new ParallelRunner();
            runner.init(maxParts, 1);

            for (int i = 0; i < maxParts; i++) {
                int finalI = i;
                int lengthT = leftX[finalI] - (finalI == 0 ? 0 : leftX[finalI - 1]);
                if (lengthT > 1) {
                    Runnable r = () -> {
                        int endIBZ = leftX[finalI];
                        int startIBZ = endIBZ - lengthT;
                        if (remainingBits <= params.getShortKBits()) {
                            sortShortK(array, start + startIBZ, start + endIBZ, kList, threadBits);
                        } else {
                            RadixBitSorterInt.radixSort(array, start + startIBZ, start + endIBZ, kList, threadBits, kList.length - 1, aux, startIBZ);
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
