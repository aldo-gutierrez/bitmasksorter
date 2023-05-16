package com.aldogg.sorter.float_.collection;

import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.ObjectSorterUtils;
import com.aldogg.sorter.Section;
import com.aldogg.sorter.float_.FloatSorterUtils;

public class ObjectFloatSorterUtils {

    public static int partitionNotStable(final Object[] oArray, final float[] array, final int start, final int endP1, final int mask) {
        int left = start;
        int right = endP1 - 1;

        while (left <= right) {
            int element = Float.floatToRawIntBits(array[left]);
            if ((element & mask) == 0) {
                left++;
            } else {
                while (left <= right) {
                    element = Float.floatToRawIntBits(array[right]);
                    if ((element & mask) == 0) {
                        FloatSorterUtils.swap(array, left, right);
                        ObjectSorterUtils.swap(oArray, left, right);
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

    public static int partitionReverseNotStable(final Object[] oArray, final float[] array, final int start, final int endP1, final int mask) {
        int left = start;
        int right = endP1 - 1;

        while (left <= right) {
            int element = Float.floatToRawIntBits(array[left]);
            if ((element & mask) == 0) {
                while (left <= right) {
                    element = Float.floatToRawIntBits(array[right]);
                    if (((element & mask) == 0)) {
                        right--;
                    } else {
                        FloatSorterUtils.swap(array, left, right);
                        ObjectSorterUtils.swap(oArray, left, right);
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

    public static int partitionStable(final Object[] oArray, final float[] array, final int start, final int endP1, final int mask) {
        float[] aux = new float[endP1 - start];
        Object[] oAux = new Object[endP1 - start];
        return partitionStable(oArray, array, start, endP1, mask, oAux, aux);
    }

    public static int partitionStable(final Object[] oArray, final float[] array, final int start, final int endP1, final int mask,
                                      final Object[] oAux, final float[] aux) {
        int left = start;
        int right = start;
        for (int i = start; i < endP1; i++) {
            float element = array[i];
            int elementFM = Float.floatToRawIntBits(array[i]);
            Object oElement = oArray[i];
            if ((elementFM & mask) == 0) {
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

    public static int partitionReverseStable(final Object[] oArray, final float[] array, final int start, final int endP1, final int mask) {
        float[] aux = new float[endP1 - start];
        Object[] oAux = new Object[endP1 - start];
        return partitionReverseStable(oArray, array, start, endP1, mask, oAux, aux);
    }


    public static int partitionReverseStable(final Object[] oArray, final float[] array, final int start, final int endP1,
                                             final int mask, final Object[] oAux, final float[] aux) {
        int left = start;
        int right = start;
        for (int i = start; i < endP1; i++) {
            float element = array[i];
            int elementFM = Float.floatToRawIntBits(array[i]);
            Object oElement = oArray[i];
            if (!((elementFM & mask) == 0)) {
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

    public static void partitionStableLastBits(final Object[] oArray, final float[] array, final int start, final Section section,
                                               final Object[] oAux, final float[] aux, int startAux, final int n) {
        int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
        int endP1 = start + n;
        int[] count = new int[1 << section.bits];
        for (int i = start; i < endP1; i++) {
            count[Float.floatToRawIntBits(array[i]) & mask]++;
        }
        int cLength = count.length;
        for (int i = 0, sum = 0; i < cLength; i++) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < endP1; i++) {
            float element = array[i];
            int elementM = Float.floatToRawIntBits(element);
            int elementShiftMasked = elementM & mask;
            int auxIndex = count[elementShiftMasked] + startAux;
            aux[auxIndex] = element;
            oAux[auxIndex] = oArray[i];
            count[elementShiftMasked]++;
        }
        System.arraycopy(aux, startAux, array, start, n);
        System.arraycopy(oAux, startAux, oArray, start, n);
    }

    public static void partitionStableGroupBits(final Object[] oArray, final float[] array, final int start, final Section section,
                                                final Object[] oAux, final float[] aux, int startAux, int n) {
        int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
        int shiftRight = section.shift;
        int endP1 = start + n;
        int[] count = new int[1 << section.bits];
        for (int i = start; i < endP1; i++) {
            count[(Float.floatToRawIntBits(array[i]) & mask) >>> shiftRight]++;
        }
        int cLength = count.length;
        for (int i = 0, sum = 0; i < cLength; i++) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < endP1; i++) {
            float element = array[i];
            int elementM = Float.floatToRawIntBits(element);
            int elementShiftMasked = (elementM & mask) >>> shiftRight;
            int auxIndex = count[elementShiftMasked] + startAux;
            aux[auxIndex] = element;
            oAux[auxIndex] = oArray[i];
            count[elementShiftMasked]++;
        }
        System.arraycopy(aux, startAux, array, start, n);
        System.arraycopy(oAux, startAux, oArray, start, n);
    }

}
