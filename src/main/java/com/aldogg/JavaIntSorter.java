package com.aldogg;

import java.util.Arrays;

public class JavaIntSorter implements IntSorter {
    @Override
    public void sort(int[] list) {
        Arrays.sort(list);
    }
}
