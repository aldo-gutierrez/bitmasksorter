package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.FieldSortOptions;
import com.aldogg.sorter.int_.SorterInt;
import com.aldogg.sorter.int_.SorterUtilsInt;
import com.aldogg.sorter.shared.NullHandling;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.aldogg.sorter.shared.FieldType.UNSIGNED_INTEGER;

public class JavaSorterInt implements SorterInt {

    @Override
    public void sort(Integer[] list, int start, int endP1, FieldSortOptions options) {
        if (options.getNullHandling().equals(NullHandling.NULLS_LAST)) {
            if (options.getFieldType().equals(UNSIGNED_INTEGER)) {
                Arrays.sort(list, start, endP1, Comparator.nullsLast(Integer::compareUnsigned));
            } else {
                Arrays.sort(list, start, endP1, Comparator.nullsLast(Comparator.naturalOrder()));
            }
        } else if (options.getNullHandling().equals(NullHandling.NULLS_FIRST)) {
            if (options.getFieldType().equals(UNSIGNED_INTEGER)) {
                Arrays.sort(list, start, endP1, Comparator.nullsFirst(Integer::compareUnsigned));
            } else {
                Arrays.sort(list, start, endP1, Comparator.nullsFirst(Comparator.naturalOrder()));
            }
        } else {
            if (options.getFieldType().equals(UNSIGNED_INTEGER)) {
                Arrays.sort(list, start, endP1, Integer::compareUnsigned);
            } else {
                Arrays.sort(list, start, endP1);
            }
        }
    }

    @Override
    public void sort(List<Integer> list, int start, int endP1, FieldSortOptions options) {
        if (options.getNullHandling().equals(NullHandling.NULLS_LAST)) {
            if (options.getFieldType().equals(UNSIGNED_INTEGER)) {
                if (start == 0 && endP1 == list.size()) {
                    list.sort(Comparator.nullsLast(Integer::compareUnsigned));
                } else {
                    list.subList(start, endP1).sort(Comparator.nullsLast(Integer::compareUnsigned));
                }
            } else {
                if (start == 0 && endP1 == list.size()) {
                    list.sort(Comparator.nullsLast(Comparator.naturalOrder()));
                } else {
                    list.subList(start, endP1).sort(Comparator.nullsLast(Comparator.naturalOrder()));
                }
            }
        } else if (options.getNullHandling().equals(NullHandling.NULLS_FIRST)) {
            if (options.getFieldType().equals(UNSIGNED_INTEGER)) {
                if (start == 0 && endP1 == list.size()) {
                    list.sort(Comparator.nullsFirst(Integer::compareUnsigned));
                } else {
                    list.subList(start, endP1).sort(Comparator.nullsFirst(Integer::compareUnsigned));
                }
            } else {
                if (start == 0 && endP1 == list.size()) {
                    list.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
                } else {
                    list.subList(start, endP1).sort(Comparator.nullsFirst(Comparator.naturalOrder()));
                }
            }
        } else {
            if (options.getFieldType().equals(UNSIGNED_INTEGER)) {
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
    }

    @Override
    public void sort(int[] array, int start, int endP1, FieldSortOptions options) {
        if (options.getFieldType().equals(UNSIGNED_INTEGER)) {
            Arrays.sort(array, start, endP1);
            int indexPositive = binarySearchIndexOfPositive(array, start, endP1);
            if (indexPositive > 0) {
                SorterUtilsInt.rotateLeft(array, start, endP1, indexPositive);
            }
        } else {
            Arrays.sort(array, start, endP1);
        }
    }

    public static int binarySearchIndexOfPositive(int[] a, int fromIndex, int toIndex) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = a[mid];
            if (midVal < 0) {
                low = mid + 1;
            } else if (midVal > 0) {
                high = mid - 1;
            } else {
                while (mid - 1 >= fromIndex && a[mid - 1] >= 0) {
                    mid--;
                }
                return mid; // key found
            }
        }
        if (low < toIndex) {
            return low;
        } else {
            return -1;
        }
    }

    public static void main(String[] args) {
        int[] a = {-5, -4, -3, -2, -1, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int res = binarySearchIndexOfPositive(a, 0, a.length);
        System.out.println("res = " + res);

        int[] b = {-10, -9, -8, -7, -6, -5, -4, -3, -2, -1, -1, -1};
        int res2 = binarySearchIndexOfPositive(b, 0, b.length);
        System.out.println("res = " + res2);

        int[] c = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        int res3 = binarySearchIndexOfPositive(c, 0, c.length);
        System.out.println("res = " + res3);
    }

}
