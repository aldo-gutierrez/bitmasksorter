package com.aldogg.sorter.int_;

import com.aldogg.sorter.Sorter;

import java.util.List;

public interface IntSorter extends Sorter {
    default void sort(int[] array) {
        sort(array, 0, array.length);
    }

    void sort(int[] array, int start, int endP1);

    default void sort(int[] array, int start, int endP1, int[] kList, Object multiThreadParams) {
        throw new UnsupportedOperationException();
    }

    default void sort(List<Integer> list) {
        sort(list, 0, list.size());
    }

    default void sort(List<Integer> list, int start, int endP1) {
        int n = endP1 - start;
        int[] a = new int[n];
        int j = 0;
        for (int i = start; i < endP1; i++, j++) {
            a[j] = list.get(i);
        }
        sort(a, 0, n);
        j = 0;
        for (int i = start; i < endP1; i++, j++) {
            list.set(i, a[j]);
        }
    }

    int SIGN_BIT_POS = 31;

}
