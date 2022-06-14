package com.aldogg.sorter.intType;

import java.util.Arrays;

public class JavaSorterInt implements IntSorter {

    @Override
    public void sort(int[] array, int start, int end) {
        Arrays.sort(array, start, end);
    }
}
