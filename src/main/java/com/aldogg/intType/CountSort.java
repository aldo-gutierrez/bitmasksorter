package com.aldogg.intType;

import com.aldogg.BitSorterParams;

import java.util.Arrays;

import static com.aldogg.BitSorterUtils.*;

public class CountSort {

    public static void countSort(final int[] list, final int start, final int end, int[] kList,  int kIndex) {
        int countBufferSize = twoPowerX(kList.length - kIndex);
        kList = Arrays.copyOfRange(kList, kIndex, kList.length);
        int[][] sections = getMaskAsSections(kList);
        kIndex = 0;
        int[] countBuffer = new int[countBufferSize];
        int[] numberBuffer = null;
        if (sections.length == 1 && sections[0][0] + 1 == sections[0][1]) {
        } else {
            numberBuffer = new int[countBufferSize];
        }
        int sortMask = getMaskLastBits(kList, kIndex);
        countSort(list, start, end, sortMask, sections, countBuffer, numberBuffer);
    }

    /**
     * CPU: N + MAX(2^K, N)
     * MEM: 2 * (2^K)
     */
    public static void countSort(final int[] list, final int start, final int end, int sortMask, int[][] sections, int[] countBuffer, int[] numberBuffer) {
        if (sections.length == 1 && sections[0][0] + 1 == sections[0][1]) {
            int elementSample = list[start];
            elementSample = elementSample & ~sortMask;
            if (elementSample == 0) { //last bits and includes all numbers
                for (int i = start; i < end; i++) {
                    int element = list[i];
                    countBuffer[element] = countBuffer[element] + 1;
                }
                int i = start;
                for (int j = 0; j < countBuffer.length; j++) {
                    int count = countBuffer[j];
                    if (count > 0) {
                        for (int k = 0; k < count; k++) {
                            list[i] = j;
                            i++;
                        }
                        if (i == end) {
                            break;
                        }
                    }
                }

            } else { //last bits but there is a mask for a bigger number
                for (int i = start; i < end; i++) {
                    int element = list[i];
                    int elementMasked = element & sortMask;
                    countBuffer[elementMasked] = countBuffer[elementMasked] + 1;
                }
                int i = start;
                for (int j = 0; j < countBuffer.length; j++) {
                    int count = countBuffer[j];
                    if (count > 0) {
                        int value = j | elementSample;
                        for (int k = 0; k < count; k++) {
                            list[i] = value;
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
                    int element = list[i];
                    int key = getKeySec1(element, sections[0]);
                    countBuffer[key] = countBuffer[key] + 1;
                    numberBuffer[key] = element;
                }
            } else {
                for (int i = start; i < end; i++) {
                    int element = list[i];
                    int key = getKeySN(element, sections);
                    countBuffer[key] = countBuffer[key] + 1;
                    numberBuffer[key] = element;
                }
            }
            int i = start;
            for (int j = 0; j < countBuffer.length; j++) {
                int count = countBuffer[j];
                if (count > 0) {
                    int value = numberBuffer[j];
                    for (int k = 0; k < count; k++) {
                        list[i] = value;
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
