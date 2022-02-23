package com.aldogg;

import java.util.Arrays;

import static com.aldogg.BitSorterUtils.*;

public class CountSort {

    public static void countSort(final int[] list, final int start, final int end, int[] kList,  int kIndex) {
        int countBufferSize = (int) Math.pow(2, kList.length - kIndex);
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
     * CPU: N + 2^K
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
                        for (int k = 0; k < count; k++) {
                            list[i] = j | elementSample;
                            i++;
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
                }
            }
        }
    }

    /*
    countSortMT
    public static void countSort(final int[] list, final int start, final int end, int[] listK, int kIndex) {
        int[][] sections = getMaskAsSections(listK);
        int sortMask = getMask(listK, kIndex);
        //List<AtomicInteger> aux = new ArrayList<>((int) Math.pow(2, listK.length));
        //for (int i=0; i < (int) Math.pow(2, listK.length); i++) {
        //    aux.add(new AtomicInteger(0));
        //}
        int[] aux = new int[(int) Math.pow(2, listK.length)];
        int[] aux2 = new int[(int) Math.pow(2, listK.length)];
        for (int i = start; i < end; i++) {
            int element = list[i];
            int elementMasked = element & sortMask;
            if (elementMasked == 0) {
                //aux.get(0).incrementAndGet();
                aux[0] = aux[0] + 1;
                aux2[0] = element;
            } else {
                int key = getKey(elementMasked, sections);
                //aux.get(key).incrementAndGet();
                aux[key] = aux[key] + 1;
                aux2[key] = element;
            }
        }
        int i = start;
        for (int j = 0; j < aux.length; j++) {
            int count = aux[j];
            //int count = aux.get(j).get();
            if (count > 0) {
                int value = aux2[j];
                for (int k = 0; k < count; k++) {
                    list[i] = value;
                    i++;
                }
            }
        }
    }


     */


}
