package com.aldogg;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.aldogg.IntSorterUtils.compareAndSwap;

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

    public static int getMask2(final int kIndexStart, final int kIndexEnd) {
        int mask = 0;
        for (int k = kIndexStart; k >= kIndexEnd; k--) {
            mask = mask | getMask(k);
        }
        return mask;
    }

    public static int getKey(final int elementMasked, final int[][] sections) {
        //if (sections.length == 1 && sections[0][0] + 1 == sections[0][1]) {
        //    return elementMasked;
        //}

        int result = 0;
        for (int i = 0; i < sections.length; i++) {
            int[] section = sections[i];
            int length = section[1];
            int bits = (elementMasked & section[2]) >> section[3];
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
            sectionsAsInts[i] = new int[4];
            sectionsAsInts[i][0] = entry.getKey();
            sectionsAsInts[i][1] = entry.getValue();
            sectionsAsInts[i][2] = getMask2(entry.getKey(), entry.getKey() - entry.getValue() + 1);
            sectionsAsInts[i][3] = entry.getKey() - entry.getValue() + 1;
            i++;
        }
        return sectionsAsInts;
    }

    public static boolean listIsOrdered(int[] list, int start, int end) {
        boolean swap = compareAndSwap(list, 0, end-1);
        if (!swap) {
            for (int i = start; i + 1 < end; i ++) {
                swap = swap || compareAndSwap(list, i, i + 1);
                if (swap) {
                    return  false;
                }
            }
            if (!swap) {
                return true;
            }
        } else {
            //Reverse Order?
            swap = true;
            for (int i = start+1; i <= (end-1) / 2; i++) {
                swap = swap && compareAndSwap(list, i, end - 1 - i);
                if (!swap) {
                    return  false;
                }
            }
            if (swap) {
                swap = false;
                for (int i = start; i + 1 < end; i ++) {
                    swap = swap || compareAndSwap(list, i, i + 1);
                    if (swap) {
                        return false;
                    }
                }
                if (!swap) {
                    return true;
                }
            }
        }
        return false;
    }

}
