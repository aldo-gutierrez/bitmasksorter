package com.aldogg.sorter.test;


import com.aldogg.sorter.intType.IntSorter;

import java.util.Arrays;

public class JavaSorterInt implements IntSorter {
    @Override
    public void sort(int[] list) {
        Arrays.sort(list);
    }
    @Override
    public void setUnsigned(boolean unsigned) {
        throw new UnsupportedOperationException();
    }
}
