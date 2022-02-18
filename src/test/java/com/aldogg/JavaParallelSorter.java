package com.aldogg;

import java.util.Arrays;

public class JavaParallelSorter implements IntSorter {
    @Override
    public void sort(int[] list) {
        Arrays.parallelSort(list);
    }
}
