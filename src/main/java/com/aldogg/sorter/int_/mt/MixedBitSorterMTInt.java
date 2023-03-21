package com.aldogg.sorter.int_.mt;

import com.aldogg.parallel.ParallelRunner;
import com.aldogg.sorter.*;
import com.aldogg.sorter.int_.IntBitMaskSorter;
import com.aldogg.sorter.int_.IntBitMaskSorterMT;
import com.aldogg.sorter.int_.IntSorterUtils;
import com.aldogg.sorter.int_.st.QuickBitSorterInt;
import com.aldogg.sorter.int_.st.RadixBitSorterInt;

import java.util.Arrays;

import static com.aldogg.parallel.ArrayParallelRunner.splitWork;
import static com.aldogg.sorter.BitSorterParams.*;
import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.int_.IntSorterUtils.sortShortK;

/**
 * Experimental Bit Sorter
 */
public class MixedBitSorterMTInt extends IntBitMaskSorterMT {

    @Override
    public void sort(int[] array, int start, int end, int[] kList, Object multiThreadParams) {
        int maxLevel = params.getMaxThreadsBits() - 1;
        Integer maxThreads = (Integer) multiThreadParams;
        int level = params.getMaxThreads() == maxThreads ? 0 : 1;
        sort(array, start, end, kList, 0, level, maxLevel, maxThreads);
    }

    public void sort(final int[] array, final int start, final int end, int[] kList, int kIndex, int level, int maxLevel, int maxThreads) {
        final int n = end - start;
        if (n <= VERY_SMALL_N_SIZE) {
            snFunctions[n].accept(array, start);
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

        if (level >= maxLevel  || maxThreads == 1) {
            radixCountSort(array, start, end, kList, kIndex);
        } else {
            int sortMask = 1 << kList[kIndex];
            int finalLeft = IntSorterUtils.partitionNotStable(array, start, end, sortMask);
            int n1 = finalLeft - start;
            int n2 = end - finalLeft;
            int[] threadNumbers = splitWork(n1, n2, maxThreads);
            ParallelRunner.runTwoRunnable(
                    n1 > 1 ? () -> {
                        int maxThreads1 = threadNumbers[0];
                        sort(array, start, finalLeft, kList, kIndex + 1, level +1, maxLevel, maxThreads1);
                    } : null, n1,
                    n2 > 1 ? () -> {
                        int maxThreads2 = threadNumbers[1];
                        sort(array, finalLeft, end, kList, kIndex + 1, level +1 , maxLevel, maxThreads2);
                    } : null, n2, params.getDataSizeForThreads(), maxThreads);
        }
    }

    protected void radixCountSort(int[] list, int start, int end, int[] kList, int kIndexEnd) {
        int n = end - start;
        int[] aux2 = new int[n];
        int kIndexCountSort = kList.length - params.getShortKBits();
        int bits = 0;
        int sortMask = 0;
        for (int i = kIndexCountSort - 1; i >= kIndexEnd; i--) {
            int sortMaskI = 1 << kList[i];
            sortMask = sortMask | sortMaskI;
            bits++;
        }
        partitionStableNonConsecutiveBitsAndCountSort(list, start, end, sortMask, kList, kIndexCountSort, aux2);
    }

    //partitionStableLastBits
    protected void partitionStableNonConsecutiveBitsAndCountSort(final int[] list, final int start, final int end, int sortMask, int[] kList, int kIndex, final int[] aux) {
        int[] kListAux = MaskInfoInt.getMaskAsArray(sortMask);
        int bits = kListAux.length;
        int twoPowerK = 1 << bits;
        IntSectionsInfo sectionsInfo = getMaskAsSections(kListAux, 0, kListAux.length - 1);
        IntSection[] sections = sectionsInfo.sections;
        int[] leftX;

        int n = end - start;
        if (sections.length == 1) {
            IntSection section = sections[0];
            if (section.isSectionAtEnd()) {
                leftX = IntSorterUtils.partitionStableLastBits(list, start, section, aux, n);
                System.arraycopy(aux, 0, list, start, n);
            } else {
                leftX = IntSorterUtils.partitionStableOneGroupBits(list, start, section, aux, n);
                System.arraycopy(aux, 0, list, start, n);
            }
        } else {
            //TODO code never reaches this path in test, add more tests
            leftX = IntSorterUtils.partitionStableNGroupBits(list, start, sectionsInfo, aux, n);
            System.arraycopy(aux, 0, list, start, n);
        }

        if (kIndex > 0) {
            final int[] kListCountS = Arrays.copyOfRange(kList, kIndex, kList.length);
            if (false) { //if (runningThreads.get() < params.getMaxThreads() + 1) {
                Runnable r1 = () -> {
                    for (int i = 0; i < twoPowerK / 2; i++) {
                        int start1 = i > 0 ? leftX[i - 1] : 0;
                        int end1 = leftX[i];
                        if (end1 - start1 > 1) {
                            smallListUtil(aux, start1, end1, kListCountS);
                        }
                    }
                };
                Thread t1 = new Thread(r1);
                t1.start();
                for (int i = twoPowerK / 2; i < twoPowerK; i++) {
                    int start1 = i > 0 ? leftX[i - 1] : 0;
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
                }
            } else {
                for (int i = 0; i < twoPowerK; i++) {
                    int start1 = i > 0 ? leftX[i - 1] : 0;
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
            snFunctions[n].accept(array, start);
        } else if (kList.length <= params.getShortKBits()) {
            sortShortK(array, start, end, kList, 0);
        } else {
            int[] auxT = new int[n];
            RadixBitSorterInt.radixSort(array, start, end, kList, 0, kList.length - 1, auxT);
        }
    }

    @Override
    public IntBitMaskSorter getSTIntSorter() {
        QuickBitSorterInt sorter = new QuickBitSorterInt();
        sorter.setUnsigned(isUnsigned());
        sorter.setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        return sorter;
    }
}
