package com.aldogg.sorter.double_;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.LongSection;

public class DoubleSorterUtils {
    public static void swap(final double[] array, final int left, final int right) {
        double auxS = array[left];
        array[left] = array[right];
        array[right] = auxS;
    }

    public static int partitionNotStable(final double[] array, final int start, final int endP1, final int mask) {
        int left = start;
        int right = endP1 - 1;
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

    public static int partitionReverseNotStable(final double[] array, final int start, final int endP1, final long mask) {
        int left = start;
        int right = endP1 - 1;

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


    public static int partitionStable(final double[] array, final int start, final int endP1, final long mask, final double[] aux) {
        int left = start;
        int right = 0;
        for (int i = start; i < endP1; ++i) {
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
        final long mask = section.mask;
        final int endP1 = start + n;
        final int countLength = 1 << section.length;
        final int[] count = new int[countLength];
        for (int i = start; i < endP1; ++i) {
            count[(int) ((Double.doubleToRawLongBits(array[i]) & mask))]++;
        }
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        if (startAux == 0) {
            for (int i = start; i < endP1; ++i) {
                double element = array[i];
                long elementM = Double.doubleToRawLongBits(element);
                aux[count[(int) (elementM & mask)]++] = element;
            }
        } else {
            for (int i = start; i < endP1; ++i) {
                double element = array[i];
                long elementM = Double.doubleToRawLongBits(element);
                aux[count[(int) (elementM & mask)]++ + startAux] = element;
            }
        }
    }

    public static void partitionStableOneGroupBits(final double[] array, final int start, final LongSection section, final double[] aux, int startAux, int n) {
        final long mask = section.mask;
        final int shiftRight = section.shift;
        final int endP1 = start + n;
        final int countLength = 1 << section.length;
        final int[] count = new int[countLength];
        for (int i = start; i < endP1; ++i) {
            count[(int) ((Double.doubleToRawLongBits(array[i]) & mask) >>> shiftRight)]++;
        }
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        if (startAux == 0) {
            for (int i = start; i < endP1; ++i) {
                double element = array[i];
                long elementM = Double.doubleToRawLongBits(element);
                aux[count[(int) ((elementM & mask) >>> shiftRight)]++] = element;
            }
        } else {
            for (int i = start; i < endP1; ++i) {
                double element = array[i];
                long elementM = Double.doubleToRawLongBits(element);
                aux[count[(int) ((elementM & mask) >>> shiftRight)]++ + startAux] = element;
            }
        }
    }


    public static void reverse(final double[] array, final int start, final int endP1) {
        int length = endP1 - start;
        int ld2 = length / 2;
        int end = endP1 - 1;
        for (int i = 0; i < ld2; ++i) {
            swap(array, start + i, end - i);
        }
    }


    public static int listIsOrderedSigned(final double[] array, final int start, final int endP1) {
        double i1 = array[start];
        int i = start + 1;
        while (i < endP1) {
            double i2 = array[i];
            if (i2 != i1) {
                break;
            }
            ++i;
        }
        if (i == endP1) {
            return AnalysisResult.ALL_EQUAL;
        }

        //ascending
        i1 = array[i];
        if (array[i - 1] < i1) {
            ++i;
            for (; i < endP1; ++i) {
                double i2 = array[i];
                if (i1 > i2) {
                    break;
                }
                i1 = i2;
            }
            if (i == endP1) {
                return AnalysisResult.ASCENDING;
            }
        }
        //descending
        else {
            ++i;
            for (; i < endP1; ++i) {
                double i2 = array[i];
                if (i1 < i2) {
                    break;
                }
                i1 = i2;
            }
            if (i == endP1) {
                return AnalysisResult.DESCENDING;
            }
        }
        return AnalysisResult.UNORDERED;
    }

}
