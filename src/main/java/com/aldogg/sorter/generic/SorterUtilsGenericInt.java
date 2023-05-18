package com.aldogg.sorter.generic;

import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.Section;
import com.aldogg.sorter.int_.object.IntMapper;

import static com.aldogg.sorter.generic.SorterUtilsGeneric.swap;

public class SorterUtilsGenericInt {

    public static int partitionNotStableUpperBit(final Object[] array, final int start, final int endP1, IntMapper mapper) {
        int left = start;
        int right = endP1 - 1;

        while (left <= right) {
            Object element = array[left];
            if (mapper.value(element) >= 0) {
                left++;
            } else {
                while (left <= right) {
                    element = array[right];
                    if (mapper.value(element) >= 0) {
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

    public static int partitionReverseNotStableUpperBit(final Object[] array, final int start, final int endP1, final IntMapper mapper) {
        int left = start;
        int right = endP1 - 1;

        while (left <= right) {
            Object element = array[left];
            if (mapper.value(element) >= 0) {
                while (left <= right) {
                    element = array[right];
                    if (mapper.value(element) >= 0) {
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

    public static int partitionStable(final Object[] array, final int start, final int endP1, final int mask, final Object[] aux, final IntMapper mapper) {
        int left = start;
        int right = 0;
        for (int i = start; i < endP1; ++i) {
            Object element = array[i];
            if ((mapper.value(element) & mask) == 0) {
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

    public static int[] partitionStableLastBits(final Object[] array, final int start, final Section section, final Object[] aux, final int startAux, final int n, IntMapper mapper) {
        final int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
        final int endP1 = start + n;
        final int countLength = 1 << section.bits;
        final int[] count = new int[countLength];
        for (int i = start; i < endP1; ++i) {
            count[mapper.value(array[i]) & mask]++;
        }
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        if (startAux == 0) {
            for (int i = start; i < endP1; ++i) {
                Object element = array[i];
                aux[count[mapper.value(element) & mask]++] = element;
            }
        } else {
            for (int i = start; i < endP1; ++i) {
                Object element = array[i];
                aux[count[mapper.value(element) & mask]++ + startAux] = element;
            }
        }
        return count;
    }

    public static int[] partitionStableOneGroupBits(final Object[] array, final int start, final Section section, final Object[] aux, int startAux, int n, IntMapper mapper) {
        final int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
        final int shiftRight = section.shift;
        final int endP1 = start + n;
        final int countLength = 1 << section.bits;
        final int[] count = new int[countLength];
        for (int i = start; i < endP1; ++i) {
            count[(mapper.value(array[i]) & mask) >>> shiftRight]++;
        }
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        if (startAux == 0) {
            for (int i = start; i < endP1; ++i) {
                Object element = array[i];
                aux[count[(mapper.value(element) & mask) >>> shiftRight]++] = element;
            }
        } else {
            for (int i = start; i < endP1; ++i) {
                Object element = array[i];
                aux[count[(mapper.value(element) & mask) >>> shiftRight]++ + startAux] = element;
            }
        }
        return count;
    }


}
