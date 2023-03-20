package com.aldogg.sorter.long_;

import com.aldogg.sorter.Sorter;

import java.util.List;

public interface LongSorter extends Sorter {

    default void sort(long[] array) {
        sort(array, 0, array.length);
    }

    void sort(long[] array, int start, int end);

    default void sort(long[] array, int start, int end, int[] kList) {
        throw new UnsupportedOperationException();
    }

    default void sort(List<Long> list) {
        sort(list, 0, list.size());
    }

    default void sort(List<Long> list, int start, int end) {
        int n = end - start;
        long[] a = new long[n];
        int j = 0;
        for (int i = start; i < end; i++, j++) {
            a[j] = list.get(i);
        }
        sort(a, 0, n);
        j = 0;
        for (int i = start; i < end; i++, j++) {
            list.set(i, a[j]);
        }
    }

    int LONG_SIGN_BIT_POS = 63;
}
