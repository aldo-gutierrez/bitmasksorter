package com.aldogg.sorter.int_.mt;

import com.aldogg.sorter.FieldOptions;
import com.aldogg.sorter.int_.SorterInt;
import com.aldogg.sorter.int_.SorterUtilsInt;
import com.aldogg.sorter.shared.NullHandling;

import java.util.*;

import static com.aldogg.sorter.int_.st.JavaSorterInt.binarySearchIndexOfPositive;

public class JavaSorterMTInt implements SorterInt {

    @Override
    public void sort(Integer[] list, int start, int endP1, FieldOptions options) {
        if (options.getNullHandling().equals(NullHandling.NULLS_LAST)) {
            if (options.isUnsigned()) {
                Arrays.parallelSort(list, start, endP1, Comparator.nullsLast(Integer::compareUnsigned));
            } else {
                Arrays.parallelSort(list, start, endP1, Comparator.nullsLast(Comparator.naturalOrder()));
            }
        } else if (options.getNullHandling().equals(NullHandling.NULLS_FIRST)) {
            if (options.isUnsigned()) {
                Arrays.parallelSort(list, start, endP1, Comparator.nullsFirst(Integer::compareUnsigned));
            } else {
                Arrays.parallelSort(list, start, endP1, Comparator.nullsFirst(Comparator.naturalOrder()));
            }
        } else {
            if (options.isUnsigned()) {
                Arrays.parallelSort(list, start, endP1, Integer::compareUnsigned);
            } else {
                Arrays.parallelSort(list, start, endP1);
            }
        }
    }

    @Override
    public void sort(List<Integer> list, int start, int endP1, FieldOptions options) {
        Integer[] a = list.toArray(new Integer[]{});
        sort(a, start, endP1, options);
        ListIterator<Integer> i = list.listIterator();
        for (Integer e : a) {
            i.next();
            i.set(e);
        }
    }

    @Override
    public void sort(int[] array, int start, int endP1, FieldOptions options) {
        if (options.isUnsigned()) {
            Arrays.parallelSort(array, start, endP1);
            int indexPositive = binarySearchIndexOfPositive(array, start, endP1);
            if (indexPositive > 0) {
                SorterUtilsInt.rotateLeft(array, start, endP1, indexPositive);
            }
        } else {
            Arrays.parallelSort(array, start, endP1);
        }
    }

}
