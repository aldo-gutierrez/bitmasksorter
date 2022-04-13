package com.aldogg.sorter.collection;

import java.util.Arrays;

public class JavaSorterObject implements ObjectSorter {
    @Override
    public void sort(Object[] array, IntComparator comparator) {
        Arrays.sort(array, comparator);
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

