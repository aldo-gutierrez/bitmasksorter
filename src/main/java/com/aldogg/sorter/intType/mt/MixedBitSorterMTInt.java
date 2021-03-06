package com.aldogg.sorter.intType.mt;

import com.aldogg.parallel.SorterRunner;
import com.aldogg.sorter.*;
import com.aldogg.sorter.intType.IntBitMaskSorter;
import com.aldogg.sorter.intType.IntBitMaskSorterMT;
import com.aldogg.sorter.intType.IntSorterUtils;
import com.aldogg.sorter.intType.st.QuickBitSorterInt;
import com.aldogg.sorter.intType.st.RadixBitSorterInt;

import java.util.Arrays;

import static com.aldogg.sorter.BitSorterParams.*;
import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.intType.IntSorterUtils.sortShortK;

/**
 * Experimental Bit Sorter
 */
public class MixedBitSorterMTInt extends IntBitMaskSorterMT {

    @Override
    public void sort(int[] array, int start, int end, int[] kList) {
        int maxLevel = params.getMaxThreadsBits() - 1;
        if (kList[0] == 31) { //there are negative numbers and positive numbers
            int sortMask = 1 << kList[0];
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);

            int size1 = finalLeft - start;
            int size2 = end - finalLeft;
            SorterRunner.runTwoRunnable(
                    size1 > 1 ? () -> { //sort negative numbers
                        MaskInfo maskParts1 = MaskInfo.getMaskBit(array, start, finalLeft);
                        int mask1 = maskParts1.getMask();
                        int[] kList1 = MaskInfo.getMaskAsArray(mask1);
                        sort(array, start, finalLeft, kList1, 0, 1, maxLevel);
                    } : null, size1,
                    size2 > 1 ? () -> { //sort positive numbers
                        MaskInfo maskParts2 = MaskInfo.getMaskBit(array, finalLeft, end);
                        int mask2 = maskParts2.getMask();
                        int[] kList2 = MaskInfo.getMaskAsArray(mask2);
                        sort(array, finalLeft, end, kList2, 0, 1 , maxLevel);
                    } : null, size2, params.getDataSizeForThreads(), params.getMaxThreads(), numThreads);
        } else {
            sort(array, start, end, kList, 0, 0, maxLevel);
        }
    }

    public void sort(final int[] array, final int start, final int end, int[] kList, int kIndex, int level, int maxLevel) {
        final int n = end - start;
        if (n <= VERY_SMALL_N_SIZE) {
            snFunctions.get(n).accept(array, start);
            return;
        }
        int kDiff = kList.length - kIndex;
        if (kDiff <= params.getShortKBits()) {
            if (kDiff < 1) {
                return;
            }
            sortShortK(array, start, end, kList, kIndex);
            return;
        }

        if (level >= maxLevel) {
            radixCountSort(array, start, end, kList, kIndex);
        } else {
            int sortMask = 1 << kList[kIndex];
            int finalLeft = IntSorterUtils.partitionNotStable(array, start, end, sortMask);
            int size1 = finalLeft - start;
            int size2 = end - finalLeft;
            SorterRunner.runTwoRunnable(
                    size1 > 1 ? () -> {
                        sort(array, start, finalLeft, kList, kIndex + 1, level +1, maxLevel);
                    } : null, size1,
                    size2 > 1 ? () -> {
                        sort(array, finalLeft, end, kList, kIndex + 1, level +1 , maxLevel);
                    } : null, size2, params.getDataSizeForThreads(), params.getMaxThreads(), numThreads);
        }
    }

    protected void radixCountSort(int[] list, int start, int end, int[] kList, int kIndexEnd) {
        int n = end - start;
        int[] aux2 = new int[n];
        //assert kIndexEnd  - kIndexStart >max 8
        int kIndexCountSort = kList.length - params.getShortKBits();
        int bits = 0;
        int sortMask1 = 0;
        for (int i = kIndexCountSort - 1; i >= kIndexEnd; i--) {
            int kListIj = kList[i];
            int sortMaskij = 1 << kListIj;
            sortMask1 = sortMask1 | sortMaskij;
            bits++;
        }
        int twoPowerBits = 1 << bits;
        partitionStableNonConsecutiveBitsAndCountSort(list, start, end, sortMask1, kList, kIndexCountSort, twoPowerBits, aux2);
    }

    //partitionStableLastBits
    protected void partitionStableNonConsecutiveBitsAndCountSort(final int[] list, final int start, final int end, int sortMask, int[] kList, int kIndex, int twoPowerK, final int[] aux) {
        int[] kListAux = MaskInfo.getMaskAsArray(sortMask);
        Section[] sections = getMaskAsSections(kListAux, 0, kListAux.length -1);

        int[] leftX = new int[twoPowerK];
        int[] count = new int[twoPowerK];

        int n = end - start;
        if (sections.length == 1) {
            Section section = sections[0];
            if (section.isSectionAtEnd()) {
                IntSorterUtils.partitionStableLastBits(list, start, end, section, leftX, count, aux);
                System.arraycopy(aux, 0, list, start, n);
            } else {
                IntSorterUtils.partitionStableOneGroupBits(list, start, end, section, leftX, count, aux);
                System.arraycopy(aux, 0, list, start, n);
            }
        } else {
            IntSorterUtils.partitionStableNGroupBits(list, start, end, sections, leftX, count, aux);
            System.arraycopy(aux, 0, list, start, n);
        }

        if (kIndex > 0) {
            final int[] kListCountS = Arrays.copyOfRange(kList, kIndex, kList.length);
            if (numThreads.get() < params.getMaxThreads() + 1) {
                Runnable r1 = () -> {
                    for (int i = 0; i < twoPowerK / 2; i++) {
                        int start1 = leftX[i] - count[i];
                        int end1 = leftX[i];
                        if (end1 - start1 > 1) {
                            smallListUtil(aux, start1, end1, kListCountS);
                        }
                    }
                };
                Thread t1 = new Thread(r1);
                t1.start();
                numThreads.addAndGet(1);
                for (int i = twoPowerK / 2; i < twoPowerK; i++) {
                    int start1 = leftX[i] - count[i];
                    int end1 = leftX[i];
                    if (end1 - start1 > 1) {
                        smallListUtil(aux, start1, end1, kListCountS);
                    }
                }
                try {
                    t1.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    numThreads.addAndGet(-1);
                }
            } else {
                for (int i = 0; i < twoPowerK; i++) {
                    int start1 = leftX[i] - count[i];
                    int end1 = leftX[i];
                    if (end1 - start1 > 1) {
                        smallListUtil(aux, start1, end1, kListCountS);
                    }
                }
            }

        }
        System.arraycopy(aux, 0, list, start, n);
    }

    private void smallListUtil(final int[] array, final int start, final int end, int[] kList) {
        int n = end - start;
        if (n <= VERY_SMALL_N_SIZE) {
            snFunctions.get(n).accept(array, start);
        } else if (kList.length <= params.getShortKBits()) {
            sortShortK(array, start, end, kList, 0);
        } else {
            int[] auxT = new int[n];
            RadixBitSorterInt.radixSort(array, start, end, kList, 0, kList.length - 1, auxT);
        }
    }

    @Override
    public IntBitMaskSorter getSTIntSorter() {
        return new QuickBitSorterInt();
    }
}
