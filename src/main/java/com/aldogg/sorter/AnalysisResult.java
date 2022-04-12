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

    public AnalysisResult analyze(int[] list, int start, int end, boolean signed) {
        return signed ? analyzeSigned(list, start, end):analyzeUnsigned(list, start, end);
    }


    public AnalysisResult analyzeSigned(int[] list, int start, int end) {
        left = start;
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
            return this;
        }
        calculateMask(i1);

        //ascending
        i1 = list[i];
        if (list[i - 1] < i1) {
            i++;
            for (; i < end; i++) {
                int i2 = list[i];
                calculateMask(i2);
                if (i1 > i2) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                ordered = ASCENDING;
                return this;
            }
        }
        //descending
        else {
            i++;
            for (; i < end; i++) {
                int i2 = list[i];
                calculateMask(i2);
                if (i1 < i2) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                ordered = DESCENDING;
                return this;
            }
        }
        for (; i < end; i++) {
            int i2 = list[i];
            calculateMask(i2);
        }
        mask = mask_low | mask_high;
        inv_mask = mask_low | mask_high;
        ordered = UNORDERED;
        return this;
    }


    public AnalysisResult analyzeUnsigned(int[] list, int start, int end) {
        left = start;
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
            return this;
        }
        calculateMask(i1);

        //ascending
        i1 = list[i];
        if (list[i - 1] < i1) {
            i++;
            for (; i < end; i++) {
                int i2 = list[i];
                calculateMask(i2);
                if (Integer.compareUnsigned(i1, i2) == 1) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                ordered = ASCENDING;
                return this;
            }
        }
        //descending
        else {
            i++;
            for (; i < end; i++) {
                int i2 = list[i];
                calculateMask(i2);
                if (Integer.compareUnsigned(i1, i2) == -1) {
                    break;
                }
                i1 = i2;
            }
            if (i == end) {
                ordered = DESCENDING;
                return this;
            }
        }
        for (; i < end; i++) {
            int i2 = list[i];
            calculateMask(i2);
        }
        mask = mask_low | mask_high;
        inv_mask = mask_low | mask_high;
        ordered = UNORDERED;
        return this;
    }

    public void calculateMask(int ix) {
        if ((ix & 0x80000000) == 0) {
            mask_low = mask_low | ix;
            inv_mask_low = inv_mask_low | (~ix);
            left++;
        } else {
            mask_high = mask_high | ix;
            inv_mask_high = inv_mask_high | (~ix);
        }
    }

}
