package com.aldogg.sorter.test;

import org.junit.jupiter.api.Test;

import static com.aldogg.sorter.BitSorterUtils.getKeySN;
import static com.aldogg.sorter.BitSorterUtils.getMaskAsSections;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaskTest {
    @Test
    public void maskTest() {
        int[] kList = new int[] {4,2,1,0};
        int[][] parts = getMaskAsSections(kList);
        int key = getKeySN(20, parts);
        assertEquals(key, 12);
    }
}
