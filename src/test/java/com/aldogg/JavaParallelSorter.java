package com.aldogg;

import com.aldogg.intType.IntSorter;

import java.util.Arrays;

public class JavaParallelSorter implements IntSorter {
    @Override
    public void sort(int[] list) {
        Arrays.parallelSort(list);
    }
}
