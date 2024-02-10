package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.FieldSorterOptions;
import com.aldogg.sorter.shared.OrderAnalysisResult;
import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;
import com.aldogg.sorter.shared.Section;
import com.aldogg.sorter.int_.BitMaskSorterInt;
import com.aldogg.sorter.int_.SorterUtilsInt;

import static com.aldogg.sorter.int_.SorterUtilsInt.listIsOrderedSigned;
import static com.aldogg.sorter.int_.SorterUtilsInt.listIsOrderedUnSigned;

/**
 * RadixByteSorter Version 2
 * that:
 * inlined some code
 * calculates all counts first to use linear memory
 * Sometimes is faster than V1, sometimes is slower
 *
 * TODO
 *  go backwards for s0, s16 as the data could be already in the cpu cache. this could be faster
 */
public class RadixByteSorterV2Int extends BitMaskSorterInt {
    boolean calculateBitMaskOptimization = true;

    public void setCalculateBitMaskOptimization(boolean calculateBitMaskOptimization) {
        this.calculateBitMaskOptimization = calculateBitMaskOptimization;
    }

    @Override
    public void sort(int[] array, int start, int endP1, FieldSorterOptions options) {
        int n = endP1 - start;
        if (n < 2) {
            return;
        }
        int ordered = options.isUnsigned() ? listIsOrderedUnSigned(array, start, endP1) : listIsOrderedSigned(array, start, endP1);
        if (ordered == OrderAnalysisResult.DESCENDING) {
            SorterUtilsInt.reverse(array, start, endP1);
        }
        if (ordered != OrderAnalysisResult.UNORDERED) return;

        int[] bList = null;

        if (calculateBitMaskOptimization) {
            MaskInfoInt maskInfo = MaskInfoInt.calculateMask(array, start, endP1);
            int mask = maskInfo.getMask();
            bList = MaskInfoInt.getMaskAsArray(mask);
            if (bList.length == 0) {
                return;
            }
        }
        sort(array, start, endP1, options, bList, null);
    }

    @Override
    public void sort(int[] array, int start, int endP1, FieldSorterOptions options, int[] bList, Object params) {
        int mask = 0xFFFFFFFF;
        if (calculateBitMaskOptimization) {
            if (bList.length == 0) {
                return;
            }
            if (bList[0] == MaskInfoInt.UPPER_BIT && !options.isUnsigned()) { //sign bit is set and there are negative numbers and positive numbers
                int sortMask = 1 << bList[0];
                int finalLeft = options.isUnsigned()
                        ? SorterUtilsInt.partitionNotStable(array, start, endP1, sortMask)
                        : SorterUtilsInt.partitionReverseNotStable(array, start, endP1, sortMask);
                int n1 = finalLeft - start;
                int n2 = endP1 - finalLeft;
                int mask1 = 0;
                int mask2 = 0;
                if (n1 > 1) { //sort negative numbers
                    MaskInfoInt maskParts1 = MaskInfoInt.calculateMask(array, start, finalLeft);
                    mask1 = maskParts1.getMask();
                    if (mask1 == 0) {
                        n1 = 0;
                    }
                }
                if (n2 > 1) { //sort positive numbers
                    MaskInfoInt maskParts2 = MaskInfoInt.calculateMask(array, finalLeft, endP1);
                    mask2 = maskParts2.getMask();
                    if (mask2 == 0) {
                        n2 = 0;
                    }
                }
                int[] aux = new int[Math.max(n1, n2)];
                if (n1 > 1) { //sort negative numbers
                    sortBytes(array, start, finalLeft, aux, mask1);
                }
                if (n2 > 1) { //sort positive numbers
                    sortBytes(array, finalLeft, endP1, aux, mask2);
                }
                return;
            } else {
                mask = MaskInfoInt.getMaskLastBits(bList, 0);
            }
        }
        int n = endP1 - start;
        int[] aux = new int[n];
        sortBytes(array, start, endP1, aux, mask);
    }

