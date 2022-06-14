package com.aldogg.sorter.intType;

import java.util.Arrays;

public class JavaParallelSorterInt implements IntSorter {

    @Override
    public void sort(int[] array, int start, int end) {
        Arrays.parallelSort(array, start, end);
    }

}
