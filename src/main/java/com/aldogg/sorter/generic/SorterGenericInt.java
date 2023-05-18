package com.aldogg.sorter.generic;

import com.aldogg.sorter.Sorter;
import com.aldogg.sorter.int_.object.IntMapper;

import java.util.List;

public interface SorterGenericInt<T> extends Sorter {

    default boolean isIee754() {
        return false;
    }

    default void sort(T[] array, IntMapper<T> mapper) {
        sort(array, 0, array.length, mapper);
    }

    void sort(T[] array, int start, int endP1, IntMapper<T> mapper);

    default void sort(T[] array, int start, int endP1, int[] bList, Object params) {
        throw new UnsupportedOperationException();
    }

    default void sort(List<T> list, IntMapper<T> mapper) {
        sort(list, 0, list.size(), mapper);
    }

    default void sort(List<T> list, int start, int endP1, IntMapper<T> mapper) {
        int n = endP1 - start;
        T[] a = (T[]) new Object[n];
        int j = 0;
        for (int i = start; i < endP1; i++, j++) {
            a[j] = list.get(i);
        }
        sort(a, 0, n, mapper);
        j = 0;
        for (int i = start; i < endP1; i++, j++) {
            list.set(i, a[j]);
        }
    }

}
