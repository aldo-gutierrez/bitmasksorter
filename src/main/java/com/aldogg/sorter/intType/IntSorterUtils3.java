package com.aldogg.sorter.intType;

import static com.sun.tools.javac.jvm.ByteCodes.swap;

/**
 *
 */
public class IntSorterUtils3 {

    public static void swap3(final int[] list, final int left, final int right, final int nPosLeft, final  int nPosRight) {
        int auxL = list[left];
        int auxR = list[right];
        list[nPosLeft] = auxL;
        list[nPosRight] = auxR;
    }

    /**
     * LOOKS O(N^2) but it works 2022 03 27
     * @return
     */
    public static int partitionStableRecursive(final int[] list, final int start, final int end, final int sortMask) {
        int left = start;
        int right = end - 1;

        while (left <= right) {
            if ((list[left] & sortMask) == 0) {
                left++;
            } else {
                while (left + 1 <= right) {
                    if ((list[left+1] & sortMask) == 0) {
                        IntSorterUtils.swap(list, left, left+1);
                        left++;
                    } else {
                        break;
                    }
                }
                break;
            }
        }

        while (left <= right) {
            if ((list[right] & sortMask) != 0) {
                right--;
            } else {
                while (left  <= right - 1) {
                    if ((list[right - 1] & sortMask) != 0) {
                        IntSorterUtils.swap(list, right, right - 1);
                        right--;
                    } else {
                        break;
                    }
                }
                break;
            }
        }

        if (left >= right) {
            if (left == right) {
                return right + 1;
            } else {
                return left;
            }
        } else {
            int leftAux = partitionStableReverseRecursive(list, left, right+1, sortMask);
            return reverseResult(list, left, right+1, leftAux);
        }
    }

    //LOOKS O(N^2)
    public static int partitionStableReverseRecursive(final int[] list, final int start, final int end, final int sortMask) {
        int left = start;
        int right = end - 1;

        while (left <= right) {
            if ((list[left] & sortMask) != 0) {
                left++;
            } else {
                while (left + 1 <= right) {
                    if ((list[left+1] & sortMask) != 0) {
                        IntSorterUtils.swap(list, left, left+1);
                        left++;
                    } else {
                        break;
                    }
                }
                break;
            }
        }

        while (left <= right) {
            if ((list[right] & sortMask) == 0) {
                right--;
            } else {
                while (left  <= right - 1) {
                    if ((list[right - 1] & sortMask) == 0) {
                        IntSorterUtils.swap(list, right, right - 1);
                        right--;
                    } else {
                        break;
                    }
                }
                break;
            }
        }

        if (left >= right) {
            if (left == right) {
                return right + 1;
            } else {
                return left;
            }
        } else {
            int leftAux = partitionStableRecursive(list, left, right+1, sortMask);
            //Reverse Result
            return reverseResult(list, left, right+1, leftAux);
        }
    }

    public static int  reverseResult(int[] list, int left, int right, int leftAux) {
        int lengthLeftAux = leftAux - left;
        int lengthRightAux = right - leftAux;
        boolean mayorLeft = lengthLeftAux > lengthRightAux;
        if (lengthLeftAux != 0 && lengthRightAux != 0) {
            if (lengthLeftAux == lengthRightAux) {
                for (int i=0; i < lengthLeftAux; i++) {
                    IntSorterUtils.swap(list, left + i, leftAux + i);
                }
            } else {
                if (mayorLeft) {
                    //close sizes
                    if ((lengthLeftAux - lengthRightAux) < lengthRightAux) {
                        int aux[] = new int[lengthLeftAux - lengthRightAux];
                        System.arraycopy(list, left, aux, 0, aux.length);
                        for (int i=0; i < lengthRightAux; i++) {
                            swap3(list, leftAux - lengthRightAux + i, leftAux + i, leftAux + i, left + i);
                        }
                        System.arraycopy(aux, 0, list, left + lengthRightAux, aux.length);
                    } else {
                        int aux[] = new int[lengthRightAux];
                        System.arraycopy(list, leftAux, aux, 0, aux.length);
                        System.arraycopy(list, left, list, left + lengthRightAux, lengthLeftAux);
                        System.arraycopy(aux, 0, list, left, aux.length);
                    }
                } else {
                    //close sizes
                    if ((lengthRightAux - lengthLeftAux) < lengthLeftAux) {
                        int aux[] = new int[lengthRightAux - lengthLeftAux];
                        System.arraycopy(list, right - aux.length, aux, 0, aux.length);
                        for (int i=0; i < lengthLeftAux; i++) {
                            swap3(list, leftAux - 1 -i, leftAux + lengthLeftAux - 1 - i, right -1 -i ,  leftAux -1 -i);
                        }
                        System.arraycopy(aux, 0, list, left + lengthLeftAux, aux.length);
                    } else {
                        int aux[] = new int[lengthLeftAux];
                        System.arraycopy(list, left, aux, 0, aux.length);
                        System.arraycopy(list, leftAux, list, left, lengthRightAux);
                        System.arraycopy(aux, 0, list, right - aux.length, aux.length);
                    }
                }
                return  left + lengthRightAux;
            }
        }
        return leftAux;
    }

}
