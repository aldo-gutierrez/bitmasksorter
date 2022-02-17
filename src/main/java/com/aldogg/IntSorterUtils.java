package com.aldogg;

public class IntSorterUtils {

    public static boolean compareAndSwap(final int[] list, final int left, final int right) {
        if ((list[left] > list[right])) {
            swap(list, left, right);
            return true;
        }
        return false;
    }

    public static void swap(final int[] list, final int left, final int right) {
        int aux = list[left];
        list[left] = list[right];
        list[right] = aux;
    }

}
