package com.aldogg.sorter.int_;

import com.aldogg.parallel.ArrayParallelRunner;
import com.aldogg.parallel.ArrayRunnable;
import com.aldogg.parallel.ParallelRunner;
import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.shared.Section;
import com.aldogg.sorter.shared.ShortSorter;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.aldogg.sorter.int_.SorterUtilsInt.partitionStable;
import static com.aldogg.sorter.int_.SorterUtilsInt.swap;
import static com.aldogg.sorter.int_.st.RadixBitSorterInt.radixSort;
import static com.aldogg.sorter.shared.ShortSorter.*;

public class SorterUtilsIntExt {

    public static int partitionNotStableUpperBit(final int[] array, final int start, final int endP1) {
        int left = start;
        int right = endP1 - 1;

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

    public static int partitionReverseNotStableUpperBit(final int[] array, final int start, final int endP1) {
        int left = start;
        int right = endP1 - 1;

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


    public static int partitionStableParallel(final int[] array, final int start, final int endP1, final int mask, final int[] aux) {
        int med = (start + endP1) / 2;
        int[] indexes = new int[]{start, 0, endP1 - 1, endP1 - 1};

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
            for (int i = endP1 - 1; i >= med; --i) {
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
        }, endP1 - med, 0, 2);
        int zeroStart = indexes[0];
        int oneAuxStart = indexes[1];
        int zeroAuxStart = indexes[2];
        int lengthZeros2 = endP1 - 1 - zeroAuxStart;
        System.arraycopy(aux, zeroAuxStart + 1, array, zeroStart, lengthZeros2);
        int lengthOnes2 = oneAuxStart - start;
        System.arraycopy(aux, 0, array, zeroStart + lengthZeros2, lengthOnes2);
        return zeroStart + lengthZeros2;
    }

    public static int partitionStableParallel2(final int[] array, final int start, final int endP1, final int mask, final int[] aux) {
        int n = endP1 - start;
        int maxThreads = 2; //best number is 5,7 then 4 then 3 then 6,8 then 2
        ArrayParallelRunner.APRParameters parameters = new ArrayParallelRunner.APRParameters(maxThreads);
        final int[][] indexes = new int[maxThreads][];
        ArrayParallelRunner.runInParallel(array, start, endP1, parameters, new ArrayRunnable<int[]>() {
            @Override
            public int[] map(Object arrayX, int start, int endP1, int index, final AtomicBoolean stop) {
                int[] array = (int[]) arrayX;
                int left = start;
                int right = start;
                for (int i = start; i < endP1; ++i) {
                    int element = array[i];
                    if ((element & mask) == 0) {
                        array[left] = element;
                        left++;
                    } else {
                        aux[right] = element;
                        right++;
                    }
                }
                int[] result = {start, endP1, left};
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

    public static int[] partitionStableLastBitsParallel(final int[] array, final int start, final Section section, final int[] aux, final int n) {
        final int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
        final int endP1 = start + n;
        final int countLength = 1 << section.bits;
        int[] count = ArrayParallelRunner.runInParallel(array, start, endP1, ArrayParallelRunner.APR_PARAMETERS_TWO_THREADS, new ArrayRunnable<int[]>() {
            @Override
            public int[] map(final Object arrayX, final int start, final int endP1, final int index, final AtomicBoolean stop) {
                int[] array = (int[]) arrayX;
                final int[] count = new int[countLength];
                for (int i = start; i < endP1; ++i) {
                    count[array[i] & mask]++;
                }
                return count;
            }

            @Override
            public int[] reduce(int[] result, int[] partialResult) {
                for (int i = 0; i < countLength; ++i) {
                    result[i] += partialResult[i];
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
        int med = (start + endP1) / 2;
        ParallelRunner.runTwoRunnable(() -> {
            for (int i = start; i < med; ++i) {
                int element = array[i];
                aux[left[element & mask]++] = element;
            }
        }, med - start, () -> {
            for (int i = endP1 - 1; i >= med; --i) {
                int element = array[i];
                aux[right[element & mask]--] = element;
            }
        }, endP1 - med, 0, 2);
        return count;
    }

    public static int[] partitionStableOneGroupBitsParallel(int[] array, int start, Section section, int[] aux, int n) {
        final int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
        final int shiftRight = section.shift;
        final int endP1 = start + n;
        final int countLength = 1 << section.bits;
        final int[] count = ArrayParallelRunner.runInParallel(array, start, endP1, ArrayParallelRunner.APR_PARAMETERS_TWO_THREADS, new ArrayRunnable<int[]>() {
            @Override
            public int[] map(final Object arrayX, final int start, final int endP1, final int index, final AtomicBoolean stop) {
                int[] array = (int[]) arrayX;
                final int[] count = new int[countLength];
                for (int i = start; i < endP1; ++i) {
                    count[(array[i] & mask) >>> shiftRight]++;
                }
                return count;
            }

            @Override
            public int[] reduce(int[] result, int[] partialResult) {
                for (int i = 0; i < countLength; ++i) {
                    result[i] += partialResult[i];
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
        int med = (start + endP1) / 2;
        ParallelRunner.runTwoRunnable(() -> {
            for (int i = start; i < med; ++i) {
                int element = array[i];
                aux[left[(element & mask) >>> shiftRight]++] = element;
            }
        }, med - start, () -> {
            for (int i = endP1 - 1; i >= med; --i) {
                int element = array[i];
                aux[right[(element & mask) >>> shiftRight]--] = element;
            }
        }, endP1 - med, 0, 2);
        return count;
    }

    /**
     * Based on BasicTest.smallListAlgorithmSpeedTest
     */
    static final ShortSorter[][] shortSorters = new ShortSorter[][]
            {
                    {StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableBit, StableByte, StableBit, StableByte, StableBit,},
                    {StableBit, StableBit, StableBit, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort,},
                    {StableBit, StableBit, StableBit, StableByte, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort,},
                    {StableBit, StableBit, StableBit, StableByte, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort,},
                    {StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort,},
                    {StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort,},
                    {StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort,},
                    {StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort,},
                    {StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, PCountSort, PCountSort, PCountSort, PCountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, PCountSort, PCountSort, PCountSort, PCountSort, PCountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, PCountSort, PCountSort, PCountSort, PCountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, PCountSort, PCountSort, PCountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, PCountSort, PCountSort,},
                    {StableBit, StableBit, StableBit, StableBit, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, StableByte, PCountSort,},
            };


    public static void sortShortK(final int[] array, final int start, final int endP1, final int[] bList, final int bListIndex) {
        int bLengthM1 = (bList.length - bListIndex) - 1; //Log2(K)
        int log2Nm1 = BitSorterUtils.logBase2(endP1 - start) - 1; //Log2(N)
        ShortSorter sorter;
        if (log2Nm1 <= 15 && bLengthM1 <= 15) {
            sorter = shortSorters[bLengthM1][log2Nm1];
        } else if (bLengthM1 <= 15) {
            sorter = PCountSort;
        } else {
            sorter = StableByte;
        }
        executeSorter(array, start, endP1, bList, bListIndex, sorter);
    }

    private static void executeSorter(int[] array, int start, int endP1, int[] bList, int bListIndex, ShortSorter sorter) {
        int n = endP1 - start;
        if (sorter.equals(PCountSort)) {
            PCountSortInt.pCountSort(array, start, endP1, bList, bListIndex);
        } else if (sorter.equals(StableByte)) {
            int[] aux = new int[n];
            radixSort(array, start, endP1, bList, bListIndex, bList.length - 1, aux, 0);
        } else {
            int[] aux = new int[n];
            for (int i = bList.length - 1; i >= bListIndex; i--) {
                int sortMask = 1 << bList[i];
                partitionStable(array, start, endP1, sortMask, aux);
            }
        }
    }

}
