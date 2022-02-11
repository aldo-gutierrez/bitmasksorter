package com.aldogg;

import static com.aldogg.BitSorterUtils.*;

public class CountSort {
    public static void countSort(final int[] list, final int start, final int end, int[] listK, final int kIndex) {
        int[][] sections = getMaskAsSections(listK); //in case of multiple sections maybe trim list and set kIndex 0???
        int sortMask = getMask(listK, kIndex);
        int auxCountSize = (int) Math.pow(2, listK.length - kIndex);
        if (sections.length == 1 && sections[0][0] + 1 == sections[0][1]) {
            //if (sections.length == 1 && sections[0][0] + 1 == sections[0][1] && kIndex == 0) { //THIS IS NOT NECESSARY BECAUSE THE ELEMENT SAMPLE
            int[] auxCount = new int[auxCountSize];
            int elementSample = list[start];
            elementSample = elementSample & ~sortMask;
            for (int i = start; i < end; i++) {
                int element = list[i];
                int elementMasked = element & sortMask;
                auxCount[elementMasked] = auxCount[elementMasked] + 1;
            }
            int i = start;
            for (int j = 0; j < auxCountSize; j++) {
                int count = auxCount[j];
                if (count > 0) {
                    for (int k = 0; k < count; k++) {
                        list[i] = j | elementSample;
                        i++;
                    }
                }
            }
        } else {
            int[] auxCount = new int[auxCountSize];
            int[] auxCount2 = new int[auxCountSize];
            for (int i = start; i < end; i++) {
                int element = list[i];
                int elementMasked = element & sortMask;
                if (elementMasked == 0) {
                    auxCount[0] = auxCount[0] + 1;
                    auxCount2[0] = element;
                } else {
                    int key = getKey(elementMasked, sections);
                    auxCount[key] = auxCount[key] + 1;
                    auxCount2[key] = element;
                }
            }
            int i = start;
            for (int j = 0; j < auxCountSize; j++) {
                int count = auxCount[j];
                if (count > 0) {
                    int value = auxCount2[j];
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
