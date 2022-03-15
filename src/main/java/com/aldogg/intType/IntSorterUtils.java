package com.aldogg.intType;

import com.aldogg.BitSorterUtils;

import static com.aldogg.BitSorterUtils.twoPowerX;
import static com.aldogg.RadixBitSorterInt.radixSort;

public class IntSorterUtils {

    public static boolean compareAndSwapSigned(final int[] list, final int left, final int right) {
        if ((list[left] > list[right])) {
            swap(list, left, right);
            return true;
        }
        return false;
    }

    public static boolean compareAndSwapUnsigned(final int[] list, final int left, final int right) {
        if (Integer.compareUnsigned(list[left], list[right]) ==  1) {
            swap(list, left, right);
            return true;
        }
        return false;
    }

    public static void swap(final int[] list, final int left, final int right) {
        int aux = list[left];
        list[left] = list[right];
        list[right] = aux;
    }

    /**
     *   partition with 0 memory in-place
     *   CPU: N
     *   MEM: 1
     *   not stable?
     */
    public static int partitionNotStable(final int[] list, final  int start, final int end, final int sortMask) {
        int left = start;
        int right = end - 1;

        for (; left <= right; ) {
            int element = list[left];
            if ((element & sortMask) == 0) {
                left++;
            } else {
                for (; left <= right; ) {
                    element = list[right];
                    if ((element & sortMask) == 0) {
                        IntSorterUtils.swap(list, left, right);
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
     *   partition with 0 memory in-place reverse order
     *   CPU: N
     *   MEM: 1
     *   not stable?
     */
    public static int partitionReverseNotStable(final int[] list, final int start, final int end, final int sortMask) {
        int left = start;
        int right = end - 1;

        for (; left <= right; ) {
            int element = list[left];
            if ((element & sortMask) == 0) {
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
            } else {
                left++;
            }
        }
        return left;
    }


    /**
     *  stable partition with aux memory, only copies right to aux for better performance
     *  CPU: 2*N*K (K=1 for 1 bit)
     *  MEM: N
     */
    public static int partitionStable(final int[] list, final int start, final int end, final int sortMask, int[] aux) {
        int left = start;
        int right = 0;
        for (int i = start; i < end; i++) {
            int element = list[i];
            if ((element & sortMask) == 0) {
                list[left] = element;
                left++;
            } else {
                aux[right] = element;
                right++;
            }
        }
        System.arraycopy(aux, 0, list, left, right);
        return left;
    }

    /**
     *  CPU: 3*N + 2^K
     *  MEM: N + 2*2^K
     */
    public static void partitionStableLastBits(int[] list, int start, int end, int lengthBitsToNumber, int[] aux, int sortMask) {
        int[] leftX = new int[lengthBitsToNumber];
        int[] count = new int[lengthBitsToNumber];
        for (int i = start; i < end; i++) {
            int elementShiftMasked = list[i] & sortMask;
            count[elementShiftMasked]++;
        }
        for (int i = 1; i < lengthBitsToNumber; i++) {
            leftX[i] = leftX[i - 1] + count[i - 1];
        }
        for (int i = start; i < end; i++) {
            int element = list[i];
            int elementShiftMasked = element & sortMask;
            aux[leftX[elementShiftMasked]] = element;
            leftX[elementShiftMasked]++;
        }
        System.arraycopy(aux, 0, list, start, end - start);
    }

    /**
     *  CPU: 3*N + 2^K
     *  MEM: N + 2*2^K
     */
    public static void partitionStableGroupBits(int[] list, int start, int end, int lengthBitsToNumber, int[] aux, int sortMask, int shiftRight) {
        int[] leftX = new int[lengthBitsToNumber];
        int[] count = new int[lengthBitsToNumber];
        for (int i = start; i < end; i++) {
            int element = list[i];
            int elementShiftMasked = (element & sortMask) >>> shiftRight;
            count[elementShiftMasked]++;
        }
        for (int i = 1; i < lengthBitsToNumber; i++) {
            leftX[i] = leftX[i - 1] + count[i - 1];
        }
        for (int i = start; i < end; i++) {
            int element = list[i];
            int elementShiftMasked = (element & sortMask) >>> shiftRight;
            aux[leftX[elementShiftMasked]] = element;
            leftX[elementShiftMasked]++;
        }
        System.arraycopy(aux, 0, list, start, end - start);
    }

    public static void sortShortList(int[] list, int start, int end, int[] kList, int kIndex) {
        int kDiff = kList.length - kIndex; //K
        int listLength = end - start; //N
        int twoPK = twoPowerX(kDiff);
        if (twoPK <= 16) { //16
            if (listLength >= twoPK*128) {
                CountSort.countSort(list, start, end, kList, kIndex);
            } else if (listLength >=32 ){
                int[] aux = new int[listLength];
                radixSort(list, start, end, aux, kList, kList.length - 1, 0);
            } else {
                int[] aux = new int[listLength];
                for (int i = kList.length - 1; i >= kIndex; i--) {
                    int sortMask = BitSorterUtils.getMaskBit(kList[i]);
                    IntSorterUtils.partitionStable(list, start, end, sortMask, aux);
                }
            }
        } else  if (twoPK <= 512) { //512
            if (listLength >= twoPK*16) {
                CountSort.countSort(list, start, end, kList, kIndex);
            } else if (listLength >=32 ){
                int[] aux = new int[listLength];
                radixSort(list, start, end, aux, kList, kList.length - 1, 0);
            } else {
                int[] aux = new int[listLength];
                for (int i = kList.length - 1; i >= kIndex; i--) {
                    int sortMask = BitSorterUtils.getMaskBit(kList[i]);
                    IntSorterUtils.partitionStable(list, start, end, sortMask, aux);
                }
            }
        } else {
            if (listLength >= twoPK*2) {
                CountSort.countSort(list, start, end, kList, kIndex);
            } else if (listLength >=128 ){
                int[] aux = new int[listLength];
                radixSort(list, start, end, aux, kList, kList.length - 1, 0);
            } else {
                int[] aux = new int[listLength];
                for (int i = kList.length - 1; i >= kIndex; i--) {
                    int sortMask = BitSorterUtils.getMaskBit(kList[i]);
                    IntSorterUtils.partitionStable(list, start, end, sortMask, aux);
                }
            }
        }
    }

}
