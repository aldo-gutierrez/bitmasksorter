package com.aldogg.sorter.int_;

import com.aldogg.sorter.FieldSortOptions;
import com.aldogg.sorter.shared.SorterPrimitive;

import java.util.*;

import static com.aldogg.sorter.shared.NullHandling.*;

public interface SorterInt extends SorterPrimitive<Integer> {

    void sort(int[] array, int start, int endP1, FieldSortOptions options);

    default void sort(int[] array) {
        sort(array, 0, array.length, getDefaultFieldSortOptions());
    }

    default void sort(int[] array, int start, int endP1) {
        sort(array, start, endP1, getDefaultFieldSortOptions());
    }

    default void sort(List<Integer> list, int start, int endP1, FieldSortOptions options) {
        int nulls = 0;
        List<Integer> subList = start == 0 && endP1 == list.size() ? list : list.subList(start, endP1);
        for (Integer value : subList) {
            if (value == null) {
                nulls++;
            }
        }
        if (UNKNOWN.equals(options.getNullHandling()) && nulls > 0) {
            throw new NullPointerException("Null found in Collection please specify option NullHandling.NULLS_FIRST or NullHandling.NULLS_LAST");
        }
        int n = endP1 - start - nulls;
        int[] a = new int[n];
        int j = 0;
        if (nulls > 0) {
            for (Integer value : subList) {
                if (value != null) {
                    a[j] = value;
                    j++;
                }
            }
        } else {
            for (Integer value : subList) {
                a[j] = value;
                j++;
            }
        }
        sort(a, 0, n, options);
        j = 0;
        ListIterator<Integer> iterator = subList.listIterator();
        if (nulls > 0) {
            if (NULLS_FIRST.equals(options.getNullHandling())) {
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
            if (NULLS_LAST.equals(options.getNullHandling())) {
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

    default void sort(Integer[] list, int start, int endP1, FieldSortOptions options) {
        int nulls = 0;
        for (int i = start; i < endP1; i++) {
            if (list[i] == null) {
                nulls++;
            }
        }
        if (UNKNOWN.equals(options.getNullHandling()) && nulls > 0) {
            throw new NullPointerException("Null found in Collection please specify option NullHandling.NULLS_FIRST or NullHandling.NULLS_LAST");
        }
        int n = endP1 - start - nulls;
        int[] array = new int[n];
        if (nulls > 0) {
            for (int i = start, j = 0; i < endP1; i++) {
                Integer e = list[i];
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
        sort(array, 0, n, options);
        if (nulls > 0) {
            int i = start;
            int j = 0;
            if (NULLS_FIRST.equals(options.getNullHandling())) {
                while (nulls > 0) {
                    list[i] = null;
                    i++;
                    nulls--;
                }
            }
            for (; j < n; i++, j++) {
                list[i] = array[j];
            }
            if (NULLS_LAST.equals(options.getNullHandling())) {
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
