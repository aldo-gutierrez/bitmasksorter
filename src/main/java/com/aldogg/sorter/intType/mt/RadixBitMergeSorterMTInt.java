package com.aldogg.sorter.intType.mt;

import com.aldogg.parallel.ArrayParallelRunner;
import com.aldogg.parallel.ArrayRunnable;
import com.aldogg.sorter.MaskInfo;
import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;
import com.aldogg.sorter.intType.st.MergeSorter2Int;
import com.aldogg.sorter.intType.st.RadixBitSorterInt;

import java.util.concurrent.atomic.AtomicInteger;

public class RadixBitMergeSorterMTInt implements IntSorter {

    public static final int NUM_THREADS_INITIAL = 1;
    protected final AtomicInteger numThreads = new AtomicInteger(NUM_THREADS_INITIAL);

    class SortInfo {
        int start;
        int end;
    }

    @Override
    public void sort(int[] array, int start, int end) {
        int[] aux = new int[end -start];
        ArrayParallelRunner.runInParallel(array, start, end, 16, numThreads, new ArrayRunnable<SortInfo>() {
            @Override
            public SortInfo map(final int[] list, final int start, final int end) {
                RadixBitSorterInt sorterInt = new RadixBitSorterInt() {
                    @Override
                    public void sort(int[] array, int start, int end, int[] kList) {
                        if (kList[0] == 31) { //there are negative numbers and positive numbers
                            MaskInfo maskInfo;
                            int mask;
                            int sortMask = 1 << kList[0];
                            int finalLeft = isUnsigned()
                                    ? IntSorterUtils.partitionNotStable(array, start, end, sortMask)
                                    : IntSorterUtils.partitionReverseNotStable(array, start, end, sortMask);
                            int n1 = finalLeft - start;
                            int n2 = end - finalLeft;
                            if (n1 > 1) { //sort negative numbers
                                maskInfo = MaskInfo.getMaskBit(array, start, finalLeft);
                                mask = maskInfo.getMask();
                                kList = MaskInfo.getMaskAsArray(mask);
                                radixSort(array, start, finalLeft, kList, 0, kList.length - 1, aux, start);
                            }
                            if (n2 > 1) { //sort positive numbers
                                maskInfo = MaskInfo.getMaskBit(array, finalLeft, end);
                                mask = maskInfo.getMask();
                                kList = MaskInfo.getMaskAsArray(mask);
                                radixSort(array, finalLeft, end, kList, 0, kList.length - 1, aux, start);
                            }
                        } else {
                            radixSort(array, start, end, kList, 0, kList.length - 1, aux, start);
                        }
                    }
                };
                sorterInt.sort(list, start, end);
                SortInfo sortInfo = new SortInfo();
                sortInfo.start = start;
                sortInfo.end = end;
                return sortInfo;
            }

            @Override
            public SortInfo reduce(final SortInfo m1, final SortInfo m2) {
                MergeSorter2Int.merge(array, m1.start, m2.end, m2.start, aux, m1.start);
                SortInfo sortInfo = new SortInfo();
                sortInfo.start = m1.start;
                sortInfo.end = m2.end;
                return sortInfo;
            }
        });
    }

}
