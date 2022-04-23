package com.aldogg.sorter;

import java.util.*;

public class BitSorterUtils {

    public static int[] getMaskBit(final int[] array, final int start, final int end) {
        int mask = 0x00000000;
        int inv_mask = 0x00000000;
        for (int i = start; i < end; i++) {
            int ei = array[i];
            mask = mask | ei;
            inv_mask = inv_mask | (~ei);
        }
        return new int[]{mask, inv_mask};
    }

    public static int[] getMaskAsArray(final int mask) {
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

    public static int getMaskLastBits(final int[] kList, final int kIndex) {
        int mask = 0;
        for (int i = kIndex; i < kList.length; i++) {
            int k = kList[i];
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

    public static int[][] getMaskAsSections(final int[] kList) {
        LinkedHashMap<Integer, Integer> sections = new LinkedHashMap<>();
        int currentSection = -1;
        for (int i = 0; i < kList.length; i++) {
            int k = kList[i];
            if (i == 0) {
                sections.put(k, 1);
                currentSection = k;
            } else {
                if (kList[i - 1] - k == 1) {
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


    public static int listIsOrderedSigned(int[] array, int start, int end) {
        int i1 = array[start];
        int i = start + 1;
        while (i < end) {
            int i2 = array[i];
            if (i2 != i1) {
                break;
            }
            i1 = i2;
            i++;
        }
        if (i == end) {
            return AnalysisResult.ALL_EQUAL;
        }

        //ascending
        i1 = array[i];
        if (array[i-1] < i1) {
            i++;
            for (; i < end; i++)  {
                int i2 = array[i];
                if (i1 > i2) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                return AnalysisResult.ASCENDING;
            }
        }
        //descending
        else {
            i++;
            for (; i < end; i++)  {
                int i2 = array[i];
                if (i1 < i2) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                return AnalysisResult.DESCENDING;
            }
        }
        return AnalysisResult.UNORDERED;
    }

    public static int listIsOrderedUnSigned(int[] array, int start, int end) {
        int i1 = array[start];
        int i = start + 1;
        while (i < end) {
            int i2 = array[i];
            if (i2 != i1) {
                break;
            }
            i1 = i2;
            i++;
        }
        if (i == end) {
            return AnalysisResult.ALL_EQUAL;
        }

        //ascending
        i1 = array[i];
        if (array[i-1] < i1) {
            i++;
            for (; i < end; i++)  {
                int i2 = array[i];
                if (i1 + 0x80000000 > i2 + 0x80000000) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                return AnalysisResult.ASCENDING;
            }
        }
        //descending
        else {
            i++;
            for (; i < end; i++)  {
                int i2 = array[i];
                if (i1 + 0x80000000 < i2 + 0x80000000) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                return AnalysisResult.DESCENDING;
            }
        }
        return AnalysisResult.UNORDERED;
    }

}
