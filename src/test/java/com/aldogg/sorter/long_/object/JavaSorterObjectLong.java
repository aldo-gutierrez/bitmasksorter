package com.aldogg.sorter.long_.object;

import java.util.Arrays;

public class JavaSorterObjectLong implements SorterObjectLong {
    @Override
    public void sort(Object[] array, int start, int endP1, LongMapper mapper) {
        Arrays.sort(array, start, endP1, (o1, o2) -> Long.compare(mapper.value(o1), mapper.value(o2)));
    }

}