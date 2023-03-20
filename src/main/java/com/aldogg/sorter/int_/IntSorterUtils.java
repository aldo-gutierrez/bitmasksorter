package com.aldogg.sorter.int_;

import com.aldogg.parallel.ArrayParallelRunner;
import com.aldogg.parallel.ArrayRunnable;
import com.aldogg.parallel.ParallelRunner;
import com.aldogg.sorter.AnalysisResult;
import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.IntSection;
import com.aldogg.sorter.IntSectionsInfo;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.aldogg.sorter.BitSorterUtils.getKeySN;
import static com.aldogg.sorter.int_.IntSorterUtils.ShortSorter.*;
import static com.aldogg.sorter.int_.st.RadixBitSorterInt.radixSort;
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

    public static int partitionNotStableSignBit(final int[] array, final int start, final int end) {
        int left = start;
        int right = end - 1;

        while (left <= right) {
            int element = array[left];
            if (element >= 0) {
                left++;
            } else {
                while (left <= right) {
                    element = array[right];
                    if (element >= 0) {
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


    public static int partitionReverseNotStableSignBit(final int[] array, final int start, final int end) {
        int left = start;
        int right = end - 1;

        while (left <= right) {
            int element = array[left];
            if (element >= 0) {
                while (left <= right) {
                    element = array[right];
                    if (element >= 0) {
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

    public static int partitionStableParallel(final int[] array, final int start, final int end, final int mask, final int[] aux) {
        int med = (start + end) / 2;
        int[] indexes = new int[]{start, 0, end - 1, end - 1};

        ParallelRunner.runTwoRunnable(() -> {
            int zeroStart = indexes[0];
            int oneAuxStart = indexes[1];
            for (int i = start; i < med; ++i) {
                int element = array[i];
                if ((element & mask) == 0) {
                    array[zeroStart] = element;
                    zeroStart++;
                } else {
                    aux[oneAuxStart] = element;
                    oneAuxStart++;
                }
            }
            indexes[0] = zeroStart;
            indexes[1] = oneAuxStart;
        }, med - start, () -> {
            int zeroAuxStart = indexes[2];
            int oneStart = indexes[3];
            for (int i = end - 1; i >= med; --i) {
                int element = array[i];
                if ((element & mask) != 0) {
                    array[oneStart] = element;
                    oneStart--;
                } else {
                    aux[zeroAuxStart] = element;
                    zeroAuxStart--;
                }
            }
            indexes[2] = zeroAuxStart;
            indexes[3] = oneStart;
        }, end - med, 0, 2, null);
        int zeroStart = indexes[0];
        int oneAuxStart = indexes[1];
        int zeroAuxStart = indexes[2];
        int lengthZeros2 = end - 1 - zeroAuxStart;
        System.arraycopy(aux, zeroAuxStart + 1, array, zeroStart, lengthZeros2);
        int lengthOnes2 = oneAuxStart - start;
        System.arraycopy(aux, 0, array, zeroStart + lengthZeros2, lengthOnes2);
        return zeroStart + lengthZeros2;
    }

    public static int partitionStableParallel2(final int[] array, final int start, final int end, final int mask, final int[] aux) {
        int n = end - start;
        int maxThreads = 2; //best number is 5,7 then 4 then 3 then 6,8 then 2
        ArrayParallelRunner.APRParameters parameters = new ArrayParallelRunner.APRParameters(maxThreads);
        final int[][] indexes = new int[maxThreads][];
        ArrayParallelRunner.runInParallel(array, start, end, parameters, new ArrayRunnable<int[]>() {
            @Override
            public int[] map(Object arrayX, int start, int end, int index, final AtomicBoolean stop) {
                int[] array = (int[]) arrayX;
                int left = start;
                int right = start;
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
                int[] result = {start, end, left};
                indexes[index] = result;
                return result;
            }

            @Override
            public int[] reduce(int[] result, int[] partialResult) {
                return null;
            }
        });
        int totalZeroLength = 0;

        for (int i = 0; i < indexes.length; i++) {
            int[] parts = indexes[i];
            int startX = parts[0];
            int leftX = parts[2];
            int zeroLengthX = leftX - startX;
            if (i > 0) {
                System.arraycopy(array, startX, array, totalZeroLength, zeroLengthX);
            }
            totalZeroLength += zeroLengthX;
        }
        int totalOneLength = totalZeroLength;
        for (int i = 0; i < indexes.length; i++) {
            int[] parts = indexes[i];
            int startX = parts[0];
            int endX = parts[1];
            int leftX = parts[2];
            int oneLengthX = endX - leftX;
            System.arraycopy(aux, startX, array, totalOneLength, oneLengthX);
            totalOneLength += oneLengthX;
        }
        return totalZeroLength;
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

    public static int[] partitionStableLastBitsParallel(final int[] array, final int start, final IntSection section, final int[] aux, final int n) {
        final int mask = section.sortMask;
        final int end = start + n;
        final int countLength = 1 << section.length;
        int[] count = ArrayParallelRunner.runInParallel(array, start, end, new ArrayParallelRunner.APRParameters(2) , new ArrayRunnable<int[]>() {
            @Override
            public int[] map(final Object arrayX, final int start, final int end, final int index, final AtomicBoolean stop) {
                int[] array = (int[]) arrayX;
                final int[] count = new int[countLength];
                for (int i = start; i < end; ++i) {
                    count[array[i] & mask]++;
                }
                return count;
            }

            @Override
            public int[] reduce(int[] result, int[] partialResult) {
                for (int i = 0; i < countLength; ++i) {
                    result[i]+=partialResult[i];
                }
                return result;
            }
        });

        int[] left = new int[countLength];
        int[] right = new int[countLength];
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            left[i] = sum;
            sum += countI;
            right[i] = sum - 1;
            count[i] = sum;
        }
        int med = (start + end) /2;
        ParallelRunner.runTwoRunnable(() -> {
            for (int i = start; i < med; ++i) {
                int element = array[i];
                aux[left[element & mask]++] = element;
            }
        }, med - start, () -> {
            for (int i = end - 1; i >= med; --i) {
                int element = array[i];
                aux[right[element & mask]--] = element;
            }
        }, end - med, 0, 2 , null);
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

    public static int[] partitionStableOneGroupBitsParallel(int[] array, int start, IntSection section, int[] aux, int n) {
        final int mask = section.sortMask;
        final int shiftRight = section.shiftRight;
        final int end = start + n;
        final int countLength = 1 << section.length;
        final int[] count = ArrayParallelRunner.runInParallel(array, start, end, new ArrayParallelRunner.APRParameters(2), new ArrayRunnable<int[]>() {
            @Override
            public int[] map(final Object arrayX, final int start, final int end, final int index, final AtomicBoolean stop) {
                int[] array = (int[]) arrayX;
                final int[] count = new int[countLength];
                for (int i = start; i < end; ++i) {
                    count[(array[i] & mask) >>> shiftRight]++;
                }
                return count;
            }

            @Override
            public int[] reduce(int[] result, int[] partialResult) {
                for (int i = 0; i < countLength; ++i) {
                    result[i]+=partialResult[i];
                }
                return result;
            }
        });

        int[] left = new int[countLength];
        int[] right = new int[countLength];
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            left[i] = sum;
            sum += countI;
            right[i] = sum - 1;
            count[i] = sum;
        }
        int med = (start + end) /2;
        ParallelRunner.runTwoRunnable(() -> {
            for (int i = start; i < med; ++i) {
                int element = array[i];
                aux[left[(element & mask) >>> shiftRight]++] = element;
            }
        }, med - start, () -> {
            for (int i = end - 1; i >= med; --i) {
                int element = array[i];
                aux[right[(element & mask) >>> shiftRight]--] = element;
            }
        }, end - med, 0, 2 , null);
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
    static final ShortSorter[][] shortSorters = new ShortSorter[][]
            {
                    {StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableByte, StableBit, StableByte, StableBit,},
                    {StableBit, StableBit, StableBit, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableByte, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableByte, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort, CountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, CountSort,},
            };


    public static void sortShortK(final int[] array, final int start, final int end, final int[] kList, final int kIndex) {
        int km1 = (kList.length - kIndex) - 1; //K
        int log2Nm1 = BitSorterUtils.logBase2(end - start) - 1; //Log2(N)
        ShortSorter sorter;
        if (log2Nm1 <= 15 && km1 <= 15) {
            sorter = shortSorters[km1][log2Nm1];
        } else if (km1 <= 15) {
            sorter = CountSort;
        } else {
            sorter = StableByte;
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
