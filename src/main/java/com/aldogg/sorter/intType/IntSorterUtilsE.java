package com.aldogg.sorter.intType;

import static com.aldogg.sorter.AnalysisResult.*;

public class IntSorterUtilsE {

    public static int[] analyzeSigned(int[] list, int start, int end) {
        int ordered;
        int mask_low = 0;
        int inv_mask_low = 0;
        int mask_high = 0;
        int inv_mask_high = 0;
        int left = start;

        int i1 = list[start];
        int i = start + 1;
        while (i < end) {
            int i2 = list[i];
            if (i2 != i1) {
                break;
            }
            i1 = i2;
            i++;
        }
        if (i == end) {
            ordered = ALL_EQUAL;
            return new int[]{ordered};
        }
        if ((i1 & 0x80000000) == 0) {
            mask_low = mask_low | i1;
            inv_mask_low = inv_mask_low | (~i1);
            left++;
        } else {
            mask_high = mask_high | i1;
            inv_mask_high = inv_mask_high | (~i1);
        }

        //ascending
        i1 = list[i];
        if (list[i - 1] < i1) {
            i++;
            for (; i < end; i++) {
                int i2 = list[i];
                if ((i2 & 0x80000000) == 0) {
                    mask_low = mask_low | i2;
                    inv_mask_low = inv_mask_low | (~i2);
                    left++;
                } else {
                    mask_high = mask_high | i2;
                    inv_mask_high = inv_mask_high | (~i2);
                }
                if (i1 > i2) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                ordered = ASCENDING;
                return new int[]{ordered};
            }
        }
        //descending
        else {
            i++;
            for (; i < end; i++) {
                int i2 = list[i];
                if ((i2 & 0x80000000) == 0) {
                    mask_low = mask_low | i2;
                    inv_mask_low = inv_mask_low | (~i2);
                    left++;
                } else {
                    mask_high = mask_high | i2;
                    inv_mask_high = inv_mask_high | (~i2);
                }
                if (i1 < i2) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                ordered = DESCENDING;
                return new int[] {ordered};
            }
        }
        for (; i < end; i++) {
            int i2 = list[i];
            if ((i2 & 0x80000000) == 0) {
                mask_low = mask_low | i2;
                inv_mask_low = inv_mask_low | (~i2);
                left++;
            } else {
                mask_high = mask_high | i2;
                inv_mask_high = inv_mask_high | (~i2);
            }
        }
        ordered = UNORDERED;
        return new int[]{ordered, left, mask_low | mask_high, inv_mask_low | inv_mask_high, mask_low, inv_mask_low, mask_high, inv_mask_high};
    }


    public static int[] analyzeUnsigned(int[] list, int start, int end) {
        int ordered;
        int mask_low = 0;
        int inv_mask_low = 0;
        int mask_high = 0;
        int inv_mask_high = 0;
        int left = start;

        int i1 = list[start];
        int i = start + 1;
        while (i < end) {
            int i2 = list[i];
            if (i2 != i1) {
                break;
            }
            i1 = i2;
            i++;
        }
        if (i == end) {
            ordered = ALL_EQUAL;
            return new int[]{ordered};
        }
        if ((i1 & 0x80000000) == 0) {
            mask_low = mask_low | i1;
            inv_mask_low = inv_mask_low | (~i1);
            left++;
        } else {
            mask_high = mask_high | i1;
            inv_mask_high = inv_mask_high | (~i1);
        }

        //ascending
        i1 = list[i];
        if (list[i - 1] < i1) {
            i++;
            for (; i < end; i++) {
                int i2 = list[i];
                if ((i2 & 0x80000000) == 0) {
                    mask_low = mask_low | i2;
                    inv_mask_low = inv_mask_low | (~i2);
                    left++;
                } else {
                    mask_high = mask_high | i2;
                    inv_mask_high = inv_mask_high | (~i2);
                }
                if (Integer.compareUnsigned(i1, i2) == 1) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                ordered = ASCENDING;
                return new int[]{ordered};
            }
        }
        //descending
        else {
            i++;
            for (; i < end; i++) {
                int i2 = list[i];
                if ((i2 & 0x80000000) == 0) {
                    mask_low = mask_low | i2;
                    inv_mask_low = inv_mask_low | (~i2);
                    left++;
                } else {
                    mask_high = mask_high | i2;
                    inv_mask_high = inv_mask_high | (~i2);
                }
                if (Integer.compareUnsigned(i1, i2) == -1) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                ordered = DESCENDING;
                return new int[]{ordered};
            }
        }
        for (; i < end; i++) {
            int i2 = list[i];
            if ((i2 & 0x80000000) == 0) {
                mask_low = mask_low | i2;
                inv_mask_low = inv_mask_low | (~i2);
                left++;
            } else {
                mask_high = mask_high | i2;
                inv_mask_high = inv_mask_high | (~i2);
            }
        }
        ordered = UNORDERED;
        return new int[]{ordered, left, mask_low | mask_high, inv_mask_low | inv_mask_high, mask_low, inv_mask_low, mask_high, inv_mask_high};
    }
}
