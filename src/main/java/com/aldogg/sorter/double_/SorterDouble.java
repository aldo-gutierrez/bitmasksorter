package com.aldogg.sorter.double_;

import com.aldogg.sorter.Sorter;

import java.util.List;

public interface SorterDouble extends Sorter {

    default void sort(double[] array) {
        sort(array, 0, array.length);
    }

    void sort(double[] array, int start, int endP1);

    default void sort(double[] array, int start, int endP1, int[] bList) {
        throw new UnsupportedOperationException();
    }

    default void sort(List<Double> list) {
        sort(list, 0, list.size());
    }

    default void sort(List<Double> list, int start, int endP1) {
        int n = endP1 - start;
        double[] a = new double[n];
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

}
