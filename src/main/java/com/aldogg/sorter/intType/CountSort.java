package com.aldogg.sorter.intType;

import java.util.Arrays;

import static com.aldogg.sorter.BitSorterUtils.*;

public class CountSort {

    public static void countSort(final int[] array, final int start, final int end, int[] kList,  int kIndex) {
        int twoPowerK = twoPowerX(kList.length - kIndex);
        kList = Arrays.copyOfRange(kList, kIndex, kList.length);
        int[][] sections = getMaskAsSections(kList);
        kIndex = 0;
        int[] countBuffer = new int[twoPowerK];
        int[] numberBuffer = null;
        if (sections.length == 1 && sections[0][0] + 1 == sections[0][1]) {
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
    public static void countSort(final int[] array, final int start, final int end, int mask, int[][] sections, int[] count, int[] auxTPK) {
        if (sections.length == 1 && sections[0][0] + 1 == sections[0][1]) {
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
                for (int i = start; i < end; i++) {
                    int element = array[i];
                    int key = getKeySec1(element, sections[0]);
                    count[key]++;
                    auxTPK[key] = element;
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
