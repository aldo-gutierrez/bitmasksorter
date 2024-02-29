package com.aldogg.sorter.int_.mt;

import com.aldogg.parallel.ParallelRunner;
import com.aldogg.sorter.*;
import com.aldogg.sorter.int_.BitMaskSorterInt;
import com.aldogg.sorter.int_.BitMaskSorterMTInt;
import com.aldogg.sorter.int_.SorterUtilsInt;
import com.aldogg.sorter.int_.st.QuickBitSorterInt;
import com.aldogg.sorter.int_.st.RadixBitSorterInt;
import com.aldogg.sorter.int_.st.SortingNetworksInt;
import com.aldogg.sorter.shared.Section;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;

import java.util.Arrays;

import static com.aldogg.parallel.ArrayParallelRunner.splitWork;
import static com.aldogg.sorter.BitSorterParams.*;
import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.int_.SorterUtilsIntExt.sortShortK;

/**
 * Experimental Bit Sorter
 */
public class MixedBitSorterMTInt extends BitMaskSorterMTInt {

    @Override
    public void sort(int[] array, int start, int endP1, FieldSorterOptions options, int[] bList, Object params) {
        Integer maxThreads = (Integer) params;
        sort(array, start, endP1, bList, 0, maxThreads, options);
    }

    public void sort(final int[] array, final int start, final int endP1, int[] bList, int bListIndex, int maxThreads, FieldSorterOptions options) {
        final int n = endP1 - start;
        if (n <= VERY_SMALL_N_SIZE) {
            SortingNetworksInt.getInstance().sort(array, start, endP1, options);
            return;
        }
        int kDiff = bList.length - bListIndex;
        if (kDiff <= params.getShortKBits()) {
            if (kDiff < 1) {
                return;
            }
            sortShortK(array, start, endP1, bList, bListIndex);
            return;
        }

        if (maxThreads == 1) {
            radixCountSort(array, start, endP1, bList, bListIndex, options);
        } else {
            int sortMask = 1 << bList[bListIndex];
            int finalLeft = SorterUtilsInt.partitionNotStable(array, start, endP1, sortMask);
            int n1 = finalLeft - start;
            int n2 = endP1 - finalLeft;
            int[] threadNumbers = splitWork(n1, n2, maxThreads);
            ParallelRunner.runTwoRunnable(
                    n1 > 1 ? () -> {
                        int maxThreads1 = threadNumbers[0];
                        sort(array, start, finalLeft, bList, bListIndex + 1, maxThreads1, options);
                    } : null, n1,
                    n2 > 1 ? () -> {
                        int maxThreads2 = threadNumbers[1];
                        sort(array, finalLeft, endP1, bList, bListIndex + 1, maxThreads2, options);
                    } : null, n2, params.getDataSizeForThreads(), maxThreads);
        }
    }

    protected void radixCountSort(int[] list, int start, int endP1, int[] bList, int bListIndexEnd, FieldSorterOptions options) {
        int bListIndexCountSort = bList.length - params.getShortKBits();
        int sortMask = MaskInfoInt.getMask(bList, bListIndexEnd, bListIndexCountSort - 1);
        partitionStableNonConsecutiveBitsAndCountSort(list, start, endP1, sortMask, bList, bListIndexCountSort, options);
    }

    //partitionStableLastBits
    protected void partitionStableNonConsecutiveBitsAndCountSort(final int[] array, final int start, final int endP1, int sortMask, int[] bList, int bListIndex, FieldSorterOptions options) {
        int n = endP1 - start;
        int[] aux = new int[n];
        int[] bListAux = MaskInfoInt.getMaskAsArray(sortMask);
        int bits = bListAux.length;
        int dRange = 1 << bits;
        Section[] sections = getMaskAsSections(bListAux, 0, bListAux.length - 1);
        int[] leftX;

        if (sections.length == 1) {
            Section section = sections[0];
            if (section.shift == 0) {
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

        if (bListIndex > 0) {
            final int[] bListCountS = Arrays.copyOfRange(bList, bListIndex, bList.length);
            for (int i = 0; i < dRange; i++) {
                int startI = i > 0 ? leftX[i - 1] : 0;
                int endI = leftX[i];
                if (endI - startI > 1) {
                    smallListUtil(aux, startI, endI, bListCountS, array, options);
                }
            }
        }
        System.arraycopy(aux, 0, array, start, n);
    }

    private void smallListUtil(final int[] array, final int start, final int endP1, int[] bList, int[] aux, FieldSorterOptions options) {
        int n = endP1 - start;
        if (n <= VERY_SMALL_N_SIZE) {
            SortingNetworksInt.getInstance().sort(array, start, endP1, options);
            return;
        } else if (bList.length <= params.getShortKBits()) {
            sortShortK(array, start, endP1, bList, 0);
        } else {
            RadixBitSorterInt.radixSort(array, start, endP1, bList, 0, bList.length - 1, aux, start);
        }
    }

    @Override
    public BitMaskSorterInt getSTIntSorter() {
        return new QuickBitSorterInt();
    }
}
