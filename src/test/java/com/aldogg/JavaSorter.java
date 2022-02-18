package com.aldogg;

import java.util.Arrays;

public class JavaSorter implements IntSorter {
    @Override
    public void sort(int[] list) {
        Arrays.sort(list);
    }
}
