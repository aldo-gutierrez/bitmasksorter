package com.aldogg.sorter.test.unit;

import com.aldogg.sorter.MaskInfoLong;
import com.aldogg.sorter.Section;
import org.junit.jupiter.api.Test;

import static com.aldogg.sorter.BitSorterUtils.getKeySN;
import static com.aldogg.sorter.BitSorterUtils.getMaskAsSections;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BitMaskTest {
    @Test
    public void maskTest() {
        int[] bList = new int[]{4, 2, 1, 0};
        Section[] parts = getMaskAsSections(bList, 0, bList.length - 1);
        int key = getKeySN(20, parts);
        assertEquals(12, key);
    }

    @Test
    public void getMaskTest() {
        assertEquals(3L, MaskInfoLong.getMaskRangeBits(1, 0));
        assertEquals(1L, MaskInfoLong.getMaskRangeBits(0, 0));
        assertEquals(255L, MaskInfoLong.getMaskRangeBits(7, 0));
        assertEquals(0xFF00L, MaskInfoLong.getMaskRangeBits(15, 8));
        assertEquals(0xFF00000000L, MaskInfoLong.getMaskRangeBits(39, 32));
        assertEquals(0x80000000_00000000L, MaskInfoLong.getMaskRangeBits(63, 63));

    }
}
