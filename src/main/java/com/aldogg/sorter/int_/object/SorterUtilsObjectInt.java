package com.aldogg.sorter.int_.object;

import com.aldogg.sorter.shared.int_mask.MaskInfoInt;
import com.aldogg.sorter.shared.Section;
import com.aldogg.sorter.int_.SorterUtilsInt;

import static com.aldogg.sorter.generic.SorterUtilsGeneric.swap;

public class SorterUtilsObjectInt {

    /**
     * partition with 0 memory in-place
     * CPU: N
     * MEM: 1
     * not stable?
     */
    public static int partitionNotStable(final Object[] oArray, final int start, final int[] array, final int mask, int n) {
        int left = start;
        int right = start + n - 1;

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
    public static int partitionReverseNotStable(final Object[] oArray, final int start, final int[] array, final int mask, int n) {
        int left = start;
        int right = start + n - 1;

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

    public static int partitionStable(final Object[] oArray, final int oStart, final int[] array, final int mask, int n) {
        int[] aux = new int[n];
        Object[] oAux = new Object[n];
        return partitionStable(oArray, oStart, array, mask, oAux, aux, 0, n);
    }

    public static int partitionStable(final Object[] oArray, int oStart, final int[] array, final int mask,
                                      final Object[] oAux, final int[] aux, int startAux, int n) {
        int left = startAux;
        int oLeft = oStart;
        int right = startAux;
        int endP1 = startAux + n;
        for (int i = startAux, oi = oStart; i < endP1; i++, oi++) {
            int element = array[i];
            Object oElement = oArray[oi];
            if ((element & mask) == 0) {
                array[left] = element;
                oArray[oLeft] = oElement;
                left++;
                oLeft++;
            } else {
                aux[right] = element;
                oAux[right] = oElement;
                right++;
            }
        }
        int lengthRight = right - startAux;
        System.arraycopy(aux, startAux, array, left, lengthRight);
        System.arraycopy(oAux, startAux, oArray, oLeft, lengthRight);
        return oLeft;
    }

    public static int partitionReverseStable(final Object[] oArray, final int oStart, final int[] array, final int mask, int n) {
        int[] aux = new int[n];
        Object[] oAux = new Object[n];
        //TODO what about start of Array, is size n, but where it starts
        return partitionReverseStable(oArray, oStart, mask, array, oAux, aux, 0, n);
    }


    public static int partitionReverseStable(final Object[] oArray, final int oStart, final int mask,
                                             final int[] array, final Object[] oAux, final int[] aux, int startAux, int n) {
        int left = startAux;
        int oLeft = oStart;
        int right = startAux;
        int endP1 = startAux + n;
        for (int i = startAux, oi = oStart; i < endP1; i++, oi++) {
            int element = array[i];
            Object oElement = oArray[oi];
            if (!((element & mask) == 0)) {
                array[left] = element;
                oArray[oLeft] = oElement;
                left++;
                oLeft++;
            } else {
                aux[right] = element;
                oAux[right] = oElement;
                right++;
            }
        }
        int lengthRight = right - startAux;
        System.arraycopy(aux, startAux, array, left, lengthRight);
        System.arraycopy(oAux, startAux, oArray, oLeft, lengthRight);
        return oLeft;
    }

    public static int[] partitionStableLastBits(final Object[] oArray, final int oStart, final int[] array, int aStart, final Section section,
                                                final Object[] oAux, final int[] aux, final int startAux, final int n) {
        int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
        int endP1 = aStart + n;
        int[] count = new int[1 << section.bits];
        int countLength = count.length;
        for (int i = aStart; i < endP1; i++) {
            count[array[i] & mask]++;
        }
        for (int i = 0, sum = 0; i < countLength; i++) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        if (startAux == 0) {
            for (int i = aStart; i < endP1; i++) {
                int element = array[i];
                int elementShiftMasked = element & mask;
                int auxIndex = count[elementShiftMasked];
                aux[auxIndex] = element;
                oAux[auxIndex] = oArray[oStart + i - aStart];
                count[elementShiftMasked]++;
            }
        } else {
            for (int i = aStart; i < endP1; i++) {
                int element = array[i];
                int elementShiftMasked = element & mask;
                int auxIndex = count[elementShiftMasked] + startAux;
                aux[auxIndex] = element;
                oAux[auxIndex] = oArray[oStart + i - aStart];
                count[elementShiftMasked]++;
            }
        }
        System.arraycopy(aux, startAux, array, aStart, n);
        System.arraycopy(oAux, startAux, oArray, oStart, n);
        return count;
    }

    public static int[] partitionStableOneGroupBits(final Object[] oArray, final int oStart, final int[] array, int aStart, final Section section,
                                                    final Object[] oAux, final int[] aux, final int startAux, final int n) {
        int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
        int shiftRight = section.shift;
        int endP1 = aStart + n;
        int[] count = new int[1 << section.bits];
        for (int i = aStart; i < endP1; i++) {
            count[(array[i] & mask) >>> shiftRight]++;
        }
        int cLength = count.length;
        for (int i = 0, sum = 0; i < cLength; i++) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        if (startAux == 0) {
            for (int i = aStart; i < endP1; i++) {
                int element = array[i];
                int elementShiftMasked = (element & mask) >>> shiftRight;
                int auxIndex = count[elementShiftMasked];
                aux[auxIndex] = element;
                oAux[auxIndex] = oArray[oStart + i - aStart];
                count[elementShiftMasked]++;
            }
        } else {
            for (int i = aStart; i < endP1; i++) {
                int element = array[i];
                int elementShiftMasked = (element & mask) >>> shiftRight;
                int auxIndex = count[elementShiftMasked] + startAux;
                aux[auxIndex] = element;
                oAux[auxIndex] = oArray[oStart + i - aStart];
                count[elementShiftMasked]++;
            }
        }
        System.arraycopy(aux, startAux, array, aStart, n);
        System.arraycopy(oAux, startAux, oArray, oStart, n);
        return count;
    }

}
