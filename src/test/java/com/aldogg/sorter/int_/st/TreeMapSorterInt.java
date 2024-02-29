package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.FieldSorterOptions;
import com.aldogg.sorter.int_.SorterInt;

import java.util.Map;
import java.util.TreeMap;

public class TreeMapSorterInt implements SorterInt {
    @Override
    public void sort(int[] array, int start, int endP1, FieldSorterOptions options) {


        TreeMap<Integer, Integer> map = new TreeMap<>();
        for (int i = 0; i < array.length; i++) {
            int e = array[i];
            map.merge(e, 1, Integer::sum);
        }
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            for (int j = 0; j < value; j++) {
                array[i] = key;
                i++;
            }
        }
    }
}
