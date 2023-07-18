package com.aldogg.sorter.float_;

import com.aldogg.sorter.Sorter;

import java.util.List;
import java.util.ListIterator;

public interface SorterFloat extends Sorter {

    default void sort(float[] array) {
        sort(array, 0, array.length);
    }

    void sort(float[] array, int start, int endP1);

    default void sort(List<Float> list) {
        sort(list, 0, list.size());
    }

    default void sort(List<Float> list, int start, int endP1) {
        int n = endP1 - start;
        float[] a = new float[n];
        int j = 0;
        List<Float> subList = start == 0 && endP1 == list.size() ? list : list.subList(start, endP1);
        for (Float value : subList) {
            a[j] = value;
            j++;
        }
        sort(a, 0, n);
        j = 0;
        ListIterator<Float> iterator = subList.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.set(a[j]);
            j++;
        }
    }

}
