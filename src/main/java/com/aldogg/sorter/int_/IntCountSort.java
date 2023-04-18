package com.aldogg.sorter.int_;

import com.aldogg.sorter.IntSection;
import com.aldogg.sorter.IntSectionsInfo;
import com.aldogg.sorter.MaskInfoInt;

import java.util.Arrays;

import static com.aldogg.sorter.BitSorterUtils.*;

public class IntCountSort {

    public static void countSort(final int[] array, final int start, final int endP1, int[] kList,  int kIndex) {
        int twoPowerK = 1 << (kList.length - kIndex);
        int[] kListNew = Arrays.copyOfRange(kList, kIndex, kList.length);
        IntSectionsInfo sectionsInfo = getMaskAsSections(kList, 0, kList.length - 1);
        IntSection[] sections = sectionsInfo.sections;
        int sortMask = MaskInfoInt.getMaskLastBits(kListNew, 0);
        countSort(array, start, endP1, sortMask, sections, twoPowerK);
    }

    /**
     * CPU: N + MAX(2^K, N)
     * MEM: 2 * (2^K)
     */
    public static void countSort(final int[] array, final int start, final int endP1, int mask, IntSection[] sections, int twoPowerK) {
        int[] count = new int[twoPowerK];
        int[] number = null;
        if (sections.length != 1 || !sections[0].isSectionAtEnd()) {
            number = new int[twoPowerK];
        }

        if (sections.length == 1 && sections[0].isSectionAtEnd()) {
            int elementSample = array[start];
            elementSample = elementSample & ~mask;
            if (elementSample == 0) { //last bits and includes all numbers
                for (int i = start; i < endP1; i++) {
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
                        if (i == endP1) {
                            break;
                        }
                    }
                }

            } else { //last bits but there is a mask for a bigger number
                for (int i = start; i < endP1; i++) {
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
                        if (i == endP1) {
                            break;
                        }
                    }
                }
            }
        } else {
            if (sections.length == 1) {
                IntSection section = sections[0];
                if (section.isSectionAtEnd()) {
                    //TODO check if this code is executed or not
                    for (int i = start; i < endP1; i++) {
                        int element = array[i];
                        int key = element & section.sortMask;
                        count[key]++;
                        number[key] = element;
                    }
                } else {
                    for (int i = start; i < endP1; i++) {
                        int element = array[i];
                        int key = (element & section.sortMask) >> section.shiftRight;
                        count[key]++;
                        number[key] = element;
                    }
                }
            } else {
                for (int i = start; i < endP1; i++) {
                    int element = array[i];
                    int key = getKeySN(element, sections);
                    count[key]++;
                    number[key] = element;
                }
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
}
