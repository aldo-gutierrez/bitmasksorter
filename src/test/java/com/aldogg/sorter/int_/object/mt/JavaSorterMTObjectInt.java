package com.aldogg.sorter.int_.object.mt;

import com.aldogg.sorter.generic.SorterGenericInt;
import com.aldogg.sorter.int_.object.IntMapper;

import java.util.Arrays;

public class JavaSorterMTObjectInt implements SorterGenericInt {

    @Override
    public void sort(Object[] array, int start, int endP1, IntMapper mapper) {
        Arrays.parallelSort(array, start, endP1, (o1, o2) -> Integer.compare(mapper.value(o1), mapper.value(o2)));
    }


}
