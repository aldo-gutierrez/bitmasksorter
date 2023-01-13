package com.aldogg.sorter.floatType.collection;

import com.aldogg.sorter.IntSection;
import com.aldogg.sorter.ObjectSorterUtils;
import com.aldogg.sorter.floatType.FloatSorterUtils;

public class ObjectFloatSorterUtils {

    public static int partitionNotStable(final Object[] oArray, final float[] array, final int start, final int end, final int mask) {
        int left = start;
        int right = end - 1;

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

    public static int partitionReverseNotStable(final Object[] oArray, final float[] array, final int start, final int end, final int mask) {
        int left = start;
        int right = end - 1;

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

    public static int partitionStable(final Object[] oArray, final float[] array, final int start, final int end, final int mask) {
        float[] aux = new float[end - start];
        Object[] oAux = new Object[end - start];
        return partitionStable(oArray, array, start, end, mask, oAux, aux);
    }

    public static int partitionStable(final Object[] oArray, final float[] array, final int start, final int end, final int mask,
                                      final Object[] oAux, final float[] aux) {
        int left = start;
        int right = start;
        for (int i = start; i < end; i++) {
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

    public static int partitionReverseStable(final Object[] oArray, final float[] array, final int start, final int end, final int mask) {
        float[] aux = new float[end - start];
        Object[] oAux = new Object[end - start];
        return partitionReverseStable(oArray, array, start, end, mask, oAux, aux);
    }


    public static int partitionReverseStable(final Object[] oArray, final float[] array, final int start, final int end,
                                             final int mask, final Object[] oAux, final float[] aux) {
        int left = start;
        int right = start;
        for (int i = start; i < end; i++) {
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

    public static void partitionStableLastBits(final Object[] oArray, final float[] array, final int start, final IntSection section, int[] leftX,
                                               final Object[] oAux, final float[] aux, int startAux, final int n) {
        int mask = section.sortMask;
        int end = start + n;
        int[] count = new int[1 << section.length];
        for (int i = start; i < end; i++) {
            count[(int) (Float.floatToRawIntBits(array[i]) & mask)]++;
        }
        int il1 = 0;
        int cLength = count.length;
        for (int i = 1; i < cLength; i++, il1++) {
            leftX[i] = leftX[il1] + count[il1];
        }
        for (int i = start; i < end; i++) {
            float element = array[i];
            int elementM = Float.floatToRawIntBits(element);
            int elementShiftMasked = elementM & mask;
            int auxIndex = leftX[elementShiftMasked] + startAux;
            aux[auxIndex] = element;
            oAux[auxIndex] = oArray[i];
            leftX[elementShiftMasked]++;
        }
        System.arraycopy(aux, startAux, array, start, n);
        System.arraycopy(oAux, startAux, oArray, start, n);
    }

    public static void partitionStableGroupBits(final Object[] oArray, final float[] array, final int start, final IntSection section, int[] leftX,
                                                final Object[] oAux, final float[] aux, int startAux, int n) {
        int mask = section.sortMask;
        int shiftRight = section.shiftRight;
        int end = start + n;
        int[] count = new int[1 << section.length];
        for (int i = start; i < end; i++) {
            count[(int) ((Float.floatToRawIntBits(array[i]) & mask) >>> shiftRight)]++;
        }
        int il1 = 0;
        int cLength = count.length;
        for (int i = 1; i < cLength; i++, il1++) {
            leftX[i] = leftX[il1] + count[il1];
        }
        for (int i = start; i < end; i++) {
            float element = array[i];
            int elementM = Float.floatToRawIntBits(element);
            int elementShiftMasked = (elementM & mask) >>> shiftRight;
            int auxIndex = leftX[elementShiftMasked] + startAux;
            aux[auxIndex] = element;
            oAux[auxIndex] = oArray[i];
            leftX[elementShiftMasked]++;
        }
        System.arraycopy(aux, startAux, array, start, n);
        System.arraycopy(oAux, startAux, oArray, start, n);
    }

}
