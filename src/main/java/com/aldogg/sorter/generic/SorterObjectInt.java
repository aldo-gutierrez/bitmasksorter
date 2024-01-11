package com.aldogg.sorter.generic;

import com.aldogg.sorter.shared.Sorter;
import com.aldogg.sorter.int_.object.IntMapper;

import java.util.List;
import java.util.ListIterator;

public interface SorterObjectInt<T> extends Sorter {

    default void sort(T[] array, IntMapper<T> mapper) {
        sort(array, 0, array.length, mapper);
    }

    void sort(T[] array, int start, int endP1, IntMapper<T> mapper);

    default void sort(List<T> list, IntMapper<T> mapper) {
        sort(list, 0, list.size(), mapper);
    }

    default void sort(List<T> list, int start, int endP1, IntMapper<T> mapper) {
        int n = endP1 - start;
        T[] a = (T[]) new Object[n];
        int j = 0;
        List<T> subList = start == 0 && endP1 == list.size() ? list : list.subList(start, endP1);
        for (T value : subList) {
            a[j] = value;
            j++;
        }
        sort(a, 0, n, mapper);
        j = 0;
        ListIterator<T> iterator = subList.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.set(a[j]);
            j++;
        }
    }

}
