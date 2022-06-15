package com.aldogg.sorter.intType;

import com.aldogg.sorter.Section;

import java.util.Arrays;

import static com.aldogg.sorter.BitSorterUtils.*;

public class CountSort {

    public static void countSort(final int[] array, final int start, final int end, int[] kList,  int kIndex) {
        int twoPowerK = 1 << kList.length - kIndex;
        kList = Arrays.copyOfRange(kList, kIndex, kList.length);
        Section[] sections = getMaskAsSections(kList);
        kIndex = 0;
        int[] countBuffer = new int[twoPowerK];
        int[] numberBuffer = null;
        if (sections.length == 1 && sections[0].isSectionAtEnd()) {
        } else {
            numberBuffer = new int[twoPowerK];
        }
        int sortMask = getMaskLastBits(kList, kIndex);
        countSort(array, start, end, sortMask, sections, countBuffer, numberBuffer);
    }

    /**
     * CPU: N + MAX(2^K, N)
     * MEM: 2 * (2^K)
     */
    public static void countSort(final int[] array, final int start, final int end, int mask, Section[] sections, int[] count, int[] auxTPK) {
        if (sections.length == 1 && sections[0].isSectionAtEnd()) {
            int elementSample = array[start];
            elementSample = elementSample & ~mask;
            if (elementSample == 0) { //last bits and includes all numbers
                for (int i = start; i < end; i++) {
                    count[array[i]]++;
                }
                int i = start;
                for (int j = 0; j < count.length; j++) {
                    int countJ = count[j];
                    if (countJ > 0) {
                        for (int k = 0; k < countJ; k++) {
                            array[i] = j;
                            i++;
                        }
                        if (i == end) {
                            break;
                        }
                    }
                }

            } else { //last bits but there is a mask for a bigger number
                for (int i = start; i < end; i++) {
                    count[array[i] & mask]++;
                }
                int i = start;
                for (int j = 0; j < count.length; j++) {
                    int countJ = count[j];
                    if (countJ > 0) {
                        int value = j | elementSample;
                        for (int k = 0; k < countJ; k++) {
                            array[i] = value;
                            i++;
                        }
                        if (i == end) {
                            break;
                        }
                    }
                }
            }
        } else {
            if (sections.length == 1) {
                Section section = sections[0];
                if (section.isSectionAtEnd()) {
                    //TODO check if this code is executed or not
                    for (int i = start; i < end; i++) {
                        int element = array[i];
                        int key = element & section.sortMask;
                        count[key]++;
                        auxTPK[key] = element;
                    }
                } else {
                    for (int i = start; i < end; i++) {
                        int element = array[i];
                        int key = (element & section.sortMask) >> section.shiftRight;
                        count[key]++;
                        auxTPK[key] = element;
                    }
                }
            } else {
                for (int i = start; i < end; i++) {
                    int element = array[i];
                    int key = getKeySN(element, sections);
                    count[key]++;
                    auxTPK[key] = element;
                }
            }
            int i = start;
            for (int j = 0; j < count.length; j++) {
                int countJ = count[j];
                if (countJ > 0) {
                    int value = auxTPK[j];
                    for (int k = 0; k < countJ; k++) {
                        array[i] = value;
                        i++;
                    }
                    if (i == end) {
                        break;
                    }
                }
            }
        }
    }
}
