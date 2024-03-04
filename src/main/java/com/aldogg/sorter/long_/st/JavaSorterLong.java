package com.aldogg.sorter.long_.st;

import com.aldogg.sorter.FieldOptions;
import com.aldogg.sorter.long_.SorterLong;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaSorterLong implements SorterLong {

    @Override
    public void sort(long[] array, int start, int endP1, FieldOptions options) {
        Arrays.sort(array, start, endP1);
    }

    @Override
    public void sort(List<Long> list) {
        Collections.sort(list);
    }
}
