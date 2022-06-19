package com.aldogg.sorter;

import java.security.InvalidParameterException;

public class BitSorterParams {
    public static final int VERY_SMALL_N_SIZE = 16;

    //11bits looks faster than 8 on AMD 4800H, 15 is slower
    public static final int MAX_BITS_RADIX_SORT = 11;

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

