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
        r.left = start;
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
            r.mask_low = r.mask_low | i1;
            r.inv_mask_low = r.inv_mask_low | (~i1);
            r.left++;
        } else {
            r.mask_high = r.mask_high | i1;
            r.inv_mask_high = r.inv_mask_high | (~i1);
        }

        //ascending
        i1 = list[i];
        if (list[i - 1] < i1) {
            i++;
            for (; i < end; i++) {
                int i2 = list[i];
                if ((i2 & 0x80000000) == 0) {
                    r.mask_low = r.mask_low | i2;
                    r.inv_mask_low = r.inv_mask_low | (~i2);
                    r.left++;
                } else {
                    r.mask_high = r.mask_high | i2;
                    r.inv_mask_high = r.inv_mask_high | (~i2);
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
                    r.mask_low = r.mask_low | i2;
                    r.inv_mask_low = r.inv_mask_low | (~i2);
                    r.left++;
                } else {
                    r.mask_high = r.mask_high | i2;
                    r.inv_mask_high = r.inv_mask_high | (~i2);
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
                r.mask_low = r.mask_low | i2;
                r.inv_mask_low = r.inv_mask_low | (~i2);
                r.left++;
            } else {
                r.mask_high = r.mask_high | i2;
                r.inv_mask_high = r.inv_mask_high | (~i2);
            }
        }
        r.mask = r.mask_low | r.mask_high;
        r.inv_mask = r.mask_low | r.mask_high;
        r.ordered = UNORDERED;
        return r;
    }


    public static AnalysisResult analyzeUnsigned(int[] list, int start, int end) {
        AnalysisResult r = new AnalysisResult();
        r.left = start;
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
            r.mask_low = r.mask_low | i1;
            r.inv_mask_low = r.inv_mask_low | (~i1);
            r.left++;
        } else {
            r.mask_high = r.mask_high | i1;
            r.inv_mask_high = r.inv_mask_high | (~i1);
        }

        //ascending
        i1 = list[i];
        if (list[i - 1] < i1) {
            i++;
            for (; i < end; i++) {
                int i2 = list[i];
                if ((i2 & 0x80000000) == 0) {
                    r.mask_low = r.mask_low | i2;
                    r.inv_mask_low = r.inv_mask_low | (~i2);
                    r.left++;
                } else {
                    r.mask_high = r.mask_high | i2;
                    r.inv_mask_high = r.inv_mask_high | (~i2);
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
                    r.mask_low = r.mask_low | i2;
                    r.inv_mask_low = r.inv_mask_low | (~i2);
                    r.left++;
                } else {
                    r.mask_high = r.mask_high | i2;
                    r.inv_mask_high = r.inv_mask_high | (~i2);
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
                r.mask_low = r.mask_low | i2;
                r.inv_mask_low = r.inv_mask_low | (~i2);
                r.left++;
            } else {
                r.mask_high = r.mask_high | i2;
                r.inv_mask_high = r.inv_mask_high | (~i2);
            }
        }
        r.mask = r.mask_low | r.mask_high;
        r.inv_mask = r.mask_low | r.mask_high;
        r.ordered = UNORDERED;
        return r;
    }


}
