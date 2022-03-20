package com.aldogg;

import com.aldogg.intType.IntSorter;

import java.util.Arrays;

public class JavaParallelSorterInt implements IntSorter {
    @Override
    public void sort(int[] list) {
        Arrays.parallelSort(list);
    }
    @Override
    public void setUnsigned(boolean unsigned) {
        throw new UnsupportedOperationException();
    }
}
