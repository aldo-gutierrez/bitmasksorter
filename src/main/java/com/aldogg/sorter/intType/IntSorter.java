package com.aldogg.sorter.intType;

import com.aldogg.sorter.Sorter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

public interface IntSorter extends Sorter {
    default void sort(int[] array) {
        sort(array, 0, array.length);
    }

    void sort(int[] array, int start, int end);

    default void sort(int[] array, int start, int end, int[] kList) {
        throw new NotImplementedException();
    }

    default void sort(List<Integer> list) {
        sort(list, 0, list.size());
    }

    default void sort(List<Integer> list, int start, int end) {
        int n = end - start;
        int[] a = new int[n];
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
