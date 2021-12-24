package com.aldogg;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BitSorterUtils {

    public static int[] getMask(final int[] list, final int start, final int end) {
        //long startTime = System.currentTimeMillis();
        int mask = 0x00000000;
        int inv_mask = 0x00000000;
        for (int i = start; i < end; i++) {
            int ei = list[i];
            mask = mask | ei;
            inv_mask = inv_mask | (~ei);
        }
        //mask = mask & inv_mask;
        //System.out.println(System.currentTimeMillis() - startTime);
        return new int[]{mask, inv_mask};
    }

    public static int[] getMaskAsList(final int mask) {
        List<Integer> list = new ArrayList<>();
        for (int i = 31; i >= 0; i--) {
            if (((mask >> i) & 1) == 1) {
                list.add(i);
            }
        }
        int[] res = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            res[i] = list.get(i);
        }
        return res;
    }

    public static int getMask(final int k) {
        final int n = 1;
        return (n << k);
    }

    public static int getMask(final int[] listK, final int kIndex) {
        int mask = 0;
        for (int i = kIndex; i < listK.length; i++) {
            int k = listK[i];
            mask = mask | getMask(k);
        }
        return mask;
    }

    public static int getKey(final int elementMasked, final int[][] sections) {
        if (sections.length == 1 && sections[0][0] + 1 == sections[0][1]) {
            return elementMasked;
        }

        int result = 0;
        for (int i = 0; i < sections.length; i++) {
            int[] section = sections[i];
            int k = section[0];
            int length = section[1];
            int bits = (elementMasked >> (k - length + 1));
            result = result << length | bits;
        }
        return result;

//        for (int i=0; i< listK.size(); i++) {
//            int k = listK.get(i);
//            int bit = (elementMasked >> k) & 1;
//            result = result<<1 | bit;
//        }
    }

    public static int[][] getMaskAsSections(final int[] listK) {
        LinkedHashMap<Integer, Integer> sections = new LinkedHashMap<>();
        int currentSection = -1;
        for (int i = 0; i < listK.length; i++) {
            int k = listK[i];
            if (i == 0) {
                sections.put(k, 1);
                currentSection = k;
            } else {
                if (listK[i - 1] - k == 1) {
                    sections.put(currentSection, sections.get(currentSection) + 1);
                } else {
                    sections.put(k, 1);
                    currentSection = k;
                }
            }
        }
        int[][] sectionsAsInts = new int[sections.size()][];
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : sections.entrySet()) {
            sectionsAsInts[i] = new int[2];
            sectionsAsInts[i][0] = entry.getKey();
            sectionsAsInts[i][1] = entry.getValue();
        }
        return sectionsAsInts;
    }

}
