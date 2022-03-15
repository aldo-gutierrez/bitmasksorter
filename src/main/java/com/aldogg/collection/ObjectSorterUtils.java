package com.aldogg.collection;

import com.aldogg.intType.IntSorterUtils;

public class ObjectSorterUtils {

    /**
     *   partition with 0 memory in-place reverse order
     *   CPU: N
     *   MEM: 1
     *   not stable?
     */
    public static int partitionReverseNotStable(final Object[] oList, final int[] list, final int start, final int end, final int sortMask) {
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
                        ObjectSorterUtils.swap(oList, left,right);
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
     *  CPU: 2*N*K (K=1 for 1 bit) //review
     *  MEM: N //review
     */
    public static int partitionStable(final Object[] oList, final int[] list, final int start, final int end, final int sortMask,
                                      Object[] oAux, int[] aux) {
        int left = start;
        int right = 0;
        for (int i = start; i < end; i++) {
            int element = list[i];
            Object oElement = oList[i];
            if ((element & sortMask) == 0) {
                list[left] = element;
                oList[left] = oElement;
                left++;
            } else {
                aux[right] = element;
                oAux[right] = oElement;
                right++;
            }
        }
        System.arraycopy(aux, 0, list, left, right);
        System.arraycopy(oAux, 0, oList, left, right);
        return left;
    }

    /**
     *  CPU: 3*N + 2^K
     *  MEM: N + 2*2^K
     */
    public static void partitionStableLastBits(Object[] oList, int[] list, int start, int end, int lengthBitsToNumber, Object[] oAux, int[] aux, int sortMask) {
        int[] leftX = new int[lengthBitsToNumber];
        int[] count = new int[lengthBitsToNumber];
        for (int i = start; i < end; i++) {
            int elementShiftMasked = list[i] & sortMask;
            count[elementShiftMasked]++;
        }
        for (int i = 1; i < lengthBitsToNumber; i++) {
            leftX[i] = leftX[i - 1] + count[i - 1];
        }
        for (int i = start; i < end; i++) {
            int element = list[i];
            Object oElement = oList[i];
            int elementShiftMasked = element & sortMask;
            aux[leftX[elementShiftMasked]] = element;
            oAux[leftX[elementShiftMasked]] = oElement;
            leftX[elementShiftMasked]++;
        }
        System.arraycopy(aux, 0, list, start, end - start);
        System.arraycopy(oAux, 0, oList, start, end - start);
    }

    /**
     *  CPU: 3*N + 2^K
     *  MEM: N + 2*2^K
     */
    public static void partitionStableGroupBits(Object[] oList, int[] list, int start, int end, int lengthBitsToNumber, Object[] oAux, int[] aux, int sortMask, int shiftRight) {
        int[] leftX = new int[lengthBitsToNumber];
        int[] count = new int[lengthBitsToNumber];
        for (int i = start; i < end; i++) {
            int element = list[i];
            int elementShiftMasked = (element & sortMask) >>> shiftRight;
            count[elementShiftMasked]++;
        }
        for (int i = 1; i < lengthBitsToNumber; i++) {
            leftX[i] = leftX[i - 1] + count[i - 1];
        }
        for (int i = start; i < end; i++) {
            int element = list[i];
            Object oElement = oList[i];
            int elementShiftMasked = (element & sortMask) >>> shiftRight;
            aux[leftX[elementShiftMasked]] = element;
            oAux[leftX[elementShiftMasked]] = oElement;
            leftX[elementShiftMasked]++;
        }
        System.arraycopy(aux, 0, list, start, end - start);
        System.arraycopy(oAux, 0, oList, start, end - start);
    }

    public static void swap(final Object[] list, final int left, final int right) {
        Object aux = list[left];
        list[left] = list[right];
        list[right] = aux;
    }

}
