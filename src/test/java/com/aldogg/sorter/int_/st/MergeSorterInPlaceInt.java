package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.FieldOptions;
import com.aldogg.sorter.int_.SorterInt;
import com.aldogg.sorter.int_.SorterUtilsInt;

import java.util.Arrays;

public class MergeSorterInPlaceInt implements SorterInt {

    @Override
    public void sort(int[] array, int start, int endP1, FieldOptions options) {
        mergeSort(array, start, endP1);
    }

    public static void mergeSort(int[] array, int start, int end) {
        int n = end - start;
        if (n < 2) {
            return;
        }
        int mid = n / 2;
        int midIndex = start + mid;
        mergeSort(array, start, midIndex);
        mergeSort(array, midIndex, end);
        merge(array, start, end, midIndex);
    }


    public static void merge(int[] array, int start, int endP1, int midIndex) {
        int N = endP1 - start;
        if (N < 256) {
            Arrays.sort(array, start, endP1);
            return;
        }
        if (array[midIndex - 1] <= array[midIndex]) {
            return;
        }
        if (array[endP1 - 1] <= array[start]) {
            SorterUtilsInt.rotateRight(array, start, endP1, midIndex - start);
            return;
        }
        int start2 = -1;
        while (start < midIndex) {
            if (midIndex == endP1 - 1 && start == endP1 - 2) {
                if (array[start] > array[midIndex]) {
                    SorterUtilsInt.swap(array, start, midIndex);
                    break;
                }
            }
            if (start2 == -1) {
                if (array[start] <= array[midIndex]) {
                    start++;
                } else {
                    SorterUtilsInt.swap(array, start, midIndex);
                    start++;
                    start2 = midIndex;
                    midIndex++;
                    if (start == start2) {
                        start2 = -1;
                    }
                    if (midIndex == endP1) {
                        midIndex--;
                        start2 = -1;
                        continue;
                    }
                }
            }
            if (start2 > -1) {
                if (array[start2] <= array[midIndex]) {
                    int diff = midIndex - start2;
                    if (diff == 1) {
                        SorterUtilsInt.swap(array, start, start2);
                        start++;
                        if (start == start2) {
                            start2 = -1;
                        }
                    } else {
                        SorterUtilsInt.swap(array, start, start2);
                        SorterUtilsInt.rotateLeft(array, start2, midIndex, 1);
                        start++;
                        if (start == start2) {
                            start2 = -1;
                        }
                    }
                } else {
                    SorterUtilsInt.swap(array, start, midIndex);
                    start++;
                    midIndex++;
                    if (start == start2) {
                        start2 = -1;
                    }
                    if (midIndex == endP1) {
                        midIndex--;
                        continue;
                    }
                }
            }
        }

    }

    public static void main(String[] args) {
        int[] array = new int[]{1, 3, 5, 7, 4, 4, 6, 9};
        merge(array, 0, array.length, 4);
        System.out.println(Arrays.toString(array));

        int[] array2 = new int[]{4242, 14821, 35922, 50677, 3365, 4816, 20399, 25232};
        merge(array2, 0, array2.length, 4);
        System.out.println(Arrays.toString(array2));

        int[] array3 = {66678, 78696, 52244, 75787, 70547, 43072, 15885, 31926};
        MergeSorterInPlaceInt sorter = new MergeSorterInPlaceInt();
        sorter.sort(array3);
        System.out.println(Arrays.toString(array3));

        int[] array4 = {63050, 67372, 41096, 68145, 54815, 30193, 37102, 59198};
        MergeSorterInPlaceInt sorter2 = new MergeSorterInPlaceInt();
        sorter2.sort(array4);
        System.out.println(Arrays.toString(array4));


    }

}
