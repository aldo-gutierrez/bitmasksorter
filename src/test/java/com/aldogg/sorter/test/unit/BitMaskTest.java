package com.aldogg.sorter.test.unit;

import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.MaskInfoLong;
import com.aldogg.sorter.Section;
import com.aldogg.sorter.int_.IntSorterUtils;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static com.aldogg.sorter.BitSorterUtils.getKeySN;
import static com.aldogg.sorter.BitSorterUtils.getMaskAsSections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void getMaskTest2() {
        int[] bList = IntStream.range(0, 31).toArray();
        IntSorterUtils.reverse(bList, 0, bList.length);
        Section[] result = BitSorterUtils.getProcessedSections(bList, 0, bList.length - 1, 11);
        assertEquals(11, result[0].bits);
        assertEquals(0, result[0].shift);
        assertEquals(11, result[1].bits);
        assertEquals(11, result[1].shift);
        assertEquals(9, result[2].bits);
        assertEquals(22, result[2].shift);

        bList = new int[]{1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29};
        IntSorterUtils.reverse(bList, 0, bList.length);
        result = BitSorterUtils.getProcessedSections(bList, 0, bList.length - 1, 11);
        assertTrue(result.length == 3);
        assertEquals(11, result[0].bits);
        assertEquals(1, result[0].shift);
        assertEquals(11, result[1].bits);
        assertEquals(13, result[1].shift);
        assertEquals(5, result[2].bits);
        assertEquals(25, result[2].shift);

        bList = new int[]{2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30};
        IntSorterUtils.reverse(bList, 0, bList.length);
        result = BitSorterUtils.getProcessedSections(bList, 0, bList.length - 1, 11);
        assertTrue(result.length == 3);
        assertEquals(11, result[0].bits);
        assertEquals(2, result[0].shift);
        assertEquals(11, result[1].bits);
        assertEquals(14, result[1].shift);
        assertEquals(5, result[2].bits);
        assertEquals(26, result[2].shift);

        bList = new int[]{2};
        IntSorterUtils.reverse(bList, 0, bList.length);
        result = BitSorterUtils.getProcessedSections(bList, 0, bList.length - 1, 11);
        assertTrue(result.length == 1);
        assertEquals(1, result[0].bits);
        assertEquals(2, result[0].shift);

        bList = IntStream.range(0, 32).toArray();
        IntSorterUtils.reverse(bList, 0, bList.length);
        result = BitSorterUtils.getProcessedSections(bList, 0, bList.length - 1, 8);
        assertTrue(result.length == 4);
        assertEquals(8, result[0].bits);
        assertEquals(0, result[0].shift);
        assertEquals(8, result[1].bits);
        assertEquals(8, result[1].shift);
        assertEquals(8, result[2].bits);
        assertEquals(16, result[2].shift);
        assertEquals(8, result[3].bits);
        assertEquals(24, result[3].shift);
    }

}
