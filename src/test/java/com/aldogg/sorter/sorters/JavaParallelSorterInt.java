package com.aldogg.sorter.sorters;


import com.aldogg.sorter.intType.IntSorter;

import java.util.Arrays;

public class JavaParallelSorterInt implements IntSorter {
    @Override
    public void sort(int[] array) {
        Arrays.parallelSort(array);
    }
    @Override
    public void setUnsigned(boolean unsigned) {
        throw new UnsupportedOperationException();
    }
}
