package com.aldogg.sorter.int_;

import com.aldogg.sorter.FieldOptions;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;
import com.aldogg.sorter.shared.Section;

import java.util.*;

import static com.aldogg.sorter.BitSorterUtils.*;

/**
 * Pigeonhole CountSort
 *   with support for a list of bits
 *   recommended to use instead of Java Sort when range <=2**24
 *   recommended to use instead of RadixBitSorter when range <=2**19
 * 18 bits (6% OF Java) (42% OF RadixBitSorter)
 * 19 bits (8% OF Java) (67% OF RadixBitSorter)
 * 20 bits (15% OF Java) (120% OF RadixBitSorter)
 * 21 bits (23% OF Java) (168% OF RadixBitSorter)
 * 22 bits (27% OF Java) (200% OF RadixBitSorter)
 * 23 bits (33% OF Java) (180% OF RadixBitSorter)
 * 24 bits (50% OF Java) (253% OF RadixBitSorter)
 * 25 bits (99% OF Java) (460% OF RadixBitSorter)
 *   tested with N=2**20
 */
public class PCountSortInt extends BitMaskSorterInt {

    @Override
    public void sort(int[] array, int start, int endP1, FieldOptions options, int[] bList, Object params) {
        pCountSort(array, start, endP1, bList, 0);
    }

    public static void pCountSort(final int[] array, final int start, final int endP1, int[] bList, int bListStart) {
        int[] bListNew = Arrays.copyOfRange(bList, bListStart, bList.length);
        Section[] sections = getMaskAsSections(bListNew, 0, bListNew.length - 1);
        if (sections.length == 1) {
            Section section = sections[0];
            if (section.shift == 0) {
                int mask = MaskInfoInt.getMaskLastBits(bListNew, 0);
                int elementSample = array[start];
                elementSample = elementSample & ~mask;
                if (elementSample == 0) { //last bits and includes all numbers and all positive numbers
                    pCountSortPositive(array, start, endP1, 1 << section.bits);
                } else { //last bits but there is a mask for a bigger number
                    pCountSortEndingMask(array, start, endP1, mask, elementSample);
                }
            } else {
                pCountSortSection(array, start, endP1, section);
            }
        } else if (sections.length > 1) {
            pCountSortSections(array, start, endP1, sections);
        }
    }

    private static void pCountSortPositive(int[] array, int start, int endP1, int range) {
        if (range > (1 << 24)) {
            //System.err.println("Pigeonhole Count sort should be used for number range <= 2**24, for optimal performance: range <= 2**20");
        }
        int[] count = new int[range];
        for (int i = start; i < endP1; i++) {
            count[array[i]]++;
        }
        int i = start;
        for (int j = 0; j < count.length; j++) {
            int cMax = count[j];
            if (cMax > 0) {
                for (int c = 0; c < cMax; c++) {
                    array[i] = j;
                    i++;
                }
                if (i == endP1) {
                    break;
                }
            }
        }
    }

    private static void pCountSortEndingMask(int[] array, int start, int endP1, int mask, int elementSample) {
        int range = mask + 1;
        if (range > (1 << 24)) {
            //System.err.println("Pigeonhole Count sort should be used for number range <= 2**24, for optimal performance: range <= 2**20");
        }
        int[] count = new int[range];
        for (int i = start; i < endP1; i++) {
            count[array[i] & mask]++;
        }
        int i = start;
        for (int j = 0; j < count.length; j++) {
            int countJ = count[j];
            if (countJ > 0) {
                int value = j | elementSample;
                for (int c = 0; c < countJ; c++) {
                    array[i] = value;
                    i++;
                }
                if (i == endP1) {
                    break;
                }
            }
        }
    }

    private static void pCountSortSection(int[] array, int start, int endP1, Section section) {
        int range = 1 << section.bits;
        if (range > (1 << 24)) {
            //System.err.println("Pigeonhole Count sort should be used for number range <= 2**24, for optimal performance: range <= 2**20");
        }
        int[] count = new int[range];
        int[] number = new int[range];
        int mask1 = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
        for (int i = start; i < endP1; i++) {
            int element = array[i];
            int key = (element & mask1) >> section.shift;
            count[key]++;
            number[key] = element;
        }
        int i = start;
        for (int j = 0; j < count.length; j++) {
            int countJ = count[j];
            if (countJ > 0) {
                int value = number[j];
                for (int c = 0; c < countJ; c++) {
                    array[i] = value;
                    i++;
                }
                if (i == endP1) {
                    break;
                }
            }
        }
    }

    //TODO need test as it not runs in performance benchmarks
    private static void pCountSortSections(int[] array, int start, int endP1, Section[] sections) {
        int bits = 0;
        for (Section section : sections) {
            bits += section.bits;
        }
        int range = 1 << bits;
        if (range > (1 << 24)) {
            //System.err.println("Pigeonhole Count sort should be used for number range <= 2**24, for optimal performance: range <= 2**20");
        }
        int[] count = new int[range];
        int[] number = new int[range];
        for (int i = start; i < endP1; i++) {
            int element = array[i];
            int key = getKeySN(element, sections);
            count[key]++;
            number[key] = element;
        }
        int i = start;
        for (int j = 0; j < count.length; j++) {
            int countJ = count[j];
            if (countJ > 0) {
                int value = number[j];
                for (int c = 0; c < countJ; c++) {
                    array[i] = value;
                    i++;
                }
                if (i == endP1) {
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        int N = (int) Math.pow(2, 20);
        int C = 100;
        int R2 = 19;
        int R = (int) Math.pow(2, R2);
        System.out.println(" R " +R+ " " + (1<<R2));
        Random random = new Random(1234);
        long time1 = 0;
        long time2 = 0;
        for (int c = 0; c < C; c++) {
            int[] array = new int[N];
            for (int n = 0; n < N; n++) {
                array[n] = random.nextInt(R);
            }
            int[] array1 = new int[N];
            int[] array2 = new int[N];
            System.arraycopy(array, 0, array1, 0, N);
            System.arraycopy(array, 0, array2, 0, N);
            long start = System.currentTimeMillis();
            pCountSortPositive(array1, 0, N, R);
            time1 += System.currentTimeMillis() - start;
            long start2 = System.currentTimeMillis();
            //RadixBitSorterInt radixBitSorterInt = new RadixBitSorterInt();
            //radixBitSorterInt.sort(array2, 0, N);
            Arrays.sort(array2, 0, N);
            time2 += System.currentTimeMillis() - start2;
        }
        System.out.println(" 1 TIME " + time1);
        System.out.println(" 2 TIME " + time2);
    }


}
