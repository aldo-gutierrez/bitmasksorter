package com.aldogg.sorter.long_.object;

import com.aldogg.sorter.shared.long_mask.MaskInfoLong;
import com.aldogg.sorter.shared.Section;
import com.aldogg.sorter.generic.SorterUtilsGeneric;
import com.aldogg.sorter.long_.SorterUtilsLong;

public class SorterUtilsObjectLong {

    public static int partitionNotStable(final Object[] oArray, final long[] array, final int start, final int endP1, final long mask) {
        int left = start;
        int right = endP1 - 1;

        while (left <= right) {
            long element = array[left];
            if ((element & mask) == 0) {
                left++;
            } else {
                while (left <= right) {
                    element = array[right];
                    if ((element & mask) == 0) {
                        SorterUtilsLong.swap(array, left, right);
                        SorterUtilsGeneric.swap(oArray, left, right);
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

    public static int partitionReverseNotStable(final Object[] oArray, final long[] array, final int start, final int endP1, final long mask) {
        int left = start;
        int right = endP1 - 1;

        while (left <= right) {
            long element = array[left];
            if ((element & mask) == 0) {
                while (left <= right) {
                    element = array[right];
                    if (((element & mask) == 0)) {
                        right--;
                    } else {
                        SorterUtilsLong.swap(array, left, right);
                        SorterUtilsGeneric.swap(oArray, left, right);
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

    public static int partitionStable(final Object[] oArray, final long[] array, final int start, final int endP1, final long mask) {
        long[] aux = new long[endP1 - start];
        Object[] oAux = new Object[endP1 - start];
        return partitionStable(oArray, array, start, endP1, mask, oAux, aux);
    }

    public static int partitionStable(final Object[] oArray, final long[] array, final int start, final int endP1, final long mask,
                                      final Object[] oAux, final long[] aux) {
        int left = start;
        int right = start;
        for (int i = start; i < endP1; i++) {
            long element = array[i];
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

    public static int partitionReverseStable(final Object[] oArray, final long[] array, final int start, final int endP1, final long mask) {
        long[] aux = new long[endP1 - start];
        Object[] oAux = new Object[endP1 - start];
        return partitionReverseStable(oArray, array, start, endP1, mask, oAux, aux);
    }


    public static int partitionReverseStable(final Object[] oArray, final long[] array, final int start, final int endP1,
                                             final long mask, final Object[] oAux, final long[] aux) {
        int left = start;
        int right = start;
        for (int i = start; i < endP1; i++) {
            long element = array[i];
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

    public static void partitionStableLastBits(final Object[] oArray, final long[] array, final int start, final Section section,
                                               final Object[] oAux, final long[] aux, int startAux, final int n) {
        long mask = MaskInfoLong.getMaskRangeBits(section.start, section.shift);
        int endP1 = start + n;
        int[] count = new int[1 << section.bits];
        for (int i = start; i < endP1; i++) {
            count[(int) (array[i] & mask)]++;
        }
        int cLength = count.length;
        for (int i = 0, sum = 0; i < cLength; i++) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < endP1; i++) {
            long element = array[i];
            int elementShiftMasked = (int) (element & mask);
            int auxIndex = count[elementShiftMasked] + startAux;
            aux[auxIndex] = element;
            oAux[auxIndex] = oArray[i];
            count[elementShiftMasked]++;
        }
        System.arraycopy(aux, startAux, array, start, n);
        System.arraycopy(oAux, startAux, oArray, start, n);
    }

    public static void partitionStableGroupBits(final Object[] oArray, final long[] array, final int start, final Section section,
                                                final Object[] oAux, final long[] aux, int startAux, int n) {
        long mask = MaskInfoLong.getMaskRangeBits(section.start, section.shift);
        int shiftRight = section.shift;
        int endP1 = start + n;
        int[] count = new int[1 << section.bits];
        for (int i = start; i < endP1; i++) {
            count[(int) ((array[i] & mask) >>> shiftRight)]++;
        }
        int cLength = count.length;
        for (int i = 0, sum = 0; i < cLength; i++) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < endP1; i++) {
            long element = array[i];
            int elementShiftMasked = (int) ((element & mask) >>> shiftRight);
            int auxIndex = count[elementShiftMasked] + startAux;
            aux[auxIndex] = element;
            oAux[auxIndex] = oArray[i];
            count[elementShiftMasked]++;
        }
        System.arraycopy(aux, startAux, array, start, n);
        System.arraycopy(oAux, startAux, oArray, start, n);
    }

}
