package com.aldogg.sorter.int_.object.st;

import com.aldogg.sorter.FieldSortOptions;
import com.aldogg.sorter.int_.SorterObjectInt;
import com.aldogg.sorter.int_.object.IntMapper;
import com.aldogg.sorter.shared.NullHandling;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.aldogg.sorter.shared.FieldType.UNSIGNED_INTEGER;

public class JavaSorterObjectInt implements SorterObjectInt {
    @Override
    public void sortNNA(Object[] array, IntMapper mapper, int start, int endP1, FieldSortOptions fieldSortOptions) {
        sort(array, mapper, start, endP1, fieldSortOptions); //COMMENT: this code should never be call and doesn't optimize for already filtered nulls
    }

    @Override
    public void sort(Object[] array, IntMapper options, int start, int endP1, FieldSortOptions fieldSortOptions) {
        if (fieldSortOptions.getNullHandling().equals(NullHandling.NULLS_LAST)) {
            if (fieldSortOptions.getFieldType().equals(UNSIGNED_INTEGER)) {
                Arrays.sort(array, start, endP1, Comparator.nullsLast((o1, o2) -> Integer.compareUnsigned(options.value(o1), options.value(o2))));
            } else {
                Arrays.sort(array, start, endP1, Comparator.nullsLast(Comparator.comparingInt(o -> options.value(o))));
            }
        } else if (fieldSortOptions.getNullHandling().equals(NullHandling.NULLS_FIRST)) {
            if (fieldSortOptions.getFieldType().equals(UNSIGNED_INTEGER)) {
                Arrays.sort(array, start, endP1, Comparator.nullsFirst((o1, o2) -> Integer.compareUnsigned(options.value(o1), options.value(o2))));
            } else {
                Arrays.sort(array, start, endP1, Comparator.nullsFirst(Comparator.comparingInt(o -> options.value(o))));
            }
        } else {
            if (fieldSortOptions.getFieldType().equals(UNSIGNED_INTEGER)) {
                Arrays.sort(array, start, endP1, (o1, o2) -> Integer.compareUnsigned(options.value(o1), options.value(o2)));
            } else {
                Arrays.sort(array, start, endP1, Comparator.comparingInt(o -> options.value(o)));
            }
        }
    }

    @Override
    public void sort(List list, IntMapper options, int start, int endP1, FieldSortOptions fieldSortOptions) {
        if (fieldSortOptions.getNullHandling().equals(NullHandling.NULLS_LAST)) {
            if (fieldSortOptions.getFieldType().equals(UNSIGNED_INTEGER)) {
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
        } else if (fieldSortOptions.getNullHandling().equals(NullHandling.NULLS_FIRST)) {
            if (fieldSortOptions.getFieldType().equals(UNSIGNED_INTEGER)) {
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
            if (fieldSortOptions.getFieldType().equals(UNSIGNED_INTEGER)) {
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

