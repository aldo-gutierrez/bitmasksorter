package com.aldogg.sorter.doubleType.collection;

import com.aldogg.sorter.LongSection;
import com.aldogg.sorter.ObjectSorterUtils;
import com.aldogg.sorter.doubleType.DoubleSorterUtils;

public class ObjectDoubleSorterUtils {

    public static int partitionNotStable(final Object[] oArray, final double[] array, final int start, final int end, final long mask) {
        int left = start;
        int right = end - 1;

        while (left <= right) {
            long element = Double.doubleToRawLongBits(array[left]);
            if ((element & mask) == 0) {
                left++;
            } else {
                while (left <= right) {
                    element = Double.doubleToRawLongBits(array[right]);
                    if ((element & mask) == 0) {
                        DoubleSorterUtils.swap(array, left, right);
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

    public static int partitionReverseNotStable(final Object[] oArray, final double[] array, final int start, final int end, final long mask) {
        int left = start;
        int right = end - 1;

        while (left <= right) {
            long element = Double.doubleToRawLongBits(array[left]);
            if ((element & mask) == 0) {
                while (left <= right) {
                    element = Double.doubleToRawLongBits(array[right]);
                    if (((element & mask) == 0)) {
                        right--;
                    } else {
                        DoubleSorterUtils.swap(array, left, right);
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

    public static int partitionStable(final Object[] oArray, final double[] array, final int start, final int end, final long mask) {
        double[] aux = new double[end - start];
        Object[] oAux = new Object[end - start];
        return partitionStable(oArray, array, start, end, mask, oAux, aux);
    }

    public static int partitionStable(final Object[] oArray, final double[] array, final int start, final int end, final long mask,
                                      final Object[] oAux, final double[] aux) {
        int left = start;
        int right = start;
        for (int i = start; i < end; i++) {
            double element = array[i];
            long elementFM = Double.doubleToRawLongBits(array[i]);
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

    public static int partitionReverseStable(final Object[] oArray, final double[] array, final int start, final int end, final long mask) {
        double[] aux = new double[end - start];
        Object[] oAux = new Object[end - start];
        return partitionReverseStable(oArray, array, start, end, mask, oAux, aux);
    }


    public static int partitionReverseStable(final Object[] oArray, final double[] array, final int start, final int end,
                                             final long mask, final Object[] oAux, final double[] aux) {
        int left = start;
        int right = start;
        for (int i = start; i < end; i++) {
            double element = array[i];
            long elementFM = Double.doubleToRawLongBits(array[i]);
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

    public static void partitionStableLastBits(final Object[] oArray, final double[] array, final int start, final LongSection section, int[] leftX,
                                               final Object[] oAux, final double[] aux, int startAux, final int n) {
        long mask = section.sortMask;
        int end = start + n;
        int[] count = new int[1 << section.length];
        for (int i = start; i < end; i++) {
            count[(int) (Double.doubleToRawLongBits(array[i]) & mask)]++;
        }
        int il1 = 0;
        int cLength = count.length;
        for (int i = 1; i < cLength; i++, il1++) {
            leftX[i] = leftX[il1] + count[il1];
        }
        for (int i = start; i < end; i++) {
            double element = array[i];
            long elementM = Double.doubleToRawLongBits(element);
            int elementShiftMasked = (int) (elementM & mask);
            int auxIndex = leftX[elementShiftMasked] + startAux;
            aux[auxIndex] = element;
            oAux[auxIndex] = oArray[i];
            leftX[elementShiftMasked]++;
        }
        System.arraycopy(aux, startAux, array, start, n);
        System.arraycopy(oAux, startAux, oArray, start, n);
    }

    public static void partitionStableGroupBits(final Object[] oArray, final double[] array, final int start, final LongSection section, int[] leftX,
                                                final Object[] oAux, final double[] aux, int startAux, int n) {
        long mask = section.sortMask;
        int shiftRight = section.shiftRight;
        int end = start + n;
        int[] count = new int[1 << section.length];
        for (int i = start; i < end; i++) {
            count[(int) ((Double.doubleToRawLongBits(array[i]) & mask) >>> shiftRight)]++;
        }
        int il1 = 0;
        int cLength = count.length;
        for (int i = 1; i < cLength; i++, il1++) {
            leftX[i] = leftX[il1] + count[il1];
        }
        for (int i = start; i < end; i++) {
            double element = array[i];
            long elementM = Double.doubleToRawLongBits(element);
            int elementShiftMasked = (int) ((elementM & mask) >>> shiftRight);
            int auxIndex = leftX[elementShiftMasked] + startAux;
            aux[auxIndex] = element;
            oAux[auxIndex] = oArray[i];
            leftX[elementShiftMasked]++;
        }
        System.arraycopy(aux, startAux, array, start, n);
        System.arraycopy(oAux, startAux, oArray, start, n);
    }

}
