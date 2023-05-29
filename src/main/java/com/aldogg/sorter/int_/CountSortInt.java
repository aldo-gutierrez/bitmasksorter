package com.aldogg.sorter.int_;

import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.Section;

import java.util.Arrays;

import static com.aldogg.sorter.BitSorterUtils.*;

public class CountSortInt {

    public static void countSort(final int[] array, final int start, final int endP1, int[] bList, int bListStart) {
        int[] bListNew = Arrays.copyOfRange(bList, bListStart, bList.length);
        Section[] sections = getMaskAsSections(bListNew, 0, bListNew.length - 1);
        if (sections.length == 1 && sections[0].shift == 0) {
            int mask = MaskInfoInt.getMaskLastBits(bListNew, 0);
            int elementSample = array[start];
            elementSample = elementSample & ~mask;
            if (elementSample == 0) { //last bits and includes all numbers and all positive numbers
                dCountPositive(array, start, endP1, 1 << sections[0].bits);
            } else { //last bits but there is a mask for a bigger number
                countEndingMask(array, start, endP1, mask, elementSample);
            }
        } else {
            if (sections.length == 1) {
                dCountSortSection(array, start, endP1, sections[0]);
            } else {
                dCountSortSections(array, start, endP1, sections);
            }
        }
    }

    private static void dCountPositive(int[] array, int start, int endP1, int range) {
        int[] count = new int[range];
        for (int i = start; i < endP1; i++) {
            count[array[i]]++;
        }
        int i = start;
        for (int j = 0; j < count.length; j++) { //Destructive (Creates new numbers no swaps)
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

    private static void countEndingMask(int[] array, int start, int endP1, int mask, int elementSample) {
        int[] count = new int[mask + 1];
        for (int i = start; i < endP1; i++) {
            count[array[i] & mask]++;
        }
        int i = start;
        for (int j = 0; j < count.length; j++) { //Destructive (Creates new numbers no swaps)
            int countJ = count[j];
            if (countJ > 0) {
                int value = j | elementSample;
                for (int k = 0; k < countJ; k++) {
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
    private static void dCountSortSection(int[] array, int start, int endP1, Section section) {
        int range = 1 << section.bits;
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
                for (int k = 0; k < countJ; k++) {
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
    private static void dCountSortSections(int[] array, int start, int endP1, Section[] sections) {
        int bits = 0;
        for (Section section : sections) {
            section.calculateIntMask();
            bits += section.bits;
        }
        int range = 1 << bits;
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
                for (int k = 0; k < countJ; k++) {
                    array[i] = value;
                    i++;
                }
                if (i == endP1) {
                    break;
                }
            }
        }
    }


}
