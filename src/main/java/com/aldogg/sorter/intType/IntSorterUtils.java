package com.aldogg.sorter.intType;

import com.aldogg.sorter.Section;

import static com.aldogg.sorter.BitSorterUtils.getKeySN;
import static com.aldogg.sorter.intType.st.RadixBitSorterInt.radixSort;

public class IntSorterUtils {

    public static void swap(final int[] array, final int left, final int right) {
        int auxS = array[left];
        array[left] = array[right];
        array[right] = auxS;
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
    public static void partitionStableLastBits(final int[] array, final int start, final Section section, int[] leftX, int[] count, final int[] aux, int startAux, int n) {
        int mask = section.sortMask;
        int end = start + n;
        for (int i = start; i < end; i++) {
            count[array[i] & mask]++;
        }
        for (int i = 1; i < count.length; i++) {
            leftX[i] = leftX[i - 1] + count[i - 1];
        }
        if (startAux == 0) {
            for (int i = start; i < end; i++) {
                int element = array[i];
                int elementShiftMasked = element & mask;
                aux[leftX[elementShiftMasked]] = element;
                leftX[elementShiftMasked]++;
            }
        } else {
            for (int i = start; i < end; i++) {
                int element = array[i];
                int elementShiftMasked = element & mask;
                aux[leftX[elementShiftMasked] + startAux] = element;
                leftX[elementShiftMasked]++;
            }
        }
    }


    /**
     * CPU: O(N), 3*N + 2^K
     * MEM: O(N), N + 2*2^K
     */
    public static void partitionStableOneGroupBits(final int[] array, final int start, final Section section, final int[] leftX, final int[] count, final int[] aux, int startAux, int n) {
        int mask = section.sortMask;
        int shiftRight = section.shiftRight;
        int end = start + n;
        for (int i = start; i < end; i++) {
            count[(array[i] & mask) >>> shiftRight]++;
        }
        for (int i = 1; i < count.length; i++) {
            leftX[i] = leftX[i - 1] + count[i - 1];
        }
        if (startAux == 0) {
            for (int i = start; i < end; i++) {
                int element = array[i];
                int elementShiftMasked = (element & mask) >>> shiftRight;
                aux[leftX[elementShiftMasked]] = element;
                leftX[elementShiftMasked]++;
            }
        } else {
            for (int i = start; i < end; i++) {
                int element = array[i];
                int elementShiftMasked = (element & mask) >>> shiftRight;
                aux[leftX[elementShiftMasked] + startAux] = element;
                leftX[elementShiftMasked]++;
            }
        }
    }

    public static void partitionStableNGroupBits(final int[] array, final int start, Section[] sections, final int[] leftX, final int[] count, final int[] aux, int startAux, int n) {
        int end = start + n;
        for (int i = start; i < end; i++) {
            int element = array[i];
            int elementMaskedShifted = getKeySN(element, sections);
            count[elementMaskedShifted]++;
        }
        for (int i = 1; i < count.length; i++) {
            leftX[i] = leftX[i - 1] + count[i - 1];
        }
        if (startAux == 0) {
            for (int i = start; i < end; i++) {
                int element = array[i];
                int elementMaskedShifted = getKeySN(element, sections);
                aux[leftX[elementMaskedShifted]] = element;
                leftX[elementMaskedShifted]++;
            }
        } else {
            for (int i = start; i < end; i++) {
                int element = array[i];
                int elementMaskedShifted = getKeySN(element, sections);
                aux[leftX[elementMaskedShifted] + startAux] = element;
                leftX[elementMaskedShifted]++;
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


    enum ShortSorter {StableByte, StableBit, CountSort}

    /**
     * Based on BasicTest.smallListAlgorithmSpeedTest
     */
    public static void sortShortKNew(final int[] array, final int start, final int end, final int[] kList, final int kIndex) {
        int k = kList.length - kIndex; //K
        int n = end - start; //N
        ShortSorter sorter;
        if (k >= 16) {
            sorter = ShortSorter.StableByte;
        } else if (k <= 1) {
            sorter = ShortSorter.StableBit;
        } else { //k2 - k15
            if (n >= 32768) {
                sorter = ShortSorter.CountSort;
            } else {
                if (k >= 14) {
                    sorter = ShortSorter.StableByte;
                } else {
                    if (n >= 8192) {
                        sorter = ShortSorter.CountSort;
                    } else {
                        if (k >= 12) {
                            sorter = ShortSorter.StableByte;
                        } else {
                            if (n >= 512) {
                                sorter = ShortSorter.CountSort;
                            } else {
                                if (k <= 2) {
                                    if (n >= 64) {
                                        sorter = ShortSorter.CountSort;
                                    } else {
                                        sorter = ShortSorter.StableBit;
                                    }
                                } else if (k >= 10) {
                                    if (n >= 64) {
                                        sorter = ShortSorter.CountSort;
                                    } else {
                                        sorter = ShortSorter.StableBit;
                                    }
                                } else if (k <= 6) {
                                    if (n >= 128) {
                                        sorter = ShortSorter.CountSort;
                                    } else {
                                        sorter = ShortSorter.StableByte;
                                    }
                                } else {
                                    if (n >= 32) {
                                        sorter = ShortSorter.StableByte;
                                    } else {
                                        sorter = ShortSorter.CountSort;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        executeSorter(array, start, end, kList, kIndex, sorter);
    }

    /**
     * Based on BasicTest.smallListAlgorithmSpeedTest
     */
    public static void sortShortK(final int[] array, final int start, final int end, final int[] kList, final int kIndex) {
        int kDiff = kList.length - kIndex; //K
        int n = end - start; //N
        int twoPowerK = 1 << kDiff;
        ShortSorter sorter;
        if (twoPowerK <= 16) { //16
            if (n >= twoPowerK*128) {
                sorter = ShortSorter.CountSort;
            } else if (n >=32 ){
                sorter = ShortSorter.StableByte;
            } else {
                sorter = ShortSorter.StableBit;
            }
        } else  if (twoPowerK <= 512) { //512
            if (n >= twoPowerK*16) {
                sorter = ShortSorter.CountSort;
            } else if (n >=32 ){
                sorter = ShortSorter.StableByte;
            } else {
                sorter = ShortSorter.StableBit;
            }
        } else {
            if (n >= twoPowerK*2) {
                sorter = ShortSorter.CountSort;
            } else if (n >=128 ){
                sorter = ShortSorter.StableByte;
            } else {
                sorter = ShortSorter.StableBit;
            }
        }

        executeSorter(array, start, end, kList, kIndex, sorter);
    }


    private static void executeSorter(int[] array, int start, int end, int[] kList, int kIndex, ShortSorter sorter) {
        int n = end - start;
        if (sorter.equals(ShortSorter.CountSort)) {
            IntCountSort.countSort(array, start, end, kList, kIndex);
        } else if (sorter.equals(ShortSorter.StableByte)) {
            int[] aux = new int[n];
            radixSort(array, start, end, kList, kIndex, kList.length - 1, aux);
        } else {
            int[] aux = new int[n];
            for (int i = kList.length - 1; i >= kIndex; i--) {
                int sortMask = 1 << kList[i];
                IntSorterUtils.partitionStable(array, start, end, sortMask, aux);
            }
        }
    }
}
