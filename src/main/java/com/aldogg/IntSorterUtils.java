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

    /**
     *   partition with 0 memory in-place
     */
    public static int partition(final int[] list, final  int start, final int end, final int sortMask) {
        int left = start;
        int right = end - 1;

        for (; left <= right; ) {
            int element = list[left];
            if ((element & sortMask) == 0) {
                left++;
            } else {
                for (; left <= right; ) {
                    element = list[right];
                    if ((element & sortMask) == 0) {
                        IntSorterUtils.swap(list, left, right);
                        left++;
                        right--;
                        break;
                    } else {
                        right--;
                    }
                }
            }
        }
        return left;
    }

    /**
     *   partition with 0 memory in-place reverse order
     */
    public static int partitionReverse(final int[] list, final int start, final int end, final int sortMask) {
        int left = start;
        int right = end - 1;

        for (; left <= right; ) {
            int element = list[left];
            if ((element & sortMask) == 0) {
                for (; left <= right; ) {
                    element = list[right];
                    if (((element & sortMask) == 0)) {
                        right--;
                    } else {
                        IntSorterUtils.swap(list, left, right);
                        left++;
                        right--;
                        break;
                    }
                }
            } else {
                left++;
            }
        }
        return left;
    }


    /**
     *  stable partition with aux memory, only copies right to aux for better performance
     *  CPU: 2*N*K (K=1 for 1 bit)
     *  MEM: N
     */
    public static int partitionStable(final int[] list, final int start, final int end, final int sortMask, int[] aux) {
        int left = start;
        int right = 0;
        for (int i = start; i < end; i++) {
            int element = list[i];
            if ((element & sortMask) == 0) {
                list[left] = element;
                left++;
            } else {
                aux[right] = element;
                right++;
            }
        }
        System.arraycopy(aux, 0, list, left, right);
        return left;
    }

}
