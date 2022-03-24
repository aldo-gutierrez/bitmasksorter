package com.aldogg.sorter.intType;

public class IntSorterUtils2 {

    //IT WORKS!! 2022-03-20
    public static int partitionStableHalfMemory(final int[] list, final int start, final int end, final int sortMask) {
        int half = (end - start) / 2;
        int[] aux = new int[half];
        return partitionStableHalfMemory(list, start, end, sortMask, aux);
    }

    public static int partitionStableHalfMemory(final int[] list, final int start, final int end, final int sortMask, int[] aux) {
        int half = (end - start) / 2;
        //int[] aux = new int[half];
        int[] auxOriginal = aux;
        int left = start;
        int right = 0;
        boolean filledRight = false;
        for (int i = start; i < end; i++) {
            int element = list[i];
            if ((element & sortMask) == 0) {
                list[left] = element;
                left++;
            } else {
                if (!filledRight && right >= half) {
                    filledRight = true;
                    aux = list;
                    right = start + half;
                }
                aux[right] = element;
                right++;
            }
        }
        if (!filledRight) {
            System.arraycopy(aux, 0, list, left, right);
        } else {
            for (int j = 0; j < (right - (start + half)); j++) {
                IntSorterUtils.swap(list, right - j - 1, end - j - 1);
            }
            System.arraycopy(auxOriginal, 0, list, left, half);
        }
        return left;
    }

    //IT WORKS!! 2022-03-23
    public static int partitionStableOneThirdMemory(final int[] list, final int start, final int end, final int sortMask) {
        int oneThird = (end - start) / 3;
        if (oneThird * 3 < (end - start)) {
            oneThird++;
        }
        int[] aux = new int[oneThird];
        return partitionStableOneThirdMemory(list, start, end, sortMask, aux);
    }

    public static int partitionStableOneThirdMemory(final int[] list, final int start, final int end, final int sortMask, int[] aux) {
        int oneThird = (end - start) / 3;
        if (oneThird * 3 < (end - start)) {
            oneThird++;
        }
        int twoThird = oneThird * 2;
        //int[] aux = new int[oneThird];
        int[] auxOriginal = aux;
        int left = start;
        int right = 0;
        boolean filledRight = false;
        boolean filledLeft = false;
        boolean rightThirdBlock = false;
        for (int i = start; i < end; i++) {
            int element = list[i];
            if ((element & sortMask) == 0) {
                if (filledRight && !filledLeft && left >= start + oneThird) {
                    filledLeft = true;
                    left = start + twoThird;
                }
                list[left] = element;
                left++;
            } else {
                if (!filledRight && right >= oneThird) {
                    filledRight = true;
                    aux = list;
                    right = start + oneThird;
                    if (right <= left) {
                        rightThirdBlock = true;
                        right = start + twoThird;
                        filledLeft = true;
                    }
                }
                aux[right] = element;
                right++;
            }
        }
        if (!filledRight && !filledLeft) {
            System.arraycopy(aux, 0, list, left, right);
            return left;
        } else {
            int rightBlockLength = right - (start + oneThird);
            if (rightThirdBlock) {
                rightBlockLength = rightBlockLength - oneThird;
            }
            if (filledRight && !filledLeft) {
                for (int j = 0; j < rightBlockLength; j++) {
                    IntSorterUtils.swap(list, right - j - 1, end - j - 1);
                }
                System.arraycopy(auxOriginal, 0, list, left, oneThird);
                return left;
            } else {//filledRight && filledLeft
                if (rightThirdBlock) {
                    System.arraycopy(list, start + twoThird, list, end - rightBlockLength, rightBlockLength);
                } else {
                    System.arraycopy(list, start + oneThird, list, end - rightBlockLength, rightBlockLength);
                    int leftBlockLength = left - (start + twoThird);
                    System.arraycopy(list, start + twoThird, list, start + oneThird, leftBlockLength);
                    left = left - oneThird;
                }
                System.arraycopy(auxOriginal, 0, list, left, oneThird);
                return left;
            }
        }
    }
}
