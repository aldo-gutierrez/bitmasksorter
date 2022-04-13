package com.aldogg.sorter;

public class AnalysisResult {
    public static int UNORDERED = 0;
    public static int ASCENDING = 1;
    public static int DESCENDING = 2;
    public static int ALL_EQUAL = 3;

    int mask;
    int inv_mask;
    int mask_low;
    int inv_mask_low;
    int mask_high;
    int inv_mask_high;
    int left;
    int ordered;




    public static  AnalysisResult analyzeSigned(int[] list, int start, int end) {
        AnalysisResult r = new AnalysisResult();
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
            r.ordered = ALL_EQUAL;
            return r;
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
                r.ordered = ASCENDING;
                return r;
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
                r.ordered = DESCENDING;
                return r;
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
        r.mask_low = mask_low;
        r.inv_mask_low = inv_mask_low;
        r.mask_high = mask_high;
        r.inv_mask_high = inv_mask_high;
        r.left = left;
        r.mask = mask_low | mask_high;
        r.inv_mask = mask_low | mask_high;
        r.ordered = UNORDERED;
        return r;
    }


    public static AnalysisResult analyzeUnsigned(int[] list, int start, int end) {
        AnalysisResult r = new AnalysisResult();
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
            r.ordered = ALL_EQUAL;
            return r;
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
                r.ordered = ASCENDING;
                return r;
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
                r.ordered = DESCENDING;
                return r;
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
        r.mask_low = mask_low;
        r.inv_mask_low = inv_mask_low;
        r.mask_high = mask_high;
        r.inv_mask_high = inv_mask_high;
        r.left = left;
        r.mask = mask_low | mask_high;
        r.inv_mask = mask_low | mask_high;
        r.ordered = UNORDERED;
        return r;
    }


}
