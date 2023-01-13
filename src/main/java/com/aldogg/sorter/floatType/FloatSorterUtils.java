package com.aldogg.sorter.floatType;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.IntSection;

public class FloatSorterUtils {

    public static void swap(final float[] array, final int left, final int right) {
        float auxS = array[left];
        array[left] = array[right];
        array[right] = auxS;
    }

    public static int partitionNotStable(final float[] array, final int start, final int end, final int mask) {
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
                        swap(array, left, right);
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

    public static int partitionReverseNotStable(final float[] array, final int start, final int end, final int mask) {
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
                        swap(array, left, right);
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


    public static int partitionStable(final float[] array, final int start, final int end, final int mask, final float[] aux) {
        int left = start;
        int right = 0;
        for (int i = start; i < end; i++) {
            float element = array[i];
            int elementFM = Float.floatToRawIntBits(array[i]);
            if ((elementFM & mask) == 0) {
                array[left] = element;
                left++;
            } else {
                aux[right] = element;
                right++;
            }
        }
        System.arraycopy(aux, 0, array, left, right);
        return left;
    }

    public static void partitionStableLastBits(final float[] array, final int start, final IntSection section, int[] leftX, final float[] aux, int startAux, int n) {
        final int mask = section.sortMask;
        final int end = start + n;
        final int[] count = new int[1 << section.length];
        for (int i = start; i < end; i++) {
            count[Float.floatToRawIntBits(array[i]) & mask]++;
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
            aux[leftX[elementShiftMasked] + startAux] = element;
            leftX[elementShiftMasked]++;
        }
    }

    public static void partitionStableLastBits(final float[] array, final int start, final IntSection section, int[] leftX, final float[] aux, int n) {
        final int mask = section.sortMask;
        final int end = start + n;
        final int[] count = new int[1 << section.length];
        for (int i = start; i < end; i++) {
            count[Float.floatToRawIntBits(array[i]) & mask]++;
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
            aux[leftX[elementShiftMasked]] = element;
            leftX[elementShiftMasked]++;
        }
    }


    public static void partitionStableOneGroupBits(final float[] array, final int start, final IntSection section, final int[] leftX, final float[] aux, int startAux, int n) {
        final int mask = section.sortMask;
        final int shiftRight = section.shiftRight;
        final int end = start + n;
        final int[] count = new int[1 << section.length];
        for (int i = start; i < end; i++) {
            count[(Float.floatToRawIntBits(array[i]) & mask) >>> shiftRight]++;
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
            aux[leftX[elementShiftMasked] + startAux] = element;
            leftX[elementShiftMasked]++;
        }
    }

    public static void partitionStableOneGroupBits(final float[] array, final int start, final IntSection section, final int[] leftX, final float[] aux, int n) {
        final int mask = section.sortMask;
        final int shiftRight = section.shiftRight;
        final int end = start + n;
        final int[] count = new int[1 << section.length];
        for (int i = start; i < end; i++) {
            count[(Float.floatToRawIntBits(array[i]) & mask) >>> shiftRight]++;
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
            aux[leftX[elementShiftMasked]] = element;
            leftX[elementShiftMasked]++;
        }
    }


    public static void reverse(final float[] array, final int start, final int end) {
        int length = end - start;
        int ld2 = length / 2;
        int endL1 = end - 1;
        for (int i = 0; i < ld2; i++) {
            swap(array, start + i, endL1 - i);
        }
    }


    public static int listIsOrderedSigned(final float[] array, final int start, final int end) {
        float i1 = array[start];
        int i = start + 1;
        while (i < end) {
            float i2 = array[i];
            if (i2 != i1) {
                break;
            }
            i1 = i2;
            i++;
        }
        if (i == end) {
            return AnalysisResult.ALL_EQUAL;
        }

        //ascending
        i1 = array[i];
        if (array[i - 1] < i1) {
            i++;
            for (; i < end; i++) {
                float i2 = array[i];
                if (i1 > i2) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                return AnalysisResult.ASCENDING;
            }
        }
        //descending
        else {
            i++;
            for (; i < end; i++) {
                float i2 = array[i];
                if (i1 < i2) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                return AnalysisResult.DESCENDING;
            }
        }
        return AnalysisResult.UNORDERED;
    }

}
