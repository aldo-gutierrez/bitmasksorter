package com.aldogg;

import org.junit.jupiter.api.Test;

import static com.aldogg.BitSorterUtils.getMaskAsSections;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaskTest {
    @Test
    public void maskTest() {
        int[] kList = new int[] {4,2,1,0};
        int[][] parts = getMaskAsSections(kList);
        //int key = getKey(20, parts);
        //assertEquals(key, 12);
    }
}
