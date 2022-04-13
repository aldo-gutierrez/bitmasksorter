package com.aldogg.sorter.collection;

import java.util.Arrays;

public class JavaSorterObject implements ObjectSorter {
    @Override
    public void sort(Object[] list, IntComparator comparator) {
        Arrays.sort(list, comparator);
    }

    @Override
    public void setUnsigned(boolean unsigned) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStable(boolean stable) {
        throw new UnsupportedOperationException();
    }
}

