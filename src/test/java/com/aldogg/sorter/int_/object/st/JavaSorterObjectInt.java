package com.aldogg.sorter.int_.object.st;

import com.aldogg.sorter.generic.SorterObjectInt;
import com.aldogg.sorter.int_.object.IntMapper;

import java.util.Arrays;

public class JavaSorterObjectInt implements SorterObjectInt {
    @Override
    public void sortNNA(Object[] array, int start, int endP1, IntMapper mapper) {
        Arrays.sort(array, start, endP1, (o1, o2) -> Integer.compare(mapper.value(o1), mapper.value(o2)));
    }

}

