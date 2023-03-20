package com.aldogg.sorter.double_.st;

import com.aldogg.sorter.double_.DoubleSorter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaSorterDouble implements DoubleSorter {

    @Override
    public void sort(double[] array, int start, int end) {
        Arrays.sort(array, start, end);
    }

    @Override
    public void sort(List<Double> list) {
        Collections.sort(list);
    }
}
