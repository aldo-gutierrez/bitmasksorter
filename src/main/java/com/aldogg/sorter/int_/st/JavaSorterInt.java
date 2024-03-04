package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.FieldOptions;
import com.aldogg.sorter.int_.SorterInt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaSorterInt implements SorterInt {

    @Override
    public void sort(int[] array, int start, int endP1, FieldOptions options) {
        Arrays.sort(array, start, endP1);
    }

    @Override
    public void sort(List<Integer> list) {
        Collections.sort(list);
    }
}
