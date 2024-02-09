package com.aldogg.sorter.int_.mt;

import com.aldogg.parallel.ParallelRunner;
import com.aldogg.sorter.*;
import com.aldogg.sorter.int_.BitMaskSorterInt;
import com.aldogg.sorter.int_.BitMaskSorterMTInt;
import com.aldogg.sorter.int_.SorterUtilsInt;
import com.aldogg.sorter.int_.SorterUtilsIntExt;
import com.aldogg.sorter.int_.st.RadixBitSorterInt;
import com.aldogg.sorter.shared.Section;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;

import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.int_.SorterUtilsIntExt.sortShortK;

public class RadixBitSorterMTInt extends BitMaskSorterMTInt {

    @Override
    public void sort(int[] array, int start, int endP1, FieldSorterOptions options, int[] bList, Object params) {
        int kDiff = bList.length;
        if (kDiff <= this.params.getShortKBits()) {
            if (kDiff < 1) {
                return;
            }
            sortShortK(array, start, endP1, bList, 0);
            return;
        }


        int maxThreads = (Integer) params;
        int tBits = BitSorterUtils.logBase2(maxThreads);
        if (!(1 << tBits == maxThreads)) {
            tBits += 1;
        }
        int threadBits = Math.min(tBits, bList.length);
        int sortMask = MaskInfoInt.getMask(bList, 0, threadBits - 1);

        partitionStableNonConsecutiveBitsAndRadixSort(array, start, endP1, sortMask, threadBits, bList);
    }

    protected void partitionStableNonConsecutiveBitsAndRadixSort(final int[] array, final int start, final int endP1, int sortMask, int threadBits, int[] bList) {
        int n = endP1 - start;
        int[] aux = new int[n];

        int remainingBits = bList.length - threadBits;

        int[] bListAux = MaskInfoInt.getMaskAsArray(sortMask);
        Section[] sections = getMaskAsSections(bListAux, 0, bListAux.length - 1);

        int[] leftX;

        if (sections.length == 1) {
            Section section = sections[0];
            if (section.shift == 0) {
                if (n > 2000000) {
                    leftX = SorterUtilsIntExt.partitionStableLastBitsParallel(array, start, section, aux, n);
                } else {
                    leftX = SorterUtilsInt.partitionStableLastBits(array, start, section, aux, 0, n);
                }
                System.arraycopy(aux, 0, array, start, n);
            } else {
                if (n > 2000000) {
                    leftX = SorterUtilsIntExt.partitionStableOneGroupBitsParallel(array, start, section, aux, n);
                } else {
                    leftX = SorterUtilsInt.partitionStableOneGroupBits(array, start, section, aux, 0, n);
                }
                System.arraycopy(aux, 0, array, start, n);
            }
        } else {
            //TODO code never reaches this path in test, add more tests
            leftX = SorterUtilsInt.partitionStableNGroupBits(array, start, sections, aux, 0, n);
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
                            sortShortK(array, start + startIBZ, start + endIBZ, bList, threadBits);
                        } else {
                            RadixBitSorterInt.radixSort(array, start + startIBZ, start + endIBZ, bList, threadBits, bList.length - 1, aux, startIBZ);
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
    public BitMaskSorterInt getSTIntSorter() {
        return new RadixBitSorterInt();
    }
}
