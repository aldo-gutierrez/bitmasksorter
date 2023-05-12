package com.aldogg.sorter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BitSorterUtils {


    public static int getKeySN(final int element, final Section[] sections) {
        int result = 0;
        for (int i = 0; i < sections.length; i++) {
            Section section = sections[i];
            int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
            int bits = (element & mask) >> section.shift;
            result = result << section.length | bits;
        }
        return result;
    }

    public static Section[] getMaskAsSections(final int[] bList, final int bListStart, final int bListEnd) {
        LinkedHashMap<Integer, Integer> sectionsMap = new LinkedHashMap<>();
        int currentSection = -1;
        for (int i = bListStart; i <= bListEnd; i++) {
            int bIndex = bList[i];
            if (i == bListStart) {
                sectionsMap.put(bIndex, 1);
                currentSection = bIndex;
            } else {
                if (bList[i - 1] - bIndex == 1) {
                    sectionsMap.put(currentSection, sectionsMap.get(currentSection) + 1);
                } else {
                    sectionsMap.put(bIndex, 1);
                    currentSection = bIndex;
                }
            }
        }
        Section[] sections = new Section[sectionsMap.size()];
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : sectionsMap.entrySet()) {
            Section section = new Section();
            section.start = entry.getKey();
            section.length = entry.getValue();
            section.shift = section.start - section.length + 1;
            sections[i] = section;
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
                sectionAux.length = (i == 0) ? section.length - (sectionSize * (sectionQuantity - 1)) : sectionSize;
                sectionAux.start = section.start - sizeAux;
                sizeAux += sectionAux.length;
                sections.add(sectionAux);
            }
            for (int i = sectionQuantity - 1; i >= 0; i--) {
                Section sectionAux = sections.get(i);
                sectionAux.shift = (i == sectionQuantity - 1) ? section.shift : sections.get(i + 1).shift + sections.get(i + 1).length;
                //sectionAux.mask = MaskInfoInt.getMaskRangeBits(sectionAux.start, sectionAux.start - sectionAux.length + 1);
            }
            return sections.toArray(new Section[]{});
        }
    }

    public static Section[] getOrderedSections(int[] bList, int bListStart, int bListEnd) {
        List<Section> finalSectionList = new ArrayList<>();
        Section[] sectionsInfos = BitSorterUtils.getMaskAsSections(bList, bListStart, bListEnd);
        Section[] sections = sectionsInfos;
        for (int i = sections.length - 1; i >= 0; i--) {
            Section section = sections[i];
            Section[] sSections = BitSorterUtils.splitSection(section);
            for (int j = sSections.length - 1; j >= 0; j--) {
                Section sSection = sSections[j];
                finalSectionList.add(sSection);
            }
        }
        return finalSectionList.toArray(new Section[0]);
    }

    public static int logBase2(int n) // returns 0 for bits=0
    {
        int log = 0;
        if ((n & 0xffff0000) != 0) {
            n >>>= 16;
            log = 16;
        }
        if (n >= 256) {
            n >>>= 8;
            log += 8;
        }
        if (n >= 16) {
            n >>>= 4;
            log += 4;
        }
        if (n >= 4) {
            n >>>= 2;
            log += 2;
        }
        return log + (n >>> 1);
    }
}
