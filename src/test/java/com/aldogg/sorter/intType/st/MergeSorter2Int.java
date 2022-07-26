package com.aldogg.sorter.intType.st;

import com.aldogg.sorter.SortingNetworks;
import com.aldogg.sorter.intType.IntSorter;

import static com.aldogg.sorter.BitSorterParams.VERY_SMALL_N_SIZE;

/*
  MergeSort implementation
  improvements:
  - Small list with Sorting Networks
  - use of aux array created once
 */
public class MergeSorter2Int implements IntSorter {

    @Override
    public void sort(int[] array, int start, int end) {
        int n = end - start;
        int[] aux = new int[n];
        mergeSort(array, aux, start, end);
    }

    public static void mergeSort(int[] a, int[] aux, int start, int end) {
        int n = end - start;
        if (n < 2) {
            return;
        }
        if (n <= VERY_SMALL_N_SIZE) {
            int length = end - start;
            SortingNetworks.signedSNFunctions[length].accept(a, start);
            return;
        }

        int mid = n / 2;
        int midIndex = start + mid;
        mergeSort(a, aux, start, midIndex);
        mergeSort(a, aux, midIndex, end);

        merge(a, aux, start, end, midIndex);
    }


    public static void merge(int[] a, int[] aux, int start, int end, int midIndex) {
        int i = start, j = midIndex, k = start;
        while (i < midIndex && j < end) {
            if (a[i] <= a[j]) {
                aux[k++] = a[i++];
            } else {
                aux[k++] = a[j++];
            }
        }
        while (i < midIndex) {
            aux[k++] = a[i++];
        }
        while (j < end) {
            aux[k++] = a[j++];
        }
        System.arraycopy(aux, start, a, start, end - start);
    }

}