    private void sortBytes(int[] array, int start, final int endP1, int[] aux, final int mask) {
        int[] bList = MaskInfoInt.getMaskAsArray(mask);

        Section[] sections = BitSorterUtils.getProcessedSections(bList, 0, bList.length - 1, 8);
        int maxBitDigits = (int) Math.ceil(bList.length * 1.0 / sections.length);
        Section[] sections2 = BitSorterUtils.getProcessedSections(bList, 0, bList.length - 1, maxBitDigits);
        if (sections2.length == sections.length) {
            sections = sections2;
        }

        int n = endP1 - start;
        int ops = 0;
        int[] arrayOrig = array;
        int startOrig = start;
        int startAux = 0;

        final int mask0 = MaskInfoInt.getMaskRangeBits(sections[0].start, sections[0].shift);
        final int countLength0 = 1 << sections[0].bits;
        final int shift0 = sections[0].shift;
        final int[] count0 = new int[countLength0];

        final int mask8 = sections.length > 1 ? MaskInfoInt.getMaskRangeBits(sections[1].start, sections[1].shift) : 0;
        final int countLength8 = sections.length > 1 ? 1 << sections[1].bits : 0;
        final int shift8 = sections.length > 1 ? sections[1].shift : 0;
        final int[] count8 = new int[countLength8];

        final int mask16 = sections.length > 2 ? MaskInfoInt.getMaskRangeBits(sections[2].start, sections[2].shift) : 0;
        final int countLength16 = sections.length > 2 ? 1 << sections[2].bits : 0;
        final int shift16 = sections.length > 2 ? sections[2].shift : 0;
        final int[] count16 = new int[countLength16];

        final int mask24 = sections.length > 3 ? MaskInfoInt.getMaskRangeBits(sections[3].start, sections[3].shift) : 0;
        final int countLength24 = sections.length > 3 ? 1 << sections[3].bits : 0;
        final int shift24 = sections.length > 3 ? sections[3].shift : 0;
        final int[] count24 = new int[countLength16];

        if (sections.length == 1) {
            if (shift0 == 0) {
                for (int i = start; i < endP1; ++i) {
                    int e = array[i];
                    count0[e & mask0]++;
                }
            } else {
                for (int i = start; i < endP1; ++i) {
                    int e = array[i];
                    count0[e & mask0 >>> shift0]++;
                }
            }
        } else if (sections.length == 2) {
            if (shift0 == 0) {
                for (int i = start; i < endP1; ++i) {
                    int e = array[i];
                    count0[e & mask0]++;
                    count8[(e & mask8) >>> shift8]++;
                }
            } else {
                for (int i = start; i < endP1; ++i) {
                    int e = array[i];
                    count0[e & mask0 >>> shift0]++;
                    count8[(e & mask8) >>> shift8]++;
                }
            }
        } else if (sections.length == 3) {
            if (shift0 == 0) {
                for (int i = start; i < endP1; ++i) {
                    int e = array[i];
                    count0[e & mask0]++;
                    count8[(e & mask8) >>> shift8]++;
                    count16[(e & mask16) >>> shift16]++;
                }
            } else {
                for (int i = start; i < endP1; ++i) {
                    int e = array[i];
                    count0[e & mask0 >>> shift0]++;
                    count8[(e & mask8) >>> shift8]++;
                    count16[(e & mask16) >>> shift16]++;
                }
            }
        } else {
            if (shift0 == 0) {
                for (int i = start; i < endP1; ++i) {
                    int e = array[i];
                    count0[e & mask0]++;
                    count8[(e & mask8) >>> shift8]++;
                    count16[(e & mask16) >>> shift16]++;
                    count24[(e & mask24) >>> shift24]++;
                }
            } else {
                for (int i = start; i < endP1; ++i) {
                    int e = array[i];
                    count0[e & mask0 >>> shift0]++;
                    count8[(e & mask8) >>> shift8]++;
                    count16[(e & mask16) >>> shift16]++;
                    count24[(e & mask24) >>> shift24]++;
                }
            }
        }


        calculatePosition(count0, countLength0);
        int end = start + n;

        if (shift0 == 0) {
            partitionCountSort(array, start, end, aux, count0, mask0);
        } else {
            partitionCountSort(array, start, end, aux, count0, mask0, shift0);
        }


        //System.arraycopy(aux, 0, array, start, n);
        //swap array with aux and start with startAux
        int[] tempArray = array;
        array = aux;
        aux = tempArray;
        int temp = start;
        start = startAux;
        startAux = temp;
        ops++;

        if (sections.length > 1) {
            calculatePosition(count8, countLength8);
            end = start + n;

            if (startAux == 0) {
                partitionCountSort(array, start, end, aux, count8, mask8, shift8);
            } else {
                partitionCountSort(array, start, end, aux, count8, mask8, shift8, startAux);
            }

            //System.arraycopy(aux, 0, array, start, n);
            //swap array with aux and start with startAux
            tempArray = array;
            array = aux;
            aux = tempArray;
            temp = start;
            start = startAux;
            startAux = temp;
            ops++;

        }
        if (sections.length > 2) {
            calculatePosition(count16, countLength16);
            end = start + n;

            if (startAux == 0) {
                partitionCountSort(array, start, end, aux, count16, mask16, shift16);
            } else {
                partitionCountSort(array, start, end, aux, count16, mask16, shift16, startAux);
            }

            //System.arraycopy(aux, 0, array, start, n);
            //swap array with aux and start with startAux
            tempArray = array;
            array = aux;
            aux = tempArray;
            temp = start;
            start = startAux;
            startAux = temp;
            ops++;

        }
        if (sections.length > 3) {
            calculatePosition(count24, countLength24);
            end = start + n;

            if (startAux == 0) {
                partitionCountSort(array, start, end, aux, count24, mask24, shift24);
            } else {
                partitionCountSort(array, start, end, aux, count24, mask24, shift24, startAux);
            }
            array = aux;
            start = startAux;
            ops++;
        }
        if (ops % 2 == 1) {
            System.arraycopy(array, start, arrayOrig, startOrig, n);
        }
    }

    private static void calculatePosition(final int[] count, final int countLength) {
        for (int i = 0, sum = 0; i < countLength; ++i) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
    }

    private static void partitionCountSort(final int[] array, final int start, final int end, final int[] aux, final int[] count, final int mask) {
        for (int i = start; i < end; ++i) {
            int element = array[i];
            aux[count[(element & mask)]++] = element;
        }
    }

    private static void partitionCountSort(final int[] array, final int start, final int end, final int[] aux, final int[] count, final int mask, final int shift) {
        for (int i = start; i < end; ++i) {
            int element = array[i];
            aux[count[(element & mask) >>> shift]++] = element;
        }
    }

    private static void partitionCountSort(final int[] array, final int start, final int end, final int[] aux, final int[] count, final int mask, final int shift, final int startAux) {
        for (int i = start; i < end; ++i) {
            int element = array[i];
            aux[count[(element & mask) >>> shift]++ + startAux] = element;
        }
    }

}
