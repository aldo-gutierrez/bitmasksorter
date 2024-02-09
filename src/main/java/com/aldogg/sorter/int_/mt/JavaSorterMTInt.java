package com.aldogg.sorter.int_.mt;

import com.aldogg.sorter.FieldSorterOptions;
import com.aldogg.sorter.int_.SorterInt;

import java.util.Arrays;

public class JavaSorterMTInt implements SorterInt {

    @Override
    public void sort(int[] array, int start, int endP1, FieldSorterOptions options) {
        Arrays.parallelSort(array, start, endP1);
    }

}
