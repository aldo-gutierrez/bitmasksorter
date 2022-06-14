package com.aldogg.sorter;

import java.security.InvalidParameterException;

public class BitSorterParams {
    public static final int VERY_SMALL_N_SIZE = 16;
    public static final int COUNT_SORT_SMALL_NUMBER_SHIFT = 4;

    private int countingSortBits = 16;

    public int getShortKBits() {
        return countingSortBits;
    }

    public void setCountingSortBits(int countingSortBits) {
        if (countingSortBits < 4) {
            throw new InvalidParameterException("maxBitsForCountingSort needs to be >=4");
        }
        this.countingSortBits = countingSortBits;
    }

    public BitSorterParams() {

    }

    public static BitSorterParams getSTParams() {
        BitSorterParams params = new BitSorterParams();
        return params;
    }


}

