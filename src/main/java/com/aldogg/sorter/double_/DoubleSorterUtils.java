package com.aldogg.sorter.double_;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.LongSection;

public class DoubleSorterUtils {
    public static void swap(final double[] array, final int left, final int right) {
        double auxS = array[left];
        array[left] = array[right];
        array[right] = auxS;
    }

    public static int partitionNotStable(final double[] array, final int start, final int end, final int mask) {
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

    public static int partitionReverseNotStable(final double[] array, final int start, final int end, final long mask) {
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


    public static int partitionStable(final double[] array, final int start, final int end, final long mask, final double[] aux) {
        int left = start;
        int right = 0;
        for (int i = start; i < end; ++i) {
            double element = array[i];
            long elementFM = Double.doubleToRawLongBits(array[i]);
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

    public static void partitionStableLastBits(final double[] array, final int start, final LongSection section, final double[] aux, int startAux, int n) {
        final long mask = section.sortMask;
        final int end = start + n;
        final int countLength = 1 << section.length;
        final int[] count = new int[countLength];
        for (int i = start; i < end; ++i) {
            count[(int) ((Double.doubleToRawLongBits(array[i]) & mask))]++;
        }
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < end; ++i) {
            double element = array[i];
            long elementM = Double.doubleToRawLongBits(element);
            aux[count[(int) (elementM & mask)]++ + startAux] = element;
        }
    }

    public static void partitionStableLastBits(final double[] array, final int start, final LongSection section, final double[] aux, int n) {
        final long mask = section.sortMask;
        final int end = start + n;
        final int countLength = 1 << section.length;
        final int[] count = new int[countLength];
        for (int i = start; i < end; ++i) {
            count[(int) (Double.doubleToRawLongBits(array[i]) & mask)]++;
        }
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < end; ++i) {
            double element = array[i];
            long elementM = Double.doubleToRawLongBits(element);
            aux[count[(int) (elementM & mask)]++] = element;
        }
    }


    public static void partitionStableOneGroupBits(final double[] array, final int start, final LongSection section, final double[] aux, int startAux, int n) {
        final long mask = section.sortMask;
        final int shiftRight = section.shiftRight;
        final int end = start + n;
        final int countLength = 1 << section.length;
        final int[] count = new int[countLength];
        for (int i = start; i < end; ++i) {
            count[(int) ((Double.doubleToRawLongBits(array[i]) & mask) >>> shiftRight)]++;
        }
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < end; ++i) {
            double element = array[i];
            long elementM = Double.doubleToRawLongBits(element);
            aux[count[(int) ((elementM & mask) >>> shiftRight)]++ + startAux] = element;
        }
    }

    public static void partitionStableOneGroupBits(final double[] array, final int start, final LongSection section, final double[] aux, int n) {
        final long mask = section.sortMask;
        final int shiftRight = section.shiftRight;
        final int end = start + n;
        final int countLength = 1 << section.length;
        final int[] count = new int[countLength];
        for (int i = start; i < end; ++i) {
            count[(int) ((Double.doubleToRawLongBits(array[i]) & mask) >>> shiftRight)]++;
        }
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < end; ++i) {
            double element = array[i];
            long elementM = Double.doubleToRawLongBits(element);
            aux[count[(int) ((elementM & mask) >>> shiftRight)]++] = element;
        }
    }


    public static void reverse(final double[] array, final int start, final int end) {
        int length = end - start;
        int ld2 = length / 2;
        int endL1 = end - 1;
        for (int i = 0; i < ld2; ++i) {
            swap(array, start + i, endL1 - i);
        }
    }


    public static int listIsOrderedSigned(final double[] array, final int start, final int end) {
        double i1 = array[start];
        int i = start + 1;
        while (i < end) {
            double i2 = array[i];
            if (i2 != i1) {
                break;
            }
            ++i;
        }
        if (i == end) {
            return AnalysisResult.ALL_EQUAL;
        }

        //ascending
        i1 = array[i];
        if (array[i - 1] < i1) {
            ++i;
            for (; i < end; ++i) {
                double i2 = array[i];
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
            ++i;
            for (; i < end; ++i) {
                double i2 = array[i];
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
