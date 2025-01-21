package com.aldogg.sorter.long_;

import com.aldogg.sorter.FieldSortOptions;
import com.aldogg.sorter.shared.NullHandling;
import com.aldogg.sorter.shared.SorterPrimitive;

import java.util.*;

public interface SorterLong extends SorterPrimitive<Long> {


    void sort(long[] array, int start, int endP1, FieldSortOptions options);

    default void sort(long[] array) {
        sort(array, 0, array.length, getDefaultFieldSortOptions());
    }

    default void sort(long[] array, int start, int endP1) {
        sort(array, start, endP1, getDefaultFieldSortOptions());
    }

    default void sort(List<Long> list, int start, int endP1, FieldSortOptions options) {
        int nulls = 0;
        boolean throwExceptionIfNull = options.getNullHandling().equals(NullHandling.NO_HANDLING);
        List<Long> subList = start == 0 && endP1 == list.size() ? list : list.subList(start, endP1);
        if (!options.getNullHandling().equals(NullHandling.NO_HANDLING)) {
            for (Long value : subList) {
                if (throwExceptionIfNull) {
                    if (value == null) {
                        throw new RuntimeException("Null found in Collection");
                    }
                }
                nulls++;
            }
        }
        int n = endP1 - start - nulls;
        long[] a = new long[n];
        int j = 0;
        if (nulls > 0) {
            for (Long value : subList) {
                if (value != null) {
                    a[j] = value;
                    j++;
                }
            }
        } else {
            for (Long value : subList) {
                a[j] = value;
                j++;
            }
        }
        sort(a, 0, n);
        j = 0;
        ListIterator<Long> iterator = subList.listIterator();
        if (nulls > 0) {
            if (options.getNullHandling().equals(NullHandling.NULLS_FIRST)) {
                while (nulls > 0) {
                    iterator.next();
                    iterator.set(null);
                    nulls--;
                }
            }
            while (j < n) {
                iterator.next();
                iterator.set(a[j]);
                j++;
            }
            if (options.getNullHandling().equals(NullHandling.NULLS_LAST)) {
                while (nulls > 0) {
                    iterator.next();
                    iterator.set(null);
                    nulls--;
                }
            }

        } else {
            while (iterator.hasNext()) {
                iterator.next();
                iterator.set(a[j]);
                j++;
            }
        }
    }

    default void sort(Long[] list, int start, int endP1, FieldSortOptions options) {
        int nulls = 0;
        boolean throwExceptionIfNull = options.getNullHandling().equals(NullHandling.NO_HANDLING);
        for (int i = start; i < endP1; i++) {
            if (list[i] == null) {
                if (throwExceptionIfNull) {
                    throw new RuntimeException("Null found in Collection");
                }
                nulls++;
            }
        }
        int n = endP1 - start - nulls;
        long[] array = new long[n];
        if (nulls > 0) {
            for (int i = start, j = 0; i < endP1; i++) {
                Long e = list[i];
                if (e != null) {
                    array[j] = e;
                    j++;
                }
            }
        } else {
            for (int i = start, j = 0; i < endP1; i++, j++) {
                array[j] = list[i];
            }
        }
        sort(array, 0, n);
        if (nulls > 0) {
            int i = start;
            int j = 0;
            if (options.getNullHandling().equals(NullHandling.NULLS_FIRST)) {
                while (nulls > 0) {
                    list[i] = null;
                    i++;
                    nulls--;
                }
            }
            for (; j < n; i++, j++) {
                list[i] = array[j];
            }
            if (options.getNullHandling().equals(NullHandling.NULLS_LAST)) {
                while (nulls > 0) {
                    list[i] = null;
                    i++;
                    nulls--;
                }
            }
        } else {
            for (int i = start, j = 0; j < n; i++, j++) {
                list[i] = array[j];
            }
        }
    }

}