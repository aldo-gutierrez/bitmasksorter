package com.aldogg.sorter.int_.object.mt;

import com.aldogg.sorter.generic.SorterObjectInt;
import com.aldogg.sorter.int_.object.IntMapper;

import java.util.Arrays;

public class JavaSorterMTObjectInt implements SorterObjectInt {

    @Override
    public void sort(Object[] array, IntMapper mapper, int start, int endP1) {
        Arrays.parallelSort(array, start, endP1, (o1, o2) -> Integer.compare(mapper.value(o1), mapper.value(o2)));
    }


}
