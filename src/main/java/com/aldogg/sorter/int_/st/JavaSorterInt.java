package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.int_.IntSorter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaSorterInt implements IntSorter {

    @Override
    public void sort(int[] array, int start, int end) {
        Arrays.sort(array, start, end);
    }

    @Override
    public void sort(List<Integer> list) {
        Collections.sort(list);
    }
}
