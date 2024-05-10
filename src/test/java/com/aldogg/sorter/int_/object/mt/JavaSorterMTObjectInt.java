package com.aldogg.sorter.int_.object.mt;

import com.aldogg.sorter.int_.SorterObjectInt;
import com.aldogg.sorter.int_.object.IntMapper;
import com.aldogg.sorter.shared.NullHandling;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

public class JavaSorterMTObjectInt implements SorterObjectInt {

    @Override
    public void sortNNA(Object[] array, int start, int endP1, IntMapper mapper) {
        sort(array, start, endP1, mapper); //COMMENT: this code should never be call and doesn't optimize for already filtered nulls
    }

    @Override
    public void sort(Object[] array, int start, int endP1, IntMapper options) {
        if (options.getNullHandling().equals(NullHandling.NULLS_LAST)) {
            if (options.isUnsigned()) {
                Arrays.parallelSort(array, start, endP1, Comparator.nullsLast((o1, o2) -> Integer.compareUnsigned(options.value(o1), options.value(o2))));
            } else {
                Arrays.parallelSort(array, start, endP1, Comparator.nullsLast(Comparator.comparingInt(o -> options.value(o))));
            }
        } else if (options.getNullHandling().equals(NullHandling.NULLS_FIRST)) {
            if (options.isUnsigned()) {
                Arrays.parallelSort(array, start, endP1, Comparator.nullsFirst((o1, o2) -> Integer.compareUnsigned(options.value(o1), options.value(o2))));
            } else {
                Arrays.parallelSort(array, start, endP1, Comparator.nullsFirst(Comparator.comparingInt(o -> options.value(o))));
            }
        } else {
            if (options.isUnsigned()) {
                Arrays.parallelSort(array, start, endP1, (o1, o2) -> Integer.compareUnsigned(options.value(o1), options.value(o2)));
            } else {
                Arrays.parallelSort(array, start, endP1, Comparator.comparingInt(o -> options.value(o)));
            }
        }
    }

    @Override
    public void sort(List list, int start, int endP1, IntMapper options) {
        Object[] a = list.toArray(new Object[]{});
        sort(a, start, endP1, options);
        ListIterator<Object> i = list.listIterator();
        for (Object e : a) {
            i.next();
            i.set(e);
        }
    }

}
