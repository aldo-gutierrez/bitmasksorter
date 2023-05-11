package com.aldogg.sorter.test.unit;

import com.aldogg.sorter.IntSection;
import org.junit.jupiter.api.Test;

import static com.aldogg.sorter.BitSorterUtils.getKeySN;
import static com.aldogg.sorter.BitSorterUtils.getMaskAsSections;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BitMaskTest {
    @Test
    public void maskTest() {
        int[] bList = new int[]{4, 2, 1, 0};
        IntSection[] parts = getMaskAsSections(bList, 0, bList.length - 1).sections;
        int key = getKeySN(20, parts);
        assertEquals(key, 12);
    }
}
