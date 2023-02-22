package com.aldogg.sorter;

import java.util.*;

public class BitSorterUtils {


    public static int getKeySN(final int element, final IntSection[] sections) {
        int result = 0;
        for (int i = 0; i < sections.length; i++) {
            IntSection section = sections[i];
            int bits = (element & section.sortMask) >> section.shiftRight;
            result = result << section.length | bits;
        }
        return result;
    }

    public static IntSectionsInfo getMaskAsSections(final int[] kList, final int kStart, final int kEnd) {
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
        IntSectionsInfo sections = new IntSectionsInfo();
        sections.sections = new IntSection[sectionsMap.size()];
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : sectionsMap.entrySet()) {
            IntSection section = new IntSection();
            section.k = entry.getKey();
            section.length = entry.getValue();
            sections.totalLength += section.length;
//            if (section.length > sections.maxLength) {
//                sections.maxLength = section.length;
//            }
            int aux = entry.getKey() - entry.getValue() + 1;
            section.sortMask = MaskInfoInt.getMaskRangeBits(entry.getKey(), aux);
            section.shiftRight = aux;
            sections.sections[i] = section;
            i++;
        }
        return sections;
    }

    public static LongSectionsInfo getMaskAsSectionsLong(final int[] kList, final int kStart, final int kEnd) {
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
        LongSectionsInfo sections = new LongSectionsInfo();
        sections.sections = new LongSection[sectionsMap.size()];
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : sectionsMap.entrySet()) {
            LongSection section = new LongSection();
            section.k = entry.getKey();
            section.length = entry.getValue();
            sections.totalLength += section.length;
//            if (section.length > sections.maxLength) {
//                sections.maxLength = section.length;
//            }
            int aux = entry.getKey() - entry.getValue() + 1;
            section.sortMask = MaskInfoLong.getMaskRangeBits(entry.getKey(), aux);
            section.shiftRight = aux;
            sections.sections[i] = section;
            i++;
        }
        return sections;
    }


    public static IntSection[] splitSection(IntSection section) {
        if (section.length <= BitSorterMTParams.MAX_BITS_RADIX_SORT) {
            return new IntSection[]{section};
        } else {
            List<IntSection> sections = new ArrayList<>();
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
                IntSection sectionAux = new IntSection();
                sectionAux.length = (i == 0) ?  section.length - (sectionSize * (sectionQuantity - 1)) : sectionSize;
                sectionAux.k = section.k - sizeAux;
                sizeAux+=sectionAux.length;
                sections.add(sectionAux);
            }
            for (int i= sectionQuantity-1; i >=0; i--) {
                IntSection sectionAux = sections.get(i);
                sectionAux.shiftRight = (i == sectionQuantity - 1) ? section.shiftRight : sections.get(i+1).shiftRight + sections.get(i+1).length;
                sectionAux.sortMask = MaskInfoInt.getMaskRangeBits(sectionAux.k, sectionAux.k - sectionAux.length + 1);
            }
            return sections.toArray(new IntSection[]{});
        }
    }

    public static LongSection[] splitSection(LongSection section) {
        if (section.length <= BitSorterMTParams.MAX_BITS_RADIX_SORT) {
            return new LongSection[]{section};
        } else {
            List<LongSection> sections = new ArrayList<>();
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
                LongSection sectionAux = new LongSection();
                sectionAux.length = (i == 0) ? section.length - (sectionSize * (sectionQuantity - 1)) : sectionSize;
                sectionAux.k = section.k - sizeAux;
                sizeAux += sectionAux.length;
                sections.add(sectionAux);
            }
            for (int i = sectionQuantity - 1; i >= 0; i--) {
                LongSection sectionAux = sections.get(i);
                sectionAux.shiftRight = (i == sectionQuantity - 1) ? section.shiftRight : sections.get(i + 1).shiftRight + sections.get(i + 1).length;
                sectionAux.sortMask = MaskInfoLong.getMaskRangeBits(sectionAux.k, sectionAux.k - sectionAux.length + 1);
            }
            return sections.toArray(new LongSection[]{});
        }
    }

    public static IntSectionsInfo getOrderedSections(int[] kList, int kStart, int kEnd) {
        IntSectionsInfo sectionsInfo = new IntSectionsInfo();
        List<IntSection> finalSectionList = new ArrayList<>();
        IntSectionsInfo sectionsInfos = BitSorterUtils.getMaskAsSections(kList, kStart, kEnd);
        IntSection[] sections = sectionsInfos.sections;
        for (int i = sections.length - 1; i >= 0; i--) {
            IntSection section = sections[i];
            IntSection[] sSections = BitSorterUtils.splitSection(section);
            for (int j = sSections.length - 1; j >= 0; j--) {
                IntSection sSection = sSections[j];
                finalSectionList.add(sSection);
                sectionsInfo.totalLength += sSection.length;
//                if (sSection.length > sectionsInfo.maxLength) {
//                    sectionsInfo.maxLength = sSection.length;
//                }
            }
        }
        sectionsInfo.sections = finalSectionList.toArray(new IntSection[0]);
        return sectionsInfo;
    }

    public static LongSectionsInfo getOrderedSectionsLong(int[] kList, int kStart, int kEnd) {
        LongSectionsInfo sectionsInfo = new LongSectionsInfo();
        List<LongSection> finalSectionList = new ArrayList<>();
        LongSectionsInfo sectionsInfos = BitSorterUtils.getMaskAsSectionsLong(kList, kStart, kEnd);
        LongSection[] sections = sectionsInfos.sections;
        for (int i = sections.length - 1; i >= 0; i--) {
            LongSection section = sections[i];
            LongSection[] sSections = BitSorterUtils.splitSection(section);
            for (int j = sSections.length - 1; j >= 0; j--) {
                LongSection sSection = sSections[j];
                finalSectionList.add(sSection);
                sectionsInfo.totalLength += sSection.length;
//                if (sSection.length > sectionsInfo.maxLength) {
//                    sectionsInfo.maxLength = sSection.length;
//                }
            }
        }
        sectionsInfo.sections = finalSectionList.toArray(new LongSection[0]);
        return sectionsInfo;
    }

    public static int logBase2(int bits ) // returns 0 for bits=0
    {
        int log = 0;
        if( ( bits & 0xffff0000 ) != 0 ) { bits >>>= 16; log = 16; }
        if( bits >= 256 ) { bits >>>= 8; log += 8; }
        if( bits >= 16  ) { bits >>>= 4; log += 4; }
        if( bits >= 4   ) { bits >>>= 2; log += 2; }
        return log + ( bits >>> 1 );
    }
}
