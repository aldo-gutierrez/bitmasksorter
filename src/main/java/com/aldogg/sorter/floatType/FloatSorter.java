package com.aldogg.sorter.floatType;

import com.aldogg.sorter.Sorter;

import java.util.List;

public interface FloatSorter extends Sorter {

    default void sort(float[] array) {
        sort(array, 0, array.length);
    }

    void sort(float[] array, int start, int end);

    default void sort(float[] array, int start, int end, int[] kList) {
        throw new UnsupportedOperationException();
    }

    default void sort(List<Float> list) {
        sort(list, 0, list.size());
    }

    default void sort(List<Float> list, int start, int end) {
        int n = end - start;
        float[] a = new float[n];
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
