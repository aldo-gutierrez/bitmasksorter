package com.aldogg.sorter;

public interface Sorter extends Named {
    default FieldSorterOptions getFieldSorterOptions() {
        return new FieldSorterOptions() {
        };
    }

    default void setFieldSorterOptions(FieldSorterOptions options) {
        throw new UnsupportedOperationException();
    }

    /*
    default void sort(List<T> list) {
        sort(list, 0, list.size());
    }

    default void sort(T[] list) {
        sort(list, 0, list.length);
    }

    void sort(List<T> list, int start, int endP1);

    void sort(T[] list, int start, int endP1);
    */

}
