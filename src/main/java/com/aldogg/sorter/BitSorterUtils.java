package com.aldogg.sorter;

import java.util.*;

public class BitSorterUtils {


    public static int getKeySN(final int element, final Section[] sections) {
        int result = 0;
        for (int i = 0; i < sections.length; i++) {
            Section section = sections[i];
            int bits = (element & section.sortMask) >> section.shiftRight;
            result = result << section.length | bits;
        }
        return result;
    }

    public static SectionsInfo getMaskAsSections(final int[] kList, final int kStart, final int kEnd) {
        LinkedHashMap<Integer, Integer> sectionsMap = new LinkedHashMap<>();
        int currentSection = -1;
        for (int i = kStart; i <= kEnd; i++) {
            int k = kList[i];
            if (i == kStart) {
                sectionsMap.put(k, 1);
                currentSection = k;
            } else {
                if (kList[i - 1] - k == 1) {
                    sectionsMap.put(currentSection, sectionsMap.get(currentSection) + 1);
                } else {
                    sectionsMap.put(k, 1);
                    currentSection = k;
                }
            }
        }
        SectionsInfo sections = new SectionsInfo();
        sections.sections = new Section[sectionsMap.size()];
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : sectionsMap.entrySet()) {
            Section section = new Section();
            section.k = entry.getKey();
            section.length = entry.getValue();
            sections.totalLength += section.length;
            if (section.length > sections.maxLength) {
                sections.maxLength = section.length;
            }
            int aux = entry.getKey() - entry.getValue() + 1;
            section.sortMask = MaskInfo.getMaskRangeBits(entry.getKey(), aux);
            section.shiftRight = aux;
            sections.sections[i] = section;
            i++;
        }
        return sections;
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

    public static SectionsInfo getOrderedSections(int[] kList, int kStart, int kEnd) {
        SectionsInfo sectionsInfo = new SectionsInfo();
        List<Section> finalSectionList = new ArrayList<>();
        SectionsInfo sectionsInfos = BitSorterUtils.getMaskAsSections(kList, kStart, kEnd);
        Section[] sections = sectionsInfos.sections;
        for (int i = sections.length - 1; i >= 0; i--) {
            Section section = sections[i];
            Section[] sSections = BitSorterUtils.splitSection(section);
            for (int j = sSections.length - 1; j >= 0; j--) {
                Section sSection = sSections[j];
                finalSectionList.add(sSection);
                sectionsInfo.totalLength += sSection.length;
                if (sSection.length > sectionsInfo.maxLength) {
                    sectionsInfo.maxLength = sSection.length;
                }
            }
        }
        sectionsInfo.sections = finalSectionList.toArray(new Section[0]);
        return sectionsInfo;
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
