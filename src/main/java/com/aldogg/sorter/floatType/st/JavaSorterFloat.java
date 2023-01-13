package com.aldogg.sorter.floatType.st;

import com.aldogg.sorter.floatType.FloatSorter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaSorterFloat implements FloatSorter {

    @Override
    public void sort(float[] array, int start, int end) {
        Arrays.sort(array, start, end);
    }

    @Override
    public void sort(List<Float> list) {
        Collections.sort(list);
    }
}
