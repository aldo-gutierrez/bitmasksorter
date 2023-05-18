package com.aldogg.sorter.int_.object;

import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.Section;
import com.aldogg.sorter.int_.SorterUtilsInt;

import static com.aldogg.sorter.generic.SorterUtilsGeneric.swap;

public class SorterUtilsObjectInt {

    /**
     * partition with 0 memory in-place
     * CPU: N
     * MEM: 1
     * not stable?
     */
    public static int partitionNotStable(final Object[] oArray, final int[] array, final int start, final int endP1, final int mask) {
        int left = start;
        int right = endP1 - 1;

        while (left <= right) {
            int element = array[left];
            if ((element & mask) == 0) {
                left++;
            } else {
                while (left <= right) {
                    element = array[right];
                    if ((element & mask) == 0) {
                        SorterUtilsInt.swap(array, left, right);
                        swap(oArray, left, right);
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
     * partition with 0 memory in-place reverse order
     * CPU: N
     * MEM: 1
     * not stable?
     */
    public static int partitionReverseNotStable(final Object[] oArray, final int[] array, final int start, final int endP1, final int mask) {
        int left = start;
        int right = endP1 - 1;

        while (left <= right) {
            int element = array[left];
            if ((element & mask) == 0) {
                while (left <= right) {
                    element = array[right];
                    if (((element & mask) == 0)) {
                        right--;
                    } else {
                        SorterUtilsInt.swap(array, left, right);
                        swap(oArray, left, right);
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

    public static int partitionStable(final Object[] oArray, final int[] array, final int start, final int endP1, final int mask) {
        int[] aux = new int[endP1 - start];
        Object[] oAux = new Object[endP1 - start];
        return partitionStable(oArray, array, start, endP1, mask, oAux, aux);
    }

    public static int partitionStable(final Object[] oArray, final int[] array, final int start, final int endP1, final int mask,
                                      final Object[] oAux, final int[] aux) {
        int left = start;
        int right = start;
        for (int i = start; i < endP1; i++) {
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
        System.arraycopy(oAux, start, oArray, left, lengthRight);
        return left;
    }

    public static int partitionReverseStable(final Object[] oArray, final int[] array, final int start, final int endP1, final int mask) {
        int[] aux = new int[endP1 - start];
        Object[] oAux = new Object[endP1 - start];
        return partitionReverseStable(oArray, array, start, endP1, mask, oAux, aux);
    }


    public static int partitionReverseStable(final Object[] oArray, final int[] array, final int start, final int endP1,
                                             final int mask, final Object[] oAux, final int[] aux) {
        int left = start;
        int right = start;
        for (int i = start; i < endP1; i++) {
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
        System.arraycopy(oAux, start, oArray, left, lengthRight);
        return left;
    }

    public static int[] partitionStableLastBits(final Object[] oArray, final int[] array, final int start, final Section section,
                                                final Object[] oAux, final int[] aux, final int startAux, final int n) {
        int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
        int endP1 = start + n;
        int[] count = new int[1 << section.bits];
        for (int i = start; i < endP1; i++) {
            count[array[i] & mask]++;
        }
        int cLength = count.length;
        for (int i = 0, sum = 0; i < cLength; i++) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        if (startAux == 0) {
            for (int i = start; i < endP1; i++) {
                int element = array[i];
                int elementShiftMasked = element & mask;
                int auxIndex = count[elementShiftMasked];
                aux[auxIndex] = element;
                oAux[auxIndex] = oArray[i];
                count[elementShiftMasked]++;
            }
        } else {
            for (int i = start; i < endP1; i++) {
                int element = array[i];
                int elementShiftMasked = element & mask;
                int auxIndex = count[elementShiftMasked] + startAux;
                aux[auxIndex] = element;
                oAux[auxIndex] = oArray[i];
                count[elementShiftMasked]++;
            }
        }
        System.arraycopy(aux, startAux, array, start, n);
        System.arraycopy(oAux, startAux, oArray, start, n);
        return count;
    }

    public static int[] partitionStableGroupBits(final Object[] oArray, final int[] array, final int start, final Section section,
                                                 final Object[] oAux, final int[] aux, final int startAux, final int n) {
        int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
        int shiftRight = section.shift;
        int endP1 = start + n;
        int[] count = new int[1 << section.bits];
        for (int i = start; i < endP1; i++) {
            count[(array[i] & mask) >>> shiftRight]++;
        }
        int cLength = count.length;
        for (int i = 0, sum = 0; i < cLength; i++) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        if (startAux == 0) {
            for (int i = start; i < endP1; i++) {
                int element = array[i];
                int elementShiftMasked = (element & mask) >>> shiftRight;
                int auxIndex = count[elementShiftMasked];
                aux[auxIndex] = element;
                oAux[auxIndex] = oArray[i];
                count[elementShiftMasked]++;
            }
        } else {
            for (int i = start; i < endP1; i++) {
                int element = array[i];
                int elementShiftMasked = (element & mask) >>> shiftRight;
                int auxIndex = count[elementShiftMasked] + startAux;
                aux[auxIndex] = element;
                oAux[auxIndex] = oArray[i];
                count[elementShiftMasked]++;
            }
        }
        System.arraycopy(aux, startAux, array, start, n);
        System.arraycopy(oAux, startAux, oArray, start, n);
        return count;
    }

}
