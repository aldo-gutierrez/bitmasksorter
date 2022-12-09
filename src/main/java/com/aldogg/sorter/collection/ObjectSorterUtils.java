package com.aldogg.sorter.collection;

import com.aldogg.sorter.Section;
import com.aldogg.sorter.intType.IntSorterUtils;

public class ObjectSorterUtils {

    public static void swap(final Object[] array, final int left, final int right) {
        Object aux = array[left];
        array[left] = array[right];
        array[right] = aux;
    }

    /**
     *   partition with 0 memory in-place
     *   CPU: N
     *   MEM: 1
     *   not stable?
     */
    public static int partitionNotStable(final Object[] oArray, final int[] array, final  int start, final int end, final int mask) {
        int left = start;
        int right = end - 1;

        while (left <= right) {
            int element = array[left];
            if ((element & mask) == 0) {
                left++;
            } else {
                while (left <= right) {
                    element = array[right];
                    if ((element & mask) == 0) {
                        IntSorterUtils.swap(array, left, right);
                        ObjectSorterUtils.swap(oArray, left,right);
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
     *   CPU: N
     *   MEM: 1
     *   not stable?
     */
    public static int partitionReverseNotStable(final Object[] oArray, final int[] array, final int start, final int end, final int mask) {
        int left = start;
        int right = end - 1;

        while (left <= right) {
            int element = array[left];
            if ((element & mask) == 0) {
                while (left <= right) {
                    element = array[right];
                    if (((element & mask) == 0)) {
                        right--;
                    } else {
                        IntSorterUtils.swap(array, left, right);
                        ObjectSorterUtils.swap(oArray, left,right);
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

    public static int partitionStable(final Object[] oArray, final int[] array, final int start, final int end, final int mask) {
        int[] aux = new int[end - start];
        Object[] oAux = new Object[end - start];
        return partitionStable(oArray, array, start, end, mask, oAux, aux);
    }

    /**
     *  stable partition with aux memory, only copies right to aux for better performance
     *  CPU: 2*N*K (K=1 for 1 bit) //review
     *  MEM: N //review
     */
    public static int partitionStable(final Object[] oArray, final int[] array, final int start, final int end, final int mask,
                                      final Object[] oAux, final int[] aux) {
        int left = start;
        int right = start;
        for (int i = start; i < end; i++) {
            int element = array[i];
            Object oElement = oArray[i];
            if ((element & mask) == 0) {
                array[left] = element;
                oArray[left] = oElement;
                left++;
            } else {
                aux[right] = element;
                oAux[right] = oElement;
                right++;
            }
        }
        int lengthRight = right - start;
        System.arraycopy(aux, start, array, left, lengthRight);
        System.arraycopy(oAux, start, oArray, left, lengthRight) ;
        return left;
    }

    public static int partitionReverseStable(final Object[] oArray, final int[] array, final int start, final int end, final int mask) {
        int[] aux = new int[end - start];
        Object[] oAux = new Object[end - start];
        return partitionReverseStable(oArray, array, start, end, mask, oAux, aux);
    }


    public static int partitionReverseStable(final Object[] oArray, final int[] array, final int start, final int end,
                                             final int mask, final Object[] oAux, final int[] aux) {
        int left = start;
        int right = start;
        for (int i = start; i < end; i++) {
            int element = array[i];
            Object oElement = oArray[i];
            if (!((element & mask) == 0)) {
                array[left] = element;
                oArray[left] = oElement;
                left++;
            } else {
                aux[right] = element;
                oAux[right] = oElement;
                right++;
            }
        }
        int lengthRight = right - start;
        System.arraycopy(aux, start, array, left, lengthRight);
        System.arraycopy(oAux, start, oArray, left, lengthRight) ;
        return left;
    }

    /**
     *  CPU: 3*N + 2^K
     *  MEM: N + 2*2^K
     */
    public static void partitionStableLastBits(final Object[] oArray, final int[] array, final int start, final Section section, int [] leftX,
                                               final Object[] oAux, final int[] aux, int startAux, final int n) {
        int mask = section.sortMask;
        int end = start + n;
        int[] count = new int[1 << section.length];
        for (int i = start; i < end; i++) {
            count[array[i] & mask]++;
        }
        for (int i = 1; i < count.length; i++) {
            int im1 = i - 1;
            leftX[i] = leftX[im1] + count[im1];
        }
        for (int i = start; i < end; i++) {
            int element = array[i];
            int elementShiftMasked = element & mask;
            int auxIndex = leftX[elementShiftMasked] + startAux;
            aux[auxIndex] = element;
            oAux[auxIndex] = oArray[i];
            leftX[elementShiftMasked]++;
        }
        System.arraycopy(aux, startAux, array, start, end - start);
        System.arraycopy(oAux, startAux, oArray, start, end - start);
    }

    /**
     *  CPU: 3*N + 2^K
     *  MEM: N + 2*2^K
     */
    public static void partitionStableGroupBits(final Object[] oArray, final int[] array, final int start, final Section section, int[] leftX,
                                                final Object[] oAux, final int[] aux, int startAux, int n) {
        int mask = section.sortMask;
        int shiftRight = section.shiftRight;
        int end = start + n;
        int[] count = new int[1 << section.length];
        for (int i = start; i < end; i++) {
            count[(array[i] & mask) >>> shiftRight]++;
        }
        for (int i = 1; i < count.length; i++) {
            int im1 = i - 1;
            leftX[i] = leftX[im1] + count[im1];
        }
        for (int i = start; i < end; i++) {
            int element = array[i];
            int elementShiftMasked = (element & mask) >>> shiftRight;
            int auxIndex = leftX[elementShiftMasked] + startAux;
            aux[auxIndex] = element;
            oAux[auxIndex] = oArray[i];
            leftX[elementShiftMasked]++;
        }
        System.arraycopy(aux, startAux, array, start, end - start);
        System.arraycopy(oAux, startAux, oArray, start, end - start);
    }

    public static void reverse(final Object[] oArray, final int start, final int end) {
        int length = end - start;
        int end2 = start + length / 2;
        for (int i = start; i < end2; i++) {
            swap(oArray, i, end - i - 1);
        }
    }
}
