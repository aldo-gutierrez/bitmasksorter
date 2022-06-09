package com.aldogg.sorter;

import com.aldogg.parallel.SorterRunner;
import com.aldogg.sorter.intType.CountSort;
import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static com.aldogg.sorter.BitSorterParams.*;
import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.intType.IntSorterUtils.sortShortK;

/**
 * Experimental Bit Sorter
 */
public class MixedBitSorterMTInt implements IntSorter {
    final AtomicInteger numThreads = new AtomicInteger(1);
    protected final BitSorterParams params = BitSorterParams.getMTParams();
    boolean unsigned = false;

    private Map<Integer, BiConsumer<int[], Integer>> snFunction;

    @Override
    public boolean isUnsigned() {
        return unsigned;
    }

    @Override
    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

    @Override
    public void sort(int[] array) {
        if (array.length < 2) {
            return;
        }
        final int start = 0;
        final int end = array.length;
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, end) : listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(array, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        int[] maskParts = getMaskBit(array, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsArray(mask);
        if (kList.length == 0) {
            return;
        }
        snFunction = unsigned ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions;

        if (kList[0] == 31) { //there are negative numbers and positive numbers
            int sortMask = BitSorterUtils.getMaskBit(kList[0]);
            int finalLeft = isUnsigned()
                    ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                    : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);

            int size1 = finalLeft - start;
            int size2 = end - finalLeft;
            SorterRunner.runTwoRunnable(
                    size1 > 1 ? () -> { //sort negative numbers
                        int[] maskParts1 = getMaskBit(array, start, finalLeft);
                        int mask1 = maskParts1[0] & maskParts1[1];
                        int[] kList1 = getMaskAsArray(mask1);
                        sort(array, start, finalLeft, kList1, 0);
                    } : null, size1,
                    size2 > 1 ? () -> { //sort positive numbers
                        int[] maskParts2 = getMaskBit(array, finalLeft, end);
                        int mask2 = maskParts2[0] & maskParts2[1];
                        int[] kList2 = getMaskAsArray(mask2);
                        sort(array, finalLeft, end, kList2, 0);
                    } : null, size2, params.getDataSizeForThreads(),params.getMaxThreads(),  numThreads);
        } else {
            sort(array, start, end, kList, 0);
        }
    }



    public void sort(final int[] array, final int start, final int end, int[] kList, int kIndex) {
        final int n = end - start;
        if (n <= VERY_SMALL_N_SIZE) {
            snFunction.get(n).accept(array, start);
            return;
        }
        int kDiff = kList.length - kIndex;
        if (kDiff  <= params.getShortKBits()) {
            if (kDiff < 1) {
                return;
            }
            sortShortK(array, start, end, kList, kIndex);
            return;
        }

        //kIndex == level, starting with 0
        if (kIndex >= params.getMaxThreadsBits() - 1) {
            radixCountSort(array, start, end, kList, kIndex);
        } else {
            int sortMask = getMaskBit(kList[kIndex]);
            int finalLeft = IntSorterUtils.partitionNotStable(array, start, end, sortMask);
            Thread t1 = null;
            int size1 = finalLeft - start;
            if (size1 > 1) {
                if (numThreads.get() < params.getMaxThreads() + 1) {
                    Runnable r1 = () -> sort(array, start, finalLeft, kList, kIndex + 1);
                    t1 = new Thread(r1);
                    t1.start();
                    numThreads.addAndGet(1);
                } else {
                    sort(array, start, finalLeft, kList, kIndex + 1);
                }
            }
            int size2 = end - finalLeft;
            if (size2 > 1) {
                sort(array, finalLeft, end, kList, kIndex + 1);
            }

            if (t1 != null) {
                try {
                    t1.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    numThreads.addAndGet(-1);
                }
            }
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
            int sortMaskij = getMaskBit(kListIj);
            sortMask1 = sortMask1 | sortMaskij;
            bits++;
        }
        int twoPowerBits = twoPowerX(bits);
        partitionStableNonConsecutiveBitsAndCountSort(list, start, end, sortMask1, kList, kIndexCountSort, twoPowerBits, aux2);
    }

