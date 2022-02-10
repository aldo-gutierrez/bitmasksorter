package com.aldogg;

import java.util.concurrent.atomic.AtomicInteger;

import static com.aldogg.BitSorterUtils.getMask;
import static com.aldogg.BitSorterUtils.getMaskAsList;
import static com.aldogg.IntSorterUtils.sortList2to5Elements;

/**
 * Experimental Bit Sorter
 */
public class BitUIntSorterMT extends  RadixBitUIntSorter3{
    AtomicInteger numThreads = new AtomicInteger(1);
    protected BitSorterParams params = BitSorterParams.getMTParams();

    @Override
    public void sort(int[] list) {
        final int start = 0;
        final int end = list.length;
        int[] maskParts = getMask(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsList(mask);
        if (kList.length <= params.getCountingSortBits()) {
            CountSort.countSort(list, start, end, kList, 0);
        } else {
            sort(list, start, end, kList, 0, 0);
        }
    }


    public void sort(final int[] list, final int start, final int end, int[] kList, int kIndex, int level) {
        final int listLength = end - start;
        if (listLength <= 5) {
            sortList2to5Elements(list, start, end);
            return;
        }
        if (kIndex > kList.length - 1) {
            return;
        }

        if (kList.length - kIndex <= params.getCountingSortBits()) {
            CountSort.countSort(list, start, end, kList, kIndex);
            return;
        }

        if (level == params.getMaxThreadsBits()) {
            //int newKIndex = kIndex + kList.length - params.getCountingSortBits() -1;
            //CountSort.countSort(list, start, end, kList, newKIndex);
            //radixSort(list, start, end, kList, kList.length -  params.getCountingSortBits() - 1, kIndex );
            radixSort(list, start, end, kList, kList.length - 1, kIndex);
        } else {
            int sortMask = getMask(kList[kIndex]);
            int finalLeft = partition(list, start, end, sortMask);
            Thread t1 = null;
            int size1 = finalLeft - start;
            if (size1 > 1) {
                if (numThreads.get() < params.getMaxThreads() + 1) {
                    Runnable r1 = () -> sort(list, start, finalLeft, kList, kIndex + 1, level + 1);
                    t1 = new Thread(r1);
                    t1.start();
                    numThreads.addAndGet(1);
                }
            }
            int size2 = end - finalLeft;
            if (size2 > 1) {
                sort(list, finalLeft, end, kList, kIndex + 1, level + 1);
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

}
