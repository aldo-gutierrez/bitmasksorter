package com.aldogg;

import static com.aldogg.BitSorterUtils.*;
import static com.aldogg.BitSorterUtils.getMask;

/**
 * Basic XXXSorter
 * It doesn't include a count sort it
 * It doesn't include a comparator sort for small lists
 */
public class QuickBitSorterUInt implements IntSorter {

    public void sort(int[] list) {
        final int start = 0;
        final int end = list.length;
        int[] maskParts = getMask(list, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] listK = getMaskAsList(mask);
        sort(list, start, end, listK, 0);
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    public void sort(final int[] list, final int start, final int end, final int[] kList, final int kIndex) {
        final int listLength = end - start;
        if (listLength < 2) {
            return;
        }
        if (kIndex > kList.length - 1) {
            return;
        }
        int sortMask = getMask(kList[kIndex]);
        int finalLeft = partition(list, start, end, sortMask);
        if (finalLeft - start > 1) {
            sort(list, start, finalLeft, kList, kIndex + 1);
        }
        if (end - finalLeft > 1) {
            sort(list, finalLeft, end, kList, kIndex + 1);
        }
    }

    //TODO CHECK
    protected int partitionWithAuxMemory(int[] list, int start, int end, int sortMask) {
        int[] aux = new int[end - start];
        int left = start;
        int right = end - 1;

        for (int i = start; i < end; i++) {
            int element = list[i];
            if ((element & sortMask) == 0) {
                aux[left] = element;
                left++;
            } else {
                aux[right] = element;
                right--;
            }
        }
        System.arraycopy(aux, start, list, start, end - start);
        return left;
    }

    protected int partition(int[] list, int start, int end, int sortMask) {
        int left = start;
        int right = end - 1;

        for (; left <= right; ) {
            int element = list[left];
            if ((element & sortMask) == 0) {
                left++;
            } else {
                for (; left <= right; ) {
                    element = list[right];
                    if (!((element & sortMask) == 0)) {
                        right--;
                    } else {
                        IntSorterUtils.swap(list, left, right);
                        left++;
                        right--;
                        break;
                    }
                }
            }
        }
        return left;
    }

    protected int partitionNegative(int[] list, int start, int end, int sortMask) {
        int left = start;
        int right = end - 1;

        for (; left <= right; ) {
            int element = list[left];
            if (!((element & sortMask) == 0)) {
                left++;
            } else {
                for (; left <= right; ) {
                    element = list[right];
                    if (((element & sortMask) == 0)) {
                        right--;
                    } else {
                        IntSorterUtils.swap(list, left, right);
                        left++;
                        right--;
                        break;
                    }
                }
            }
        }
        return left;
    }

}
