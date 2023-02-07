package com.aldogg.sorter.intType;

import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.IntSection;
import com.aldogg.sorter.IntSectionsInfo;

import static com.aldogg.sorter.BitSorterUtils.getKeySN;
import static com.aldogg.sorter.intType.IntSorterUtils.ShortSorter.*;
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


    public enum ShortSorter {StableByte, StableBit, CountSort}

    /**
     * Based on BasicTest.smallListAlgorithmSpeedTest
     */
    static ShortSorter[][] shortSorters = new ShortSorter[][]
            {
                    {StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableByte, CountSort, CountSort, StableBit,},
                    {StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableBit, CountSort, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, CountSort, CountSort, StableByte, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte,},

            };


    public static int binlog( int bits ) // returns 0 for bits=0
    {
        int log = 0;
        if( ( bits & 0xffff0000 ) != 0 ) { bits >>>= 16; log = 16; }
        if( bits >= 256 ) { bits >>>= 8; log += 8; }
        if( bits >= 16  ) { bits >>>= 4; log += 4; }
        if( bits >= 4   ) { bits >>>= 2; log += 2; }
        return log + ( bits >>> 1 );
    }

    public static void sortShortK(final int[] array, final int start, final int end, final int[] kList, final int kIndex) {
        int km1 = (kList.length - kIndex) - 1; //K
        int log2Nm1 = binlog(end - start) - 1; //Log2(N)
        ShortSorter sorter;
        if (log2Nm1 > 15) {
            sorter = CountSort;
        } else {
            sorter = shortSorters[km1][log2Nm1];
        }
        executeSorter(array, start, end, kList, kIndex, sorter);
    }

    public static void sortShortKOld(final int[] array, final int start, final int end, final int[] kList, final int kIndex) {
        int kDiff = kList.length - kIndex; //K
        int n = end - start; //N
        int twoPowerK = 1 << kDiff;
        ShortSorter sorter;
        if (twoPowerK <= 16) { //16
            if (n >= twoPowerK * 128) {
                sorter = CountSort;
            } else if (n >= 32) {
                sorter = StableByte;
            } else {
                sorter = StableBit;
            }
        } else if (twoPowerK <= 512) { //512
            if (n >= twoPowerK * 16) {
                sorter = CountSort;
            } else if (n >= 32) {
                sorter = StableByte;
            } else {
                sorter = StableBit;
            }
        } else {
            if (n >= twoPowerK * 2) {
                sorter = CountSort;
            } else if (n >= 128) {
                sorter = StableByte;
            } else {
                sorter = StableBit;
            }
        }

        executeSorter(array, start, end, kList, kIndex, sorter);
    }


    private static void executeSorter(int[] array, int start, int end, int[] kList, int kIndex, ShortSorter sorter) {
        int n = end - start;
        if (sorter.equals(CountSort)) {
            IntCountSort.countSort(array, start, end, kList, kIndex);
        } else if (sorter.equals(StableByte)) {
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
