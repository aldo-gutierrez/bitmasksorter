package com.aldogg.sorter.generic;

import com.aldogg.sorter.shared.Sorter;
import com.aldogg.sorter.long_.object.LongMapper;

import java.util.List;
import java.util.ListIterator;

public interface SorterGenericLong<T> extends Sorter {
    default boolean isIee754() {
        return false;
    }

    default void sort(T[] array, LongMapper<T> mapper) {
        sort(array, 0, array.length, mapper);
    }

    void sort(T[] array, int start, int endP1, LongMapper<T> mapper);

    default void sort(T[] array, int start, int endP1, int[] bList, Object params) {
        throw new UnsupportedOperationException();
    }

    default void sort(List<T> list, LongMapper<T> mapper) {
        sort(list, 0, list.size(), mapper);
    }

    default void sort(List<T> list, int start, int endP1, LongMapper<T> mapper) {
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
