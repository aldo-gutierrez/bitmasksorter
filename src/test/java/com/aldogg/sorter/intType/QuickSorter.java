package com.aldogg.sorter.intType;

public class QuickSorter implements IntSorter {
    boolean unsigned = false;

    @Override
    public boolean isUnsigned() {
        return unsigned;
    }

    @Override
    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }
    @Override
    public void sort(int[] list) {
        quickSort(list, 0, list.length - 1);
    }

    private void quickSort(int arr[], int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex - 1);
            quickSort(arr, partitionIndex + 1, end);
        }
    }

    private int partition(int arr[], int begin, int end) {
        int pivot = arr[end];
        int i = (begin - 1);

        for (int j = begin; j < end; j++) {
            if (arr[j] <= pivot) {
                i++;

                int swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        int swapTemp = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = swapTemp;

        return i + 1;
    }

}
