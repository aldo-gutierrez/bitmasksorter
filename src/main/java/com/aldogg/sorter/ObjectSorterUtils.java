package com.aldogg.sorter;

public class ObjectSorterUtils {

    public static void swap(final Object[] array, final int left, final int right) {
        Object aux = array[left];
        array[left] = array[right];
        array[right] = aux;
    }

    public static void reverse(final Object[] oArray, final int start, final int end) {
        int length = end - start;
        int end2 = start + length / 2;
        for (int i = start; i < end2; i++) {
            swap(oArray, i, end - i - 1);
        }
    }

}