    //partitionStableLastBits
    protected void partitionStableNonConsecutiveBitsAndCountSort(final int[] list, final int start, final int end, int sortMask, int[] kList, int kIndex, int twoPowerK, final int[] aux) {
        int[] kListAux = getMaskAsArray(sortMask);
        Section[] sections = getMaskAsSections(kListAux);


        int[] leftX = new int[twoPowerK];
        int[] count = new int[twoPowerK];

        if (sections.length == 1) {
            Section section = sections[0];
            if (section.isSectionAtEnd()) {
                for (int i = start; i < end; i++) {
                    int element = list[i];
                    int elementMaskedShifted = element & section.sortMask;
                    count[elementMaskedShifted]++;
                }
            } else {
                for (int i = start; i < end; i++) {
                    int element = list[i];
                    int elementMaskedShifted = (element & section.sortMask) >> section.shiftRight;
                    count[elementMaskedShifted]++;
                }
            }
        } else {
            for (int i = start; i < end; i++) {
                int element = list[i];
                int elementMaskedShifted = getKeySN(element, sections);
                count[elementMaskedShifted]++;
            }
        }

        for (int i = 1; i < twoPowerK; i++) {
            leftX[i] = leftX[i - 1] + count[i - 1];
        }

        if (sections.length == 1) {
            Section section = sections[0];
            if (section.isSectionAtEnd()) {
                for (int i = start; i < end; i++) {
                    int element = list[i];
                    int elementMaskedShifted = element & section.sortMask;
                    aux[leftX[elementMaskedShifted]] = element;
                    leftX[elementMaskedShifted]++;
                }
            } else {
                for (int i = start; i < end; i++) {
                    int element = list[i];
                    int elementMaskedShifted = (element & section.sortMask) >> section.shiftRight;
                    aux[leftX[elementMaskedShifted]] = element;
                    leftX[elementMaskedShifted]++;
                }
            }

        } else {
            for (int i = start; i < end; i++) {
                int element = list[i];
                int elementMaskedShifted = getKeySN(element, sections);
                aux[leftX[elementMaskedShifted]] = element;
                leftX[elementMaskedShifted]++;
            }
        }

        if (kIndex > 0) {
            final int[] kListCountS = Arrays.copyOfRange(kList, kIndex, kList.length);
            final int kIndexCountS = 0;
            final int bufferCountSSize = twoPowerX(kListCountS.length - kIndexCountS);
            final Section[] sectionsCountS = getMaskAsSections(kListCountS);
            final int sortMaskCountS = getMaskLastBits(kListCountS, kIndexCountS);
            final int[] zeroBuffer = new int[bufferCountSSize];
            if (numThreads.get() < params.getMaxThreads() + 1) {
                Runnable r1 = () -> {
                    int[] bufferCount = new int[bufferCountSSize];
                    int[] bufferSize = new int[bufferCountSSize];
                    for (int i = 0; i < twoPowerK / 2; i++) {
                        smallListUtil(aux, leftX[i] - count[i], leftX[i], kListCountS, sectionsCountS, sortMaskCountS, bufferCount, bufferSize, zeroBuffer);
                    }
                };
                Thread t1 = new Thread(r1);
                t1.start();
                numThreads.addAndGet(1);
                int[] bufferCount = new int[bufferCountSSize];
                int[] bufferSize = new int[bufferCountSSize];
                for (int i = twoPowerK / 2; i < twoPowerK; i++) {
                    smallListUtil(aux, leftX[i] - count[i], leftX[i], kListCountS, sectionsCountS, sortMaskCountS, bufferCount, bufferSize, zeroBuffer);
                }
                try {
                    t1.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    numThreads.addAndGet(-1);
                }
            } else {
                int[] bufferCount = new int[bufferCountSSize];
                int[] bufferSize = new int[bufferCountSSize];
                for (int i = 0; i < twoPowerK; i++) {
                    smallListUtil(aux, leftX[i] - count[i], leftX[i], kListCountS,sectionsCountS, sortMaskCountS, bufferCount, bufferSize, zeroBuffer);
                }
            }

        }
        System.arraycopy(aux, 0, list, start, end - start);
    }

    private void smallListUtil(final int[] array, final int start, final int end, int[] kList, final Section[] sections, final int sortMask, final int[] bufferCount, final int[] bufferNumber, final int[] zeroBuffer) {
        int n = end - start;
        if (n > VERY_SMALL_N_SIZE) {
            int bufferLength = bufferCount.length;
            if (n < bufferLength >>COUNT_SORT_SMALL_NUMBER_SHIFT ) {
                int[] aux = new int[n];
                for (int i = kList.length - 1; i >= 0; i--) {
                    IntSorterUtils.partitionStable(array, start, end, BitSorterUtils.getMaskBit(kList[i]), aux);
                }
            } else {
                CountSort.countSort(array, start, end, sortMask, sections, bufferCount, bufferNumber);
                System.arraycopy(zeroBuffer, 0, bufferCount, 0, bufferLength);
                System.arraycopy(zeroBuffer, 0, bufferNumber, 0, bufferLength);
            }

        } else if (n > 1) {
            snFunction.get(n).accept(array, start);
        }
    }

}
