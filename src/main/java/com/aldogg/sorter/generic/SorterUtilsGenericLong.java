package com.aldogg.sorter.generic;

import com.aldogg.sorter.long_.object.LongMapper;

import static com.aldogg.sorter.generic.SorterUtilsGeneric.swap;

public class SorterUtilsGenericLong {
    public static int partitionNotStableUpperBit(final Object[] array, final int start, final int endP1, LongMapper mapper) {
        int left = start;
        int right = endP1 - 1;

        while (left <= right) {
            Object element = array[left];
            if (mapper.value(element) >= 0L) {
                left++;
            } else {
                while (left <= right) {
                    element = array[right];
                    if (mapper.value(element) >= 0L) {
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

    public static int partitionReverseNotStableUpperBit(final Object[] array, final int start, final int endP1, final LongMapper mapper) {
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

    public static int partitionStable(final Object[] array, final int start, final int endP1, final int mask, final Object[] aux, final LongMapper mapper) {
        int left = start;
        int right = 0;
        for (int i = start; i < endP1; ++i) {
            Object element = array[i];
            if ((mapper.value(element) & mask) == 0L) {
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

}
