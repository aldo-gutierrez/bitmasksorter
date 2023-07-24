package com.aldogg.sorter.generic;

import com.aldogg.sorter.Sorter;
import com.aldogg.sorter.int_.object.IntMapper;

import java.util.List;
import java.util.ListIterator;

public interface SorterObjectInt<T> extends Sorter {

    default void sort(T[] array, IntMapper<T> mapper) {
        sort(array, mapper, 0, array.length);
    }

    void sort(T[] array, IntMapper<T> mapper, int start, int endP1);

    default void sort(List<T> list, IntMapper<T> mapper) {
        sort(list, mapper, 0, list.size());
    }

    default void sort(List<T> list, IntMapper<T> mapper, int start, int endP1) {
        int n = endP1 - start;
        T[] a = (T[]) new Object[n];
        int j = 0;
        List<T> subList = start == 0 && endP1 == list.size() ? list : list.subList(start, endP1);
        for (T value : subList) {
            a[j] = value;
            j++;
        }
        sort(a, mapper, 0, n);
        j = 0;
        ListIterator<T> iterator = subList.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.set(a[j]);
            j++;
        }
    }

}
