package com.aldogg.sorter.intType;

import com.aldogg.sorter.BitSorterUtils;

import static com.aldogg.sorter.BitSorterUtils.twoPowerX;
import static com.aldogg.sorter.RadixBitSorterInt.radixSort;

public class IntSorterUtils {

    public static void compareAndSwapSigned(final int[] array, final int left, final int right) {
        int aL = array[left];
        int aR = array[right];
        if (aL > aR) {
            array[left] = aR;
            array[right] = aL;
        }
    }

    public static void compareAndSwapUnsigned(final int[] array, final int left, final int right) {
        int aL = array[left];
        int aR = array[right];
        if (aL + 0x80000000 > aR + 0x80000000) { //if (aL + Integer.MIN_VALUE > aR + Integer.MIN_VALUE) {
            array[left] = aR;
            array[right] = aL;
        }
    }

    public static void swap(final int[] array, final int left, final int right) {
        int aux = array[left];
        array[left] = array[right];
        array[right] = aux;
    }

    /**
     *   CPU: O(N)
     *   MEM: O(1)
     */
    public static int partitionNotStable(final int[] array, final  int start, final int end, final int mask) {
        int left = start;
        int right = end - 1;

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

    /**
     *   CPU: O(N)
     *   MEM: O(1)
     */
    public static int partitionReverseNotStable(final int[] array, final int start, final int end, final int mask) {
        int left = start;
        int right = end - 1;

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


    /**
     *  CPU: O(N), N + (0.R)*N
     *  MEM: O(N), N
     */
    public static int partitionStable(final int[] array, final int start, final int end, final int mask, final int[] aux) {
        int left = start;
        int right = 0;
        for (int i = start; i < end; i++) {
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



    /**
     *  CPU: O(N), 3*N + 2^K
     *  MEM: O(N), N + 2*2^K
     */
    public static void partitionStableLastBits(final int[] array, final int start, final int end, final int mask, final int twoPowerK, final int[] aux) {
        int[] leftX = new int[twoPowerK];
        int[] count = new int[twoPowerK];
        for (int i = start; i < end; i++) {
            count[array[i] & mask]++;
        }
        for (int i = 1; i < twoPowerK; i++) {
            leftX[i] = leftX[i - 1] + count[i - 1];
        }
        for (int i = start; i < end; i++) {
            int element = array[i];
            int elementShiftMasked = element & mask;
            aux[leftX[elementShiftMasked]] = element;
            leftX[elementShiftMasked]++;
        }
        System.arraycopy(aux, 0, array, start, end - start);
    }

    /**
     *  CPU: O(N), 3*N + 2^K
     *  MEM: O(N), N + 2*2^K
     */
    public static void partitionStableGroupBits(final int[] array, final int start, final int end, final int mask, final int shiftRight, final int twoPowerK, final int[] aux) {
        int[] leftX = new int[twoPowerK];
        int[] count = new int[twoPowerK];
        for (int i = start; i < end; i++) {
            count[(array[i] & mask) >> shiftRight]++;
        }
        for (int i = 1; i < twoPowerK; i++) {
            leftX[i] = leftX[i - 1] + count[i - 1];
        }
        for (int i = start; i < end; i++) {
            int element = array[i];
            int elementShiftMasked = (element & mask) >> shiftRight;
            aux[leftX[elementShiftMasked]] = element;
            leftX[elementShiftMasked]++;
        }
        System.arraycopy(aux, 0, array, start, end - start);
    }

    public static void sortShortKList(final int[] array, final int start, final int end, final int[] kList, final int kIndex) {
        int kDiff = kList.length - kIndex; //K
        int listLength = end - start; //N
        int twoPowerK = twoPowerX(kDiff);
        if (twoPowerK <= 16) { //16
            if (listLength >= twoPowerK*128) {
                CountSort.countSort(array, start, end, kList, kIndex);
            } else if (listLength >=32 ){
                int[] aux = new int[listLength];
                radixSort(array, start, end, kList, kList.length - 1, 0, aux);
            } else {
                int[] aux = new int[listLength];
                for (int i = kList.length - 1; i >= kIndex; i--) {
                    int sortMask = BitSorterUtils.getMaskBit(kList[i]);
                    IntSorterUtils.partitionStable(array, start, end, sortMask, aux);
                }
            }
        } else  if (twoPowerK <= 512) { //512
            if (listLength >= twoPowerK*16) {
                CountSort.countSort(array, start, end, kList, kIndex);
            } else if (listLength >=32 ){
                int[] aux = new int[listLength];
                radixSort(array, start, end, kList, kList.length - 1, 0, aux);
            } else {
                int[] aux = new int[listLength];
                for (int i = kList.length - 1; i >= kIndex; i--) {
                    int sortMask = BitSorterUtils.getMaskBit(kList[i]);
                    IntSorterUtils.partitionStable(array, start, end, sortMask, aux);
                }
            }
        } else {
            if (listLength >= twoPowerK*2) {
                CountSort.countSort(array, start, end, kList, kIndex);
            } else if (listLength >=128 ){
                int[] aux = new int[listLength];
                radixSort(array, start, end, kList, kList.length - 1, 0, aux);
            } else {
                int[] aux = new int[listLength];
                for (int i = kList.length - 1; i >= kIndex; i--) {
                    int sortMask = BitSorterUtils.getMaskBit(kList[i]);
                    IntSorterUtils.partitionStable(array, start, end, sortMask, aux);
                }
            }
        }
    }

    public static void reverse(final int[] array, final int start, final int end) {
        int length = end - start;
        int end2 = start + length / 2;
        for (int i = start; i < end2; i++) {
            swap(array, i, end - i - 1);
        }
    }

        /*
        int[] maskParts = ArrayThreadRunner.runInParallel(list, start, end, 2, new ArrayRunnable<int[]>() {
            @Override
            public int[] map(int[] list, int start, int end) {
                return getMask(list, start, end);
            }

            @Override
            public int[] reduce(int[] m1, int[] m2) {
                return new int[]{m1[0] | m2[0], m1[1] | m2[1]};
            }
        });
        */

}
