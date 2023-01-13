package com.aldogg.sorter.doubleType.st;

import com.aldogg.sorter.doubleType.DoubleSorter;

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
