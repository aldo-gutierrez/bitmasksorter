package com.aldogg.sorter.intType;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.IntSection;
import com.aldogg.sorter.IntSectionsInfo;

import static com.aldogg.sorter.BitSorterUtils.getKeySN;
import static com.aldogg.sorter.intType.st.RadixBitSorterInt.radixSort;
import static java.lang.Integer.MIN_VALUE;

public class IntSorterUtils {

    public static void swap(final int[] array, final int left, final int right) {
        int auxS = array[left];
        array[left] = array[right];
        array[right] = auxS;
    }

    /**
     * CPU: O(N)
     * MEM: O(1)
     */
    public static int partitionNotStable(final int[] array, final int start, final int end, final int mask) {
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
     * CPU: O(N)
     * MEM: O(1)
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
     * CPU: O(N), N + (0.R)*N
     * MEM: O(N), N
     */
    public static int partitionStable(final int[] array, final int start, final int end, final int mask, final int[] aux) {
        int left = start;
        int right = 0;
        for (int i = start; i < end; ++i) {
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


    public static void partitionStableLastBits(final int[] array, final int start, final IntSection section, final int[] aux, final int startAux, final int n) {
        final int mask = section.sortMask;
        final int end = start + n;
        final int countLength = 1 << section.length;
        final int[] count = new int[countLength];
        for (int i = start; i < end; ++i) {
            count[array[i] & mask]++;
        }
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < end; ++i) {
            int element = array[i];
            aux[count[element & mask]++ + startAux] = element;
        }
    }

    public static int[] partitionStableLastBits(final int[] array, final int start, final IntSection section, final int[] aux, final int n) {
        final int mask = section.sortMask;
        final int end = start + n;
        final int countLength = 1 << section.length;
        final int[] count = new int[countLength];
        for (int i = start; i < end; ++i) {
            count[array[i] & mask]++;
        }
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < end; ++i) {
            int element = array[i];
            aux[count[element & mask]++] = element;
        }
        return count;
    }


    public static int[] partitionStableOneGroupBits(final int[] array, final int start, final IntSection section, final int[] aux, int startAux, int n) {
        final int mask = section.sortMask;
        final int shiftRight = section.shiftRight;
        final int end = start + n;
        final int countLength = 1 << section.length;
        final int[] count = new int[countLength];
        for (int i = start; i < end; ++i) {
            count[(array[i] & mask) >>> shiftRight]++;
        }
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < end; ++i) {
            int element = array[i];
            aux[count[(element & mask) >>> shiftRight]++ + startAux] = element;
        }
        return count;
    }

    public static int[] partitionStableOneGroupBits(final int[] array, final int start, final IntSection section, final int[] aux, int n) {
        final int mask = section.sortMask;
        final int shiftRight = section.shiftRight;
        final int end = start + n;
        final int countLength = 1 << section.length;
        final int[] count = new int[countLength];
        for (int i = start; i < end; ++i) {
            count[(array[i] & mask) >>> shiftRight]++;
        }
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < end; ++i) {
            int element = array[i];
            aux[count[(element & mask) >>> shiftRight]++] = element;
        }
        return count;
    }

    public static int[] partitionStableNGroupBits(final int[] array, final int start, IntSectionsInfo sectionsInfo, final int[] aux, int n) {
        final IntSection[] sections = sectionsInfo.sections;
        final int end = start + n;
        final int countLength = 1 << sectionsInfo.totalLength;
        final int[] count = new int[countLength];
        for (int i = start; i < end; ++i) {
            int element = array[i];
            count[getKeySN(element, sections)]++;
        }
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        for (int i = start; i < end; ++i) {
            int element = array[i];
            aux[count[getKeySN(element, sections)]++] = element;
        }
        return count;
    }


    public static void reverse(final int[] array, final int start, final int end) {
        int length = end - start;
        int ld2 = length / 2;
        int endL1 = end - 1;
        for (int i = 0; i < ld2; ++i) {
            swap(array, start + i, endL1 - i);
        }
    }

    public static int listIsOrderedSigned(final int[] array, final int start, final int end) {
        int i1 = array[start];
        int i = start + 1;
        while (i < end) {
            int i2 = array[i];
            if (i2 != i1) {
                break;
            }
            ++i;
        }
        if (i == end) {
            return AnalysisResult.ALL_EQUAL;
        }

        //ascending
        i1 = array[i];
        if (array[i - 1] < i1) {
            ++i;
            for (; i < end; ++i) {
                int i2 = array[i];
                if (i1 > i2) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                return AnalysisResult.ASCENDING;
            }
        }
        //descending
        else {
            ++i;
            for (; i < end; ++i) {
                int i2 = array[i];
                if (i1 < i2) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                return AnalysisResult.DESCENDING;
            }
        }
        return AnalysisResult.UNORDERED;
    }

    public static int listIsOrderedUnSigned(int[] array, int start, int end) {
        int i1 = array[start];
        int i = start + 1;
        while (i < end) {
            int i2 = array[i];
            if (i2 != i1) {
                break;
            }
            ++i;
        }
        if (i == end) {
            return AnalysisResult.ALL_EQUAL;
        }

        //ascending
        i1 = array[i];
        if (array[i - 1] < i1) {
            ++i;
            for (; i < end; ++i) {
                int i2 = array[i];
                if (i1 + MIN_VALUE > i2 + MIN_VALUE) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                return AnalysisResult.ASCENDING;
            }
        }
        //descending
        else {
            ++i;
            for (; i < end; ++i) {
                int i2 = array[i];
                if (i1 + MIN_VALUE < i2 + MIN_VALUE) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                return AnalysisResult.DESCENDING;
            }
        }
        return AnalysisResult.UNORDERED;
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
            if (n >= twoPowerK * 128) {
                sorter = ShortSorter.CountSort;
            } else if (n >= 32) {
                sorter = ShortSorter.StableByte;
            } else {
                sorter = ShortSorter.StableBit;
            }
        } else if (twoPowerK <= 512) { //512
            if (n >= twoPowerK * 16) {
                sorter = ShortSorter.CountSort;
            } else if (n >= 32) {
                sorter = ShortSorter.StableByte;
            } else {
                sorter = ShortSorter.StableBit;
            }
        } else {
            if (n >= twoPowerK * 2) {
                sorter = ShortSorter.CountSort;
            } else if (n >= 128) {
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
                partitionStable(array, start, end, sortMask, aux);
            }
        }
    }
}
