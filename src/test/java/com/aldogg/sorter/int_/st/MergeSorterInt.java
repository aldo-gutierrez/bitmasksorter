package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.FieldOptions;
import com.aldogg.sorter.int_.SorterInt;
import com.aldogg.sorter.int_.SorterUtilsInt;

import java.util.Arrays;

public class MergeSorterInt implements SorterInt {

    @Override
    public void sort(int[] array, int start, int endP1, FieldOptions options) {
        int n = endP1 - start;
        int[] aux = new int[n];
        mergeSort(array, start, endP1, aux, 0);
    }

    public static void mergeSort(int[] array, int start, int end, int[] aux, int startAux) {
        int n = end - start;
        if (n < 2) {
            return;
        }

        int mid = n / 2;
        int midIndex = start + mid;
        mergeSort(array, start, midIndex, aux, startAux);
        mergeSort(array, midIndex, end, aux, startAux);

        merge(array, start, end, midIndex, aux, startAux);
    }


    public static void merge(int[] array, int start, int endP1, int midIndex, int[] aux, int startAux) {
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
        int i = start, j = midIndex, k = startAux;
        while (i < midIndex && j < endP1) {
            if (array[i] <= array[j]) {
                aux[k++] = array[i++];
            } else {
                aux[k++] = array[j++];
            }
        }
        while (i < midIndex) {
            aux[k++] = array[i++];
        }
        while (j < endP1) {
            aux[k++] = array[j++];
        }
        System.arraycopy(aux, startAux, array, start, endP1 - start);
    }

}
