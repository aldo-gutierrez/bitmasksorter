package com.aldogg.sorter.int_.collection;

import com.aldogg.sorter.IntSection;
import com.aldogg.sorter.ObjectSorterUtils;
import com.aldogg.sorter.int_.IntSorterUtils;

public class ObjectIntSorterUtils {

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
    public static int[] partitionStableLastBits(final Object[] oArray, final int[] array, final int start, final IntSection section,
                                               final Object[] oAux, final int[] aux, int startAux, final int n) {
        int mask = section.sortMask;
        int end = start + n;
        int[] count = new int[1 << section.length];
        for (int i = start; i < end; i++) {
            count[array[i] & mask]++;
        }
        int cLength = count.length;
        for (int i = 0, sum = 0; i < cLength; i++) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < end; i++) {
            int element = array[i];
            int elementShiftMasked = element & mask;
            int auxIndex = count[elementShiftMasked] + startAux;
            aux[auxIndex] = element;
            oAux[auxIndex] = oArray[i];
            count[elementShiftMasked]++;
        }
        System.arraycopy(aux, startAux, array, start, n);
        System.arraycopy(oAux, startAux, oArray, start, n);
        return count;
    }

    /**
     *  CPU: 3*N + 2^K
     *  MEM: N + 2*2^K
     */
    public static int[] partitionStableGroupBits(final Object[] oArray, final int[] array, final int start, final IntSection section,
                                                final Object[] oAux, final int[] aux, int startAux, int n) {
        int mask = section.sortMask;
        int shiftRight = section.shiftRight;
        int end = start + n;
        int[] count = new int[1 << section.length];
        for (int i = start; i < end; i++) {
            count[(array[i] & mask) >>> shiftRight]++;
        }
        int cLength = count.length;
        for (int i = 0, sum = 0; i < cLength; i++) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < end; i++) {
            int element = array[i];
            int elementShiftMasked = (element & mask) >>> shiftRight;
            int auxIndex = count[elementShiftMasked] + startAux;
            aux[auxIndex] = element;
            oAux[auxIndex] = oArray[i];
            count[elementShiftMasked]++;
        }
        System.arraycopy(aux, startAux, array, start, n);
        System.arraycopy(oAux, startAux, oArray, start, n);
        return count;
    }

}
