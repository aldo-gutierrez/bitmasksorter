package com.aldogg.sorter.intType;

import java.util.Arrays;

public class JavaSorterInt implements IntSorter {
    @Override
    public void sort(int[] array) {
        Arrays.sort(array);
    }
    @Override
    public void setUnsigned(boolean unsigned) {
        throw new UnsupportedOperationException();
    }
}
