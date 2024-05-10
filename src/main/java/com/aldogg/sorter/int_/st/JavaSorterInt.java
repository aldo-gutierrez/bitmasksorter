package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.FieldOptions;
import com.aldogg.sorter.int_.SorterInt;
import com.aldogg.sorter.int_.SorterUtilsInt;

import java.util.Arrays;
import java.util.List;

public class JavaSorterInt implements SorterInt {

    @Override
    public void sort(Integer[] list, int start, int endP1, FieldOptions options) {
        if (options.isUnsigned()) {
            Arrays.sort(list, start, endP1, Integer::compareUnsigned);
        } else {
            Arrays.sort(list, start, endP1);
        }
    }

    @Override
    public void sort(List<Integer> list, int start, int endP1, FieldOptions options) {
        if (options.isUnsigned()) {
            if (start == 0 && endP1 == list.size()) {
                list.sort(Integer::compareUnsigned);
            } else {
                list.subList(start, endP1).sort(Integer::compareUnsigned);
            }
        } else {
            if (start == 0 && endP1 == list.size()) {
                list.sort(null);
            } else {
                list.subList(start, endP1).sort(null);
            }
        }
    }

    @Override
    public void sort(int[] array, int start, int endP1, FieldOptions options) {
        if (options.isUnsigned()) {
            Arrays.sort(array, start, endP1);
            int indexPositive = binarySearchPositive(array, start, endP1);
            if (indexPositive > 0) {
                SorterUtilsInt.rotateLeft(array, start, endP1, indexPositive);
            }
        } else {
            Arrays.sort(array, start, endP1);
        }
    }

    /**
     * -5 -4 -3 -2 -1 1 2 3 4 5 6 7 8 9 0
     * -5 -4 -3 -2 -1 1
     * -2 -1 1
     * 1
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */

    private static int binarySearchPositive(int[] a, int fromIndex, int toIndex) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;

            int midVal = a[mid];

            if (midVal < 0)
                low = mid + 1;
            else if (midVal > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        if (low < toIndex) {
            return low;
        } else  {
            return  -1;
        }
    }

    public static void main(String[] args) {
        int[] a = {-5, -4, -3, -2, -1, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int res = binarySearchPositive(a, 0, a.length);
        System.out.println("res = " + res );

        int[] b = {-10, -9, -8, -7, -6, -5, -4, -3, -2, -1, -1, -1};
        int res2 = binarySearchPositive(b, 0, b.length);
        System.out.println("res = " + res2);

        int[] c = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        int res3 = binarySearchPositive(c, 0, c.length);
        System.out.println("res = " + res3);

    }

}
