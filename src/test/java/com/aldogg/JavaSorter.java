package com.aldogg;

import com.aldogg.intType.IntSorter;

import java.util.Arrays;

public class JavaSorter implements IntSorter {
    @Override
    public void sort(int[] list) {
        Arrays.sort(list);
    }
}
