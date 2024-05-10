package com.aldogg.sorter.int_.object.st;

import com.aldogg.sorter.int_.SorterObjectInt;
import com.aldogg.sorter.int_.object.IntMapper;
import com.aldogg.sorter.shared.NullHandling;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class JavaSorterObjectInt implements SorterObjectInt {
    @Override
    public void sortNNA(Object[] array, int start, int endP1, IntMapper mapper) {
        sort(array, start, endP1, mapper); //COMMENT: this code should never be call and doesn't optimize for already filtered nulls
    }

    @Override
    public void sort(Object[] array, int start, int endP1, IntMapper options) {
        if (options.getNullHandling().equals(NullHandling.NULLS_LAST)) {
            if (options.isUnsigned()) {
                Arrays.sort(array, start, endP1, Comparator.nullsLast((o1, o2) -> Integer.compareUnsigned(options.value(o1), options.value(o2))));
            } else {
                Arrays.sort(array, start, endP1, Comparator.nullsLast(Comparator.comparingInt(o -> options.value(o))));
            }
        } else if (options.getNullHandling().equals(NullHandling.NULLS_FIRST)) {
            if (options.isUnsigned()) {
                Arrays.sort(array, start, endP1, Comparator.nullsFirst((o1, o2) -> Integer.compareUnsigned(options.value(o1), options.value(o2))));
            } else {
                Arrays.sort(array, start, endP1, Comparator.nullsFirst(Comparator.comparingInt(o -> options.value(o))));
            }
        } else {
            if (options.isUnsigned()) {
                Arrays.sort(array, start, endP1, (o1, o2) -> Integer.compareUnsigned(options.value(o1), options.value(o2)));
            } else {
                Arrays.sort(array, start, endP1, Comparator.comparingInt(o -> options.value(o)));
            }
        }
    }

    @Override
    public void sort(List list, int start, int endP1, IntMapper options) {
        if (options.getNullHandling().equals(NullHandling.NULLS_LAST)) {
            if (options.isUnsigned()) {
                if (start == 0 && endP1 == list.size()) {
                    list.sort(Comparator.nullsLast((o1, o2) -> Integer.compareUnsigned(options.value(o1), options.value(o2))));
                } else {
                    list.subList(start, endP1).sort(Comparator.nullsLast((o1, o2) -> Integer.compareUnsigned(options.value(o1), options.value(o2))));
                }
            } else {
                if (start == 0 && endP1 == list.size()) {
                    list.sort(Comparator.nullsLast(Comparator.comparingInt(o -> options.value(o))));
                } else {
                    list.subList(start, endP1).sort(Comparator.nullsLast(Comparator.comparingInt(o -> options.value(o))));
                }
            }
        } else if (options.getNullHandling().equals(NullHandling.NULLS_FIRST)) {
            if (options.isUnsigned()) {
                if (start == 0 && endP1 == list.size()) {
                    list.sort(Comparator.nullsFirst((o1, o2) -> Integer.compareUnsigned(options.value(o1), options.value(o2))));
                } else {
                    list.subList(start, endP1).sort(Comparator.nullsFirst((o1, o2) -> Integer.compareUnsigned(options.value(o1), options.value(o2))));
                }
            } else {
                if (start == 0 && endP1 == list.size()) {
                    list.sort(Comparator.nullsFirst(Comparator.comparingInt(o -> options.value(o))));
                } else {
                    list.subList(start, endP1).sort(Comparator.nullsFirst(Comparator.comparingInt(o -> options.value(o))));
                }
            }
        } else {
            if (options.isUnsigned()) {
                if (start == 0 && endP1 == list.size()) {
                    list.sort((o1, o2) -> Integer.compareUnsigned(options.value(o1), options.value(o2)));
                } else {
                    list.subList(start, endP1).sort((o1, o2) -> Integer.compareUnsigned(options.value(o1), options.value(o2)));
                }
            } else {
                if (start == 0 && endP1 == list.size()) {
                    list.sort(Comparator.comparingInt(o -> options.value(o)));
                } else {
                    list.subList(start, endP1).sort(Comparator.comparingInt(o -> options.value(o)));
                }
            }
        }
    }
}

