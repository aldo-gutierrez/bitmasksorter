package com.aldogg.sorter.longType;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.LongSection;

import static java.lang.Long.MIN_VALUE;

public class LongSorterUtils {

    public static void swap(final long[] array, final int left, final int right) {
        long auxS = array[left];
        array[left] = array[right];
        array[right] = auxS;
    }

    public static int partitionNotStable(final long[] array, final  int start, final int end, final long mask) {
        int left = start;
        int right = end - 1;
        while (left <= right) {
            long element = array[left];
            if ((element & mask) == 0) {
                left++;
            } else {
                while (left <= right) {
                    element = array[right];
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

    public static int partitionReverseNotStable(final long[] array, final int start, final int end, final long mask) {
        int left = start;
        int right = end - 1;

        while (left <= right) {
            long element = array[left];
            if ((element & mask) == 0) {
                while (left <= right) {
                    element = array[right];
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


    public static int partitionStable(final long[] array, final int start, final int end, final long mask, final long[] aux) {
        int left = start;
        int right = 0;
        for (int i = start; i < end; i++) {
            long element = array[i];
            if ((element & mask) == 0) {
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

    public static void partitionStableLastBits(final long[] array, final int start, final LongSection section, final long[] aux, int startAux, int n) {
        final long mask = section.sortMask;
        final int end = start + n;
        final int[] count = new int[1 << section.length];
        for (int i = start; i < end; i++) {
            count[(int) (array[i] & mask)]++;
        }
        int cLength = count.length;
        for (int i = 0, sum = 0; i < cLength; i++) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < end; i++) {
            long element = array[i];
            aux[count[(int) (element & mask)]++ + startAux] = element;
        }
    }

    public static void partitionStableLastBits(final long[] array, final int start, final LongSection section, final long[] aux, int n) {
        final long mask = section.sortMask;
        final int end = start + n;
        final int[] count = new int[1 << section.length];
        for (int i = start; i < end; i++) {
            count[(int) (array[i] & mask)]++;
        }
        int cLength = count.length;
        for (int i = 0, sum = 0; i < cLength; i++) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < end; i++) {
            long element = array[i];
            aux[count[(int) (element & mask)]++] = element;
        }
    }


    public static int[] partitionStableOneGroupBits(final long[] array, final int start, final LongSection section, final long[] aux, int startAux, int n) {
        final long mask = section.sortMask;
        final int shiftRight = section.shiftRight;
        final int end = start + n;
        final int[] count = new int[1 << section.length];
        for (int i = start; i < end; i++) {
            count[(int) ((array[i] & mask) >>> shiftRight)]++;
        }
        int cLength = count.length;
        for (int i = 0, sum = 0; i < cLength; i++) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < end; i++) {
            long element = array[i];
            aux[count[(int) ((element & mask) >>> shiftRight)]++ + startAux] = element;
        }
        return count;
    }

    public static int[] partitionStableOneGroupBits(final long[] array, final int start, final LongSection section, final long[] aux, int n) {
        final long mask = section.sortMask;
        final int shiftRight = section.shiftRight;
        final int end = start + n;
        final int[] count = new int[1 << section.length];
        for (int i = start; i < end; i++) {
            count[(int) ((array[i] & mask) >>> shiftRight)]++;
        }
        int cLength = count.length;
        for (int i = 0, sum = 0; i < cLength; i++) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < end; i++) {
            long element = array[i];
            aux[count[(int) ((element & mask) >>> shiftRight)]++] = element;
        }
        return count;
    }

    public static void reverse(final long[] array, final int start, final int end) {
        int length = end - start;
        int ld2 = length / 2;
        int endL1 = end - 1;
        for (int i = 0; i < ld2; i++) {
            swap(array, start + i, endL1 - i);
        }
    }

    public static int listIsOrderedSigned(final long[] array, final int start, final int end) {
        long i1 = array[start];
        int i = start + 1;
        while (i < end) {
            long i2 = array[i];
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
                long i2 = array[i];
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
                long i2 = array[i];
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

    public static int listIsOrderedUnSigned(long[] array, int start, int end) {
        long i1 = array[start];
        int i = start + 1;
        while (i < end) {
            long i2 = array[i];
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
                long i2 = array[i];
                if (i1 + MIN_VALUE > i2 + MIN_VALUE) {
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
                long i2 = array[i];
                if (i1 + MIN_VALUE < i2 + MIN_VALUE) {
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
