package com.aldogg.sorter.int_;

import com.aldogg.sorter.shared.OrderAnalysisResult;
import com.aldogg.sorter.shared.Section;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;

import static com.aldogg.sorter.BitSorterUtils.getKeySN;
import static java.lang.Integer.MIN_VALUE;

public class SorterUtilsInt {

    public static void swap(final int[] array, final int left, final int right) {
        int auxS = array[left];
        array[left] = array[right];
        array[right] = auxS;
    }

    public static int partitionNotStable(final int[] array, final int start, final int endP1, final int mask) {
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

    public static int partitionReverseNotStable(final int[] array, final int start, final int endP1, final int mask) {
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


    public static int partitionStable(final int[] array, final int start, final int endP1, final int mask, final int[] aux) {
        int left = start;
        int right = 0;
        for (int i = start; i < endP1; ++i) {
            int element = array[i];
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

    public static int[] partitionStableLastBits(final int[] array, final int start, final Section section, final int[] aux, final int startAux, final int n) {
        final int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
        final int endP1 = start + n;
        final int countLength = 1 << section.bits;
        final int[] count = new int[countLength];
        for (int i = start; i < endP1; ++i) {
            count[array[i] & mask]++;
        }
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        if (startAux == 0) {
            for (int i = start; i < endP1; ++i) {
                int element = array[i];
                aux[count[element & mask]++] = element;
            }
        } else {
            for (int i = start; i < endP1; ++i) {
                int element = array[i];
                aux[count[element & mask]++ + startAux] = element;
            }
        }
        return count;
    }

    public static int[] partitionStableOneGroupBits(final int[] array, final int start, final Section section, final int[] aux, int startAux, int n) {
        final int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
        final int shiftRight = section.shift;
        final int endP1 = start + n;
        final int countLength = 1 << section.bits;
        final int[] count = new int[countLength];
        for (int i = start; i < endP1; ++i) {
            count[(array[i] & mask) >>> shiftRight]++;
        }
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        if (startAux == 0) {
            for (int i = start; i < endP1; ++i) {
                int element = array[i];
                aux[count[(element & mask) >>> shiftRight]++] = element;
            }
        } else {
            for (int i = start; i < endP1; ++i) {
                int element = array[i];
                aux[count[(element & mask) >>> shiftRight]++ + startAux] = element;
            }
        }
        return count;
    }


    public static int[] partitionStableNGroupBits(final int[] array, final int start, Section[] sections, final int[] aux, final int startAux, final int n) {
        final int endP1 = start + n;
        int bits = 0;
        for (Section section : sections) {
            bits += section.bits;
        }
        final int countLength = 1 << bits;
        final int[] count = new int[countLength];
        for (int i = start; i < endP1; ++i) {
            int element = array[i];
            count[getKeySN(element, sections)]++;
        }
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        if (startAux == 0) {
            for (int i = start; i < endP1; ++i) {
                int element = array[i];
                aux[count[getKeySN(element, sections)]++] = element;
            }
        } else {
            for (int i = start; i < endP1; ++i) {
                int element = array[i];
                aux[count[getKeySN(element, sections)]++ + startAux] = element;
            }
        }
        return count;
    }


    public static void reverse(final int[] array, final int start, final int endP1) {
        int length = endP1 - start;
        int ld2 = length / 2;
        int end = endP1 - 1;
        for (int i = 0; i < ld2; ++i) {
            swap(array, start + i, end - i);
        }
    }

    public static int listIsOrderedSigned(final int[] array, final int start, final int endP1) {
        int i1 = array[start];
        int i = start + 1;
        while (i < endP1) {
            int i2 = array[i];
            if (i2 != i1) {
                break;
            }
            ++i;
        }
        if (i == endP1) {
            return OrderAnalysisResult.ALL_EQUAL;
        }

        //ascending
        i1 = array[i];
        if (array[i - 1] < i1) {
            ++i;
            for (; i < endP1; ++i) {
                int i2 = array[i];
                if (i1 > i2) {
                    break;
                }
                i1 = i2;
            }
            if (i == endP1) {
                return OrderAnalysisResult.ASCENDING;
            }
        }
        //descending
        else {
            ++i;
            for (; i < endP1; ++i) {
                int i2 = array[i];
                if (i1 < i2) {
                    break;
                }
                i1 = i2;
            }
            if (i == endP1) {
                return OrderAnalysisResult.DESCENDING;
            }
        }
        return OrderAnalysisResult.UNORDERED;
    }

    public static int listIsOrderedUnSigned(int[] array, int start, int endP1) {
        int i1 = array[start];
        int i = start + 1;
        while (i < endP1) {
            int i2 = array[i];
            if (i2 != i1) {
                break;
            }
            ++i;
        }
        if (i == endP1) {
            return OrderAnalysisResult.ALL_EQUAL;
        }

        //ascending
        i1 = array[i];
        if (array[i - 1] < i1) {
            ++i;
            for (; i < endP1; ++i) {
                int i2 = array[i];
                if (i1 + MIN_VALUE > i2 + MIN_VALUE) {
                    break;
                }
                i1 = i2;
            }
            if (i == endP1) {
                return OrderAnalysisResult.ASCENDING;
            }
        }
        //descending
        else {
            ++i;
            for (; i < endP1; ++i) {
                int i2 = array[i];
                if (i1 + MIN_VALUE < i2 + MIN_VALUE) {
                    break;
                }
                i1 = i2;
            }
            if (i == endP1) {
                return OrderAnalysisResult.DESCENDING;
            }
        }
        return OrderAnalysisResult.UNORDERED;
    }


}
