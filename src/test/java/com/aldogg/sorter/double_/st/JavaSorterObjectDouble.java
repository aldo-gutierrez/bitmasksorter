package com.aldogg.sorter.double_.st;

import com.aldogg.sorter.double_.object.DoubleMapper;
import com.aldogg.sorter.double_.object.SorterObjectDouble;

import java.util.Arrays;

public class JavaSorterObjectDouble implements SorterObjectDouble {
    @Override
    public void sort(Object[] array, int start, int endP1, DoubleMapper mapper) {
        Arrays.sort(array, start, endP1, (o1, o2) -> Double.compare(mapper.value(o1), mapper.value(o2)));
        //Arrays.sort(array, start, endP1);
    }

}
