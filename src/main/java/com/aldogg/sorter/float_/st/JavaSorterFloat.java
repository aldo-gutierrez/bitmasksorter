package com.aldogg.sorter.float_.st;

import com.aldogg.sorter.float_.FloatSorter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaSorterFloat implements FloatSorter {

    @Override
    public void sort(float[] array, int start, int endP1) {
        Arrays.sort(array, start, endP1);
    }

    @Override
    public void sort(List<Float> list) {
        Collections.sort(list);
    }
}
