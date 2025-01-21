package com.aldogg.sorter.shared;

import com.aldogg.sorter.FieldSortOptions;

import java.util.List;

public interface SorterPrimitive<P> extends Sorter {

    default FieldSortOptions getDefaultFieldSortOptions() {
        FieldSortOptions fieldSortOptions = new FieldSortOptions();
        fieldSortOptions.setStable(false);
        return fieldSortOptions;
    }

    default void sort(List<P> list) {
        sort(list, 0, list.size(), getDefaultFieldSortOptions());
    }

    default void sortDesc(List<P> list) {
        FieldSortOptions defaultFieldSortOptions = getDefaultFieldSortOptions();
        defaultFieldSortOptions.setOrder(FieldSortOptions.ORDER.DESC);
        sort(list, 0, list.size(), defaultFieldSortOptions);
    }

    default void sort(List<P> list, int start, int endP1) {
        sort(list, start, endP1, getDefaultFieldSortOptions());
    }

    default void sortDesc(List<P> list, int start, int endP1) {
        FieldSortOptions defaultFieldSortOptions = getDefaultFieldSortOptions();
        defaultFieldSortOptions.setOrder(FieldSortOptions.ORDER.DESC);
        sort(list, start, endP1, defaultFieldSortOptions);
    }

    default void sort(P[] list) {
        sort(list, 0, list.length, getDefaultFieldSortOptions());
    }

    default void sortDesc(P[] list) {
        FieldSortOptions defaultFieldSortOptions = getDefaultFieldSortOptions();
        defaultFieldSortOptions.setOrder(FieldSortOptions.ORDER.DESC);
        sort(list, 0, list.length, defaultFieldSortOptions);
    }

    default void sort(P[] list, int start, int endP1) {
        sort(list, start, endP1, getDefaultFieldSortOptions());
    }

    default void sortDesc(P[] list, int start, int endP1) {
        FieldSortOptions defaultFieldSortOptions = getDefaultFieldSortOptions();
        defaultFieldSortOptions.setOrder(FieldSortOptions.ORDER.DESC);
        sort(list, start, endP1, defaultFieldSortOptions);
    }

    void sort(List<P> list, int start, int endP1, FieldSortOptions options);


    void sort(P[] list, int start, int endP1, FieldSortOptions options);

}
