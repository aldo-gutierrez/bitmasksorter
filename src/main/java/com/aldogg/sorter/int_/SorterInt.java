package com.aldogg.sorter.int_;

import com.aldogg.sorter.Sorter;

import java.util.*;

public interface SorterInt extends Sorter {
    default void sort(int[] array) {
        sort(array, 0, array.length);
    }

    void sort(int[] array, int start, int endP1);

    default void sort(int[] array, int start, int endP1, int[] bList, Object params) {
        throw new UnsupportedOperationException();
    }

    default void sort(List<Integer> list) {
        sort(list, 0, list.size());
    }

    default void sort(List<Integer> list, int start, int endP1) {
        int n = endP1 - start;
        int[] a = new int[n];
        int j = 0;
        List<Integer> subList = start == 0 && endP1 == list.size() ? list : list.subList(start, endP1);
        for (Integer value : subList) {
            a[j] = value;
            j++;
        }
        sort(a, 0, n);
        j = 0;
        ListIterator<Integer> iterator = subList.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.set(a[j]);
            j++;
        }
    }

}
