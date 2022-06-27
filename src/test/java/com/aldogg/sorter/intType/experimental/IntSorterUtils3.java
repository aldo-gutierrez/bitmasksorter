package com.aldogg.sorter.intType.experimental;

import com.aldogg.sorter.intType.IntSorterUtils;

/**
 *
 */
public class IntSorterUtils3 {

    public static void swap3(final int[] array, final int left, final int right, final int nPosLeft, final  int nPosRight) {
        int auxL = array[left];
        int auxR = array[right];
        array[nPosLeft] = auxL;
        array[nPosRight] = auxR;
    }

    public static int[] skipSorted(final int[] array, final int start, final int end, final int sortMask) {
        int left = start;
        int right = end - 1;

        while (left <= right) {
            if ((array[left] & sortMask) == 0) {
                left++;
            } else {
                while (left + 1 <= right) {
                    if ((array[left+1] & sortMask) == 0) {
                        IntSorterUtils.swap(array, left, left+1);
                        left++;
                    } else {
                        break;
                    }
                }
                break;
            }
        }

        while (left <= right) {
            if ((array[right] & sortMask) != 0) {
                right--;
            } else {
                while (left  <= right - 1) {
                    if ((array[right - 1] & sortMask) != 0) {
                        IntSorterUtils.swap(array, right, right - 1);
                        right--;
                    } else {
                        break;
                    }
                }
                break;
            }
        }
        return new int[] {left, right};
    }

    public static int[] skipSortedReverse(final int[] array, final int start, final int end, final int mask) {
        int left = start;
        int right = end - 1;

        while (left <= right) {
            if ((array[left] & mask) != 0) {
                left++;
            } else {
                while (left + 1 <= right) {
                    if ((array[left+1] & mask) != 0) {
                        IntSorterUtils.swap(array, left, left+1);
                        left++;
                    } else {
                        break;
                    }
                }
                break;
            }
        }

        while (left <= right) {
            if ((array[right] & mask) == 0) {
                right--;
            } else {
                while (left  <= right - 1) {
                    if ((array[right - 1] & mask) == 0) {
                        IntSorterUtils.swap(array, right, right - 1);
                        right--;
                    } else {
                        break;
                    }
                }
                break;
            }
        }
        return new int[] {left, right};
    }

    /**
     * LOOKS O(N^2) but it works 2022 03 27
     * @return
     */
    public static int partitionStableRecursive(final int[] array, final int start, final int end, final int mask) {
        int[] aux = skipSorted(array, start, end, mask);
        int left = aux[0];
        int right = aux[1];

        if (left >= right) {
            if (left == right) {
                return right + 1;
            } else {
                return left;
            }
        } else {
            int leftAux = partitionStableReverseRecursive(array, left, right+1, mask);
            return reverseResult(array, left, right+1, leftAux);
        }
    }

    //LOOKS O(N^2)
    public static int partitionStableReverseRecursive(final int[] array, final int start, final int end, final int mask) {
        int[] aux = skipSortedReverse(array, start, end, mask);
        int left = aux[0];
        int right = aux[1];

        if (left >= right) {
            if (left == right) {
                return right + 1;
            } else {
                return left;
            }
        } else {
            int leftAux = partitionStableRecursive(array, left, right+1, mask);
            //Reverse Result
            return reverseResult(array, left, right+1, leftAux);
        }
    }

    public static int  reverseResult(int[] array, int left, int right, int leftAux) {
        int lengthLeftAux = leftAux - left;
        int lengthRightAux = right - leftAux;
        boolean mayorLeft = lengthLeftAux > lengthRightAux;
        if (lengthLeftAux != 0 && lengthRightAux != 0) {
            if (lengthLeftAux == lengthRightAux) {
                for (int i=0; i < lengthLeftAux; i++) {
                    IntSorterUtils.swap(array, left + i, leftAux + i);
                }
            } else {
                if (mayorLeft) {
                    //close sizes
                    if ((lengthLeftAux - lengthRightAux) < lengthRightAux) {
                        int[] aux = new int[lengthLeftAux - lengthRightAux];
                        System.arraycopy(array, left, aux, 0, aux.length);
                        for (int i=0; i < lengthRightAux; i++) {
                            swap3(array, leftAux - lengthRightAux + i, leftAux + i, leftAux + i, left + i);
                        }
                        System.arraycopy(aux, 0, array, left + lengthRightAux, aux.length);
                    } else {
                        int[] aux = new int[lengthRightAux];
                        System.arraycopy(array, leftAux, aux, 0, aux.length);
                        System.arraycopy(array, left, array, left + lengthRightAux, lengthLeftAux);
                        System.arraycopy(aux, 0, array, left, aux.length);
                    }
                } else {
                    //close sizes
                    if ((lengthRightAux - lengthLeftAux) < lengthLeftAux) {
                        int[] aux = new int[lengthRightAux - lengthLeftAux];
                        System.arraycopy(array, right - aux.length, aux, 0, aux.length);
                        for (int i=0; i < lengthLeftAux; i++) {
                            swap3(array, leftAux - 1 -i, leftAux + lengthLeftAux - 1 - i, right -1 -i ,  leftAux -1 -i);
                        }
                        System.arraycopy(aux, 0, array, left + lengthLeftAux, aux.length);
                    } else {
                        int[] aux = new int[lengthLeftAux];
                        System.arraycopy(array, left, aux, 0, aux.length);
                        System.arraycopy(array, leftAux, array, left, lengthRightAux);
                        System.arraycopy(aux, 0, array, right - aux.length, aux.length);
                    }
                }
                return  left + lengthRightAux;
            }
        }
        return leftAux;
    }

}
