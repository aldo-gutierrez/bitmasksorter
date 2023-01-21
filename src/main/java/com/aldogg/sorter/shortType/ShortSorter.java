package com.aldogg.sorter.shortType;

import com.aldogg.sorter.Sorter;

import java.util.List;

public interface ShortSorter extends Sorter {
    default void sort(short[] array) {
        sort(array, 0, array.length);
    }

    void sort(short[] array, int start, int end);

    default void sort(short[] array, int start, int end, int[] kList) {
        throw new UnsupportedOperationException();
    }

    default void sort(List<Short> list) {
        sort(list, 0, list.size());
    }

    default void sort(List<Short> list, int start, int end) {
        int n = end - start;
        short[] a = new short[n];
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


}

