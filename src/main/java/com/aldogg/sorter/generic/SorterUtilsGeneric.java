package com.aldogg.sorter.generic;

public class SorterUtilsGeneric {

    public static void swap(final Object[] array, final int left, final int right) {
        Object auxS = array[left];
        array[left] = array[right];
        array[right] = auxS;
    }

    public static void reverse(final Object[] array, final int start, final int endP1) {
        int length = endP1 - start;
        int ld2 = length / 2;
        int end = endP1 - 1;
        for (int i = 0; i < ld2; ++i) {
            swap(array, start + i, end - i);
        }
    }

}
