package com.aldogg.sorter.int_.mt;

import com.aldogg.parallel.ParallelRunner;
import com.aldogg.sorter.*;
import com.aldogg.sorter.int_.BitMaskSorterInt;
import com.aldogg.sorter.int_.BitMaskSorterMTInt;
import com.aldogg.sorter.int_.SorterUtilsInt;
import com.aldogg.sorter.int_.st.QuickBitSorterInt;
import com.aldogg.sorter.int_.st.RadixBitSorterInt;

import java.util.Arrays;

import static com.aldogg.parallel.ArrayParallelRunner.splitWork;
import static com.aldogg.sorter.BitSorterParams.*;
import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.int_.SorterUtilsInt.sortShortK;

/**
 * Experimental Bit Sorter
 */
public class MixedBitSorterMTInt extends BitMaskSorterMTInt {

    @Override
    public void sort(int[] array, int start, int endP1, int[] bList, Object params) {
        Integer maxThreads = (Integer) params;
        sort(array, start, endP1, bList, 0, maxThreads);
    }

    public void sort(final int[] array, final int start, final int endP1, int[] bList, int kIndex, int maxThreads) {
        final int n = endP1 - start;
        if (n <= VERY_SMALL_N_SIZE) {
            snFunctions[n].accept(array, start);
            return;
        }
        int kDiff = bList.length - kIndex;
        if (kDiff <= params.getShortKBits()) {
            if (kDiff < 1) {
                return;
            }
            sortShortK(array, start, endP1, bList, kIndex);
            return;
        }

        if (maxThreads == 1) {
            radixCountSort(array, start, endP1, bList, kIndex);
        } else {
            int sortMask = 1 << bList[kIndex];
            int finalLeft = SorterUtilsInt.partitionNotStable(array, start, endP1, sortMask);
            int n1 = finalLeft - start;
            int n2 = endP1 - finalLeft;
            int[] threadNumbers = splitWork(n1, n2, maxThreads);
            ParallelRunner.runTwoRunnable(
                    n1 > 1 ? () -> {
                        int maxThreads1 = threadNumbers[0];
                        sort(array, start, finalLeft, bList, kIndex + 1, maxThreads1);
                    } : null, n1,
                    n2 > 1 ? () -> {
                        int maxThreads2 = threadNumbers[1];
                        sort(array, finalLeft, endP1, bList, kIndex + 1, maxThreads2);
                    } : null, n2, params.getDataSizeForThreads(), maxThreads);
        }
    }

    protected void radixCountSort(int[] list, int start, int endP1, int[] bList, int kIndexEnd) {
        int kIndexCountSort = bList.length - params.getShortKBits();
        int sortMask = SorterUtilsInt.getIntMask(bList, kIndexEnd, kIndexCountSort - 1);
        partitionStableNonConsecutiveBitsAndCountSort(list, start, endP1, sortMask, bList, kIndexCountSort);
    }

    //partitionStableLastBits
    protected void partitionStableNonConsecutiveBitsAndCountSort(final int[] array, final int start, final int endP1, int sortMask, int[] bList, int kIndex) {
        int n = endP1 - start;
        int[] aux = new int[n];
        int[] bListAux = MaskInfoInt.getMaskAsArray(sortMask);
        int bits = bListAux.length;
        int dRange = 1 << bits;
        Section[] sections = getMaskAsSections(bListAux, 0, bListAux.length - 1);
        int[] leftX;

        if (sections.length == 1) {
            Section section = sections[0];
            if (section.isSectionAtEnd()) {
                leftX = SorterUtilsInt.partitionStableLastBits(array, start, section, aux, 0, n);
                System.arraycopy(aux, 0, array, start, n);
            } else {
                leftX = SorterUtilsInt.partitionStableOneGroupBits(array, start, section, aux, 0, n);
                System.arraycopy(aux, 0, array, start, n);
            }
        } else {
            //TODO code never reaches this path in test, add more tests
            leftX = SorterUtilsInt.partitionStableNGroupBits(array, start, sections, aux, 0, n);
            System.arraycopy(aux, 0, array, start, n);
        }

        if (kIndex > 0) {
            final int[] bListCountS = Arrays.copyOfRange(bList, kIndex, bList.length);
            for (int i = 0; i < dRange; i++) {
                int startI = i > 0 ? leftX[i - 1] : 0;
                int endI = leftX[i];
                if (endI - startI > 1) {
                    smallListUtil(aux, startI, endI, bListCountS, array);
                }
            }
        }
        System.arraycopy(aux, 0, array, start, n);
    }

    private void smallListUtil(final int[] array, final int start, final int end, int[] bList, int[] aux) {
        int n = end - start;
        if (n <= VERY_SMALL_N_SIZE) {
            snFunctions[n].accept(array, start);
        } else if (bList.length <= params.getShortKBits()) {
            sortShortK(array, start, end, bList, 0);
        } else {
            RadixBitSorterInt.radixSort(array, start, end, bList, 0, bList.length - 1, aux, start);
        }
    }

    @Override
    public BitMaskSorterInt getSTIntSorter() {
        QuickBitSorterInt sorter = new QuickBitSorterInt();
        sorter.setUnsigned(isUnsigned());
        sorter.setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        return sorter;
    }
}
