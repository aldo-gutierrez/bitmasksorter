package com.aldogg.sorter;

import java.util.*;

public class BitSorterUtils {


    public static int getKeySN(final int element, final Section[] sections) {
        int result = 0;
        for (int i = 0; i < sections.length; i++) {
            Section section = sections[i];
            int length = section.length;
            int sortMask = section.sortMask;
            int shiftRight = section.shiftRight;
            int bits = (element & sortMask) >> shiftRight;
            result = result << length | bits;
        }
        return result;
    }

    public static Section[] getMaskAsSections(final int[] kList, int kStart, int kEnd) {
        LinkedHashMap<Integer, Integer> sections = new LinkedHashMap<>();
        int currentSection = -1;
        for (int i = kStart; i <= kEnd; i++) {
            int k = kList[i];
            if (i == kStart) {
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
        Section[] sectionsAsInts = new Section[sections.size()];
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : sections.entrySet()) {
            sectionsAsInts[i] = new Section();
            sectionsAsInts[i].k = entry.getKey();
            sectionsAsInts[i].length = entry.getValue();
            int aux = entry.getKey() - entry.getValue() + 1;
            sectionsAsInts[i].sortMask = MaskInfo.getMaskRangeBits(entry.getKey(), aux);
            sectionsAsInts[i].shiftRight = aux;
            i++;
        }
        return sectionsAsInts;
    }


    public static Section[] splitSection(Section section) {
        if (section.length <= BitSorterMTParams.MAX_BITS_RADIX_SORT) {
            return new Section[]{section};
        } else {
            List<Section> sections = new ArrayList<>();
            int sectionQuantity;
            int sectionSize = BitSorterMTParams.MAX_BITS_RADIX_SORT;
            int divisor = section.length / BitSorterMTParams.MAX_BITS_RADIX_SORT;
            if (section.length % BitSorterMTParams.MAX_BITS_RADIX_SORT == 0) {
                sectionQuantity = divisor;
            } else {
                sectionQuantity = divisor + 1;
                if (section.length % BitSorterMTParams.MAX_BITS_RADIX_SORT <= 2) {
                    sectionSize = BitSorterMTParams.MAX_BITS_RADIX_SORT - 1;
            }
            }
            int sizeAux = 0;
            for (int i = 0; i < sectionQuantity; i++) {
                Section sectionAux = new Section();
                sectionAux.length = (i == 0) ?  section.length - (sectionSize * (sectionQuantity - 1)) : sectionSize;
                sectionAux.k = section.k - sizeAux;
                sizeAux+=sectionAux.length;
                sections.add(sectionAux);
            }
            for (int i= sectionQuantity-1; i >=0; i--) {
                Section sectionAux = sections.get(i);
                sectionAux.shiftRight = (i == sectionQuantity - 1) ? section.shiftRight : sections.get(i+1).shiftRight + sections.get(i+1).length;
                sectionAux.sortMask = MaskInfo.getMaskRangeBits(sectionAux.k, sectionAux.k - sectionAux.length + 1);
            }
            return sections.toArray(new Section[]{});
        }
    }


    public static int listIsOrderedSigned(final int[] array, final int start, final int end) {
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
