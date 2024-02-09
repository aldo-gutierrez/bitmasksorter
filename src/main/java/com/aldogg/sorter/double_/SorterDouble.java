package com.aldogg.sorter.double_;

import com.aldogg.sorter.shared.Sorter;

import java.util.List;
import java.util.ListIterator;

public interface SorterDouble extends Sorter {

    default void sort(double[] array) {
        sort(array, 0, array.length);
    }

    void sort(double[] array, int start, int endP1);

    default void sort(List<Double> list) {
        sort(list, 0, list.size());
    }

    default void sort(List<Double> list, int start, int endP1) {
        int n = endP1 - start;
        double[] a = new double[n];
        int j = 0;
        List<Double> subList = start == 0 && endP1 == list.size() ? list : list.subList(start, endP1);
        for (Double value : subList) {
            a[j] = value;
            j++;
        }
        sort(a, 0, n);
        j = 0;
        ListIterator<Double> iterator = subList.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.set(a[j]);
            j++;
        }
    }

}
