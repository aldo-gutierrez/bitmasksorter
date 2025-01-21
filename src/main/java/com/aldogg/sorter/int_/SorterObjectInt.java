package com.aldogg.sorter.int_;

import com.aldogg.sorter.FieldSortOptions;
import com.aldogg.sorter.shared.NullHandling;
import com.aldogg.sorter.int_.object.IntMapper;
import com.aldogg.sorter.shared.SorterObjectType;

import java.util.*;

public interface SorterObjectInt<T> extends SorterObjectType<T, Integer> {

    void sortNNA(T[] array, IntMapper<T> mapper, int start, int endP1, FieldSortOptions fieldSortOptions);

    default void sort(List<T> list, IntMapper<T> mapper) {
        sort(list, mapper, 0, list.size(), getDefaultFieldSortOptions());
    }

    default void sort(T[] array, IntMapper<T> mapper) {
        sort(array, mapper, 0, array.length, getDefaultFieldSortOptions());
    }

    default void sort(List<T> list, IntMapper<T> mapper, int start, int endP1, FieldSortOptions fieldSortOptions) {
        int nulls = 0;
        boolean throwExceptionIfNull = fieldSortOptions.getNullHandling().equals(NullHandling.NO_HANDLING);
        List<T> subList = start == 0 && endP1 == list.size() ? list : list.subList(start, endP1);
        for (T value : subList) {
            if (value == null) {
                if (throwExceptionIfNull) {
                    throw new RuntimeException("Null found in Collection");
                }
                nulls++;
            }
        }
        int n = endP1 - start - nulls;
        T[] array = (T[]) new Object[n];
        int j = 0;
        if (nulls > 0) {
            for (T value : subList) {
                if (value != null) {
                    array[j] = value;
                    j++;
                }
            }
        } else {
            for (T value : subList) {
                array[j] = value;
                j++;
            }
        }
        sortNNA(array, mapper, 0, n, fieldSortOptions);
        j = 0;
        ListIterator<T> iterator = subList.listIterator();
        if (nulls > 0) {
            if (fieldSortOptions.getNullHandling().equals(NullHandling.NULLS_FIRST)) {
                while (nulls > 0) {
                    iterator.next();
                    iterator.set(null);
                    nulls--;
                }
            }
            while (j < n) {
                iterator.next();
                iterator.set(array[j]);
                j++;
            }
            if (fieldSortOptions.getNullHandling().equals(NullHandling.NULLS_LAST)) {
                while (nulls > 0) {
                    iterator.next();
                    iterator.set(null);
                    nulls--;
                }
            }
        } else {
            while (iterator.hasNext()) {
                iterator.next();
                iterator.set(array[j]);
                j++;
            }
        }
    }

    default void sort(T[] list, IntMapper<T> mapper, int start, int endP1, FieldSortOptions fieldSortOptions) {
        int nulls = 0;
        if (fieldSortOptions.getNullHandling() == NullHandling.NULLS_LAST) {
            int j = start;
            for (int i = start; i < endP1; i++) {
                T value = list[i];
                if (value == null) {
                    nulls++;
                } else {
                    list[j] = value;
                    j++;
                }
            }
            for (; j < endP1; j++) {
                list[j] = null;
            }
            sortNNA(list, mapper, 0, endP1 - nulls, fieldSortOptions);
        } else if (fieldSortOptions.getNullHandling() == NullHandling.NULLS_FIRST) {
            int j = endP1 - 1;
            for (int i = endP1 - 1; i >= start; i--) {
                T value = list[i];
                if (value == null) {
                    nulls++;
                } else {
                    list[j] = value;
                    j--;
                }
            }
            for (; j >= start; j--) {
                list[j] = null;
            }
            sortNNA(list, mapper, start + nulls, endP1, fieldSortOptions);
        } else {
            for (int i = start; i < endP1; i++) {
                T value = list[i];
                if (value == null) {
                    throw new RuntimeException("Null found in Collection");
                }
            }
            sortNNA(list, mapper, 0, endP1, fieldSortOptions);
        }
    }

}
