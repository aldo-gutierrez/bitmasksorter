package com.aldogg.sorter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.aldogg.sorter.intType.IntSorterUtils.compareAndSwapSigned;
import static com.aldogg.sorter.intType.IntSorterUtils.compareAndSwapSignedR;

public class BitSorterUtils {

    public static int[] getMaskBit(final int[] list, final int start, final int end) {
        int mask = 0x00000000;
        int inv_mask = 0x00000000;
        for (int i = start; i < end; i++) {
            int ei = list[i];
            mask = mask | ei;
            inv_mask = inv_mask | (~ei);
        }
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

    public static int getMaskBit(final int k) {
        final int n = 1;
        return (n << k);
    }

    public static int getMaskLastBits(final int[] listK, final int kIndex) {
        int mask = 0;
        for (int i = kIndex; i < listK.length; i++) {
            int k = listK[i];
            mask = mask | getMaskBit(k);
        }
        return mask;
    }

    public static int getMaskRangeBits(final int kIndexStart, final int kIndexEnd) {
        int mask = 0;
        for (int k = kIndexStart; k >= kIndexEnd; k--) {
            mask = mask | getMaskBit(k);
        }
        return mask;
    }

    public static int getKeySN(int element, int[][] sections) {
        int result = 0;
        for (int i = 0; i < sections.length; i++) {
            int[] section = sections[i];
            int length = section[1];
            int sortMask = section[2];
            int shiftRight = section[3];
            int bits = (element & sortMask) >> shiftRight;
            result = result << length | bits;
        }
        return result;
    }

    public static int getKeySec1(int element, int[] section) {
        int sortMask = section[2];
        int length = section[1];
        if  (section[0] + 1 == length) {
            return element & sortMask;
        } else {
            int shiftRight = section[3];
            return  (element & sortMask) >> shiftRight;
        }
    }

    public static int twoPowerX(int k) {
        return 1<<k;
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
            sectionsAsInts[i][2] = getMaskRangeBits(entry.getKey(), entry.getKey() - entry.getValue() + 1);
            sectionsAsInts[i][3] = entry.getKey() - entry.getValue() + 1;
            i++;
        }
        return sectionsAsInts;
    }


    public static boolean listIsOrdered(int[] list, int start, int end) {
        boolean swap = compareAndSwapSignedR(list, 0, end-1);
        if (!swap) {
            for (int i = start; i + 1 < end; i ++) {
                swap = swap || compareAndSwapSignedR(list, i, i + 1);
                if (swap) {
                    return  false;
                }
            }
            return !swap;
        } else {
            //Reverse Order?
            for (int i = start+1; i <= (end-1) / 2; i++) {
                swap = swap && compareAndSwapSignedR(list, i, end - 1 - i);
                if (!swap) {
                    return  false;
                }
            }
            if (swap) {
                swap = false;
                for (int i = start; i + 1 < end; i ++) {
                    swap = swap || compareAndSwapSignedR(list, i, i + 1);
                    if (swap) {
                        return false;
                    }
                }
                return !swap;
            }
        }
        return false;
    }

}
