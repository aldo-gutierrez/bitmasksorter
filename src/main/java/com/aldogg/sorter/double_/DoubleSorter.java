package com.aldogg.sorter.double_;

import com.aldogg.sorter.Sorter;

import java.util.List;

public interface DoubleSorter extends Sorter {

    default void sort(double[] array) {
        sort(array, 0, array.length);
    }

    void sort(double[] array, int start, int end);

    default void sort(double[] array, int start, int end, int[] kList) {
        throw new UnsupportedOperationException();
    }

    default void sort(List<Double> list) {
        sort(list, 0, list.size());
    }

    default void sort(List<Double> list, int start, int end) {
        int n = end - start;
        double[] a = new double[n];
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
