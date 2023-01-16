package com.aldogg.sorter.byteType;

import com.aldogg.sorter.Sorter;

import java.util.List;

public interface ByteSorter extends Sorter {
    default void sort(byte[] array) {
        sort(array, 0, array.length);
    }

    void sort(byte[] array, int start, int end);

    default void sort(byte[] array, int start, int end, int[] kList) {
        throw new UnsupportedOperationException();
    }

    default void sort(List<Byte> list) {
        sort(list, 0, list.size());
    }

    default void sort(List<Byte> list, int start, int end) {
        int n = end - start;
        byte[] a = new byte[n];
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
