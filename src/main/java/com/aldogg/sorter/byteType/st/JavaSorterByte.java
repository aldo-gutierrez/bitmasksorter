package com.aldogg.sorter.byteType.st;

import com.aldogg.sorter.byteType.ByteSorter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaSorterByte implements ByteSorter {

    @Override
    public void sort(byte[] array, int start, int end) {
        Arrays.sort(array, start, end);
    }

    @Override
    public void sort(List<Byte> list) {
        Collections.sort(list);
    }
}

