package com.aldogg.sorter.double_.st;

import com.aldogg.sorter.double_.SorterDouble;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaSorterDouble implements SorterDouble {

    @Override
    public void sort(double[] array, int start, int endP1) {
        Arrays.sort(array, start, endP1);
    }

    @Override
    public void sort(List<Double> list) {
        Collections.sort(list);
    }
}
