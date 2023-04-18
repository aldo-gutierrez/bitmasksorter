package com.aldogg.sorter.int_.mt;

import com.aldogg.sorter.int_.IntSorter;

import java.util.Arrays;

public class JavaSorterMTInt implements IntSorter {

    @Override
    public void sort(int[] array, int start, int endP1) {
        Arrays.parallelSort(array, start, endP1);
    }

}
