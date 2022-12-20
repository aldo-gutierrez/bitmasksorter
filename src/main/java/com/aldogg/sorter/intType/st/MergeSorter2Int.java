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
        mergeSort(array, start, end, aux, 0);
    }

    public static void mergeSort(int[] array, int start, int end, int[] aux, int startAux) {
        int n = end - start;
        if (n < 2) {
            return;
        }
        if (n <= VERY_SMALL_N_SIZE) {
            SortingNetworks.signedSNFunctions[n].accept(array, start);
            return;
        }

        int mid = n / 2;
        int midIndex = start + mid;
        mergeSort(array, start, midIndex, aux, startAux);
        mergeSort(array, midIndex, end, aux, startAux);

        merge(array, start, end, midIndex, aux, startAux);
    }


    public static void merge(int[] array, int start, int end, int midIndex, int[] aux, int startAux) {
        int i = start, j = midIndex, k = startAux;
        while (i < midIndex && j < end) {
            if (array[i] <= array[j]) {
                aux[k++] = array[i++];
            } else {
                aux[k++] = array[j++];
            }
        }
        while (i < midIndex) {
            aux[k++] = array[i++];
        }
        while (j < end) {
            aux[k++] = array[j++];
        }
        if (end - start < 0) {
            System.out.println("ASDFASDF");
        }
        System.arraycopy(aux, startAux, array, start, end - start);
    }

}
