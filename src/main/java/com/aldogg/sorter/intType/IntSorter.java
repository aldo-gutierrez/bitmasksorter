package com.aldogg.sorter.intType;

public interface IntSorter extends Sorter {
    default void sort(int[] array) {
        sort(array, 0, array.length);
    }
    void sort(int[] array, int start, int end);

    default void sort(int[] array, int start, int end, int[] kList) {
        sort(array, start, end);
    }

}
