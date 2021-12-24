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

    public static void sortList2to5Elements(final int[] list, final int start, final int end) {
        int listLength = end - start;
        if (listLength == 2) {
            compareAndSwap(list, start, end - 1);
        } else if (listLength == 3) {
            compareAndSwap(list, start, end - 1);
            compareAndSwap(list, start, end - 2);
            compareAndSwap(list, end - 2, end - 1);
        } else if (listLength == 4) {
            compareAndSwap(list, start, start + 1);
            compareAndSwap(list, end - 2, end - 1);
            compareAndSwap(list, start, end - 2);
            compareAndSwap(list, start + 1, end - 1);
            compareAndSwap(list, start + 1, end - 2);
        } else if (listLength == 5) {
            compareAndSwap(list, start + 1, start + 2);  //1 < 2
            compareAndSwap(list, start + 3, start + 4);  //3 < 4
            if (compareAndSwap(list, start, start + 1)) { //0 < 1?
                compareAndSwap(list, start + 1, start + 2); // 0 < 1 < 2
            }
            compareAndSwap(list, start, start + 3); // 0 < 3, 0 menor que todos
            compareAndSwap(list, start + 2, start + 4); // 2 < 4, 4 mayor que todos

            compareAndSwap(list, start + 1, start + 2); // 1 < 2
            compareAndSwap(list, start + 1, start + 3); // 1 < 3
            compareAndSwap(list, start + 2, start + 3); // 0 <1 < 2 < 3 <4
        }
        return;
    }

}
