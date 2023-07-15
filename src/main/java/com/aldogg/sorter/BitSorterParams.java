package com.aldogg.sorter;

import java.security.InvalidParameterException;

public class BitSorterParams {
    public static final int VERY_SMALL_N_SIZE = 16;

    public static int RADIX_SORT_MAX_BITS = 8;

    static {
        //looks like MAX_BITS_RADIX_SORT depends on cache size, an approximation is to use the number of cores
        int cores = Runtime.getRuntime().availableProcessors();
        if (cores <= 4) {
            //8bits looks faster on Single Thread on Core i5-5200U
            RADIX_SORT_MAX_BITS = 8;
        } else if (cores <= 6) {
            RADIX_SORT_MAX_BITS = 9;
        } else if (cores <= 8) {
            RADIX_SORT_MAX_BITS = 10;
        } else if (cores <= 16) {
            //11bits looks faster than 8 on AMD 4800H, 15 is slower
            RADIX_SORT_MAX_BITS = 11;
        } else {
            RADIX_SORT_MAX_BITS = 12;
        }
    }

    private int shortKBits = 16;

    public int getShortKBits() {
        return shortKBits;
    }

    public void setShortKBits(int shortKBits) {
        if (shortKBits < 4) {
            throw new InvalidParameterException("shortKBits needs to be >=4");
        } else if (shortKBits > 24) {
            throw new InvalidParameterException("shortKBits needs to be <=24");
        }
        this.shortKBits = shortKBits;
    }

    public BitSorterParams() {

    }

    public static BitSorterParams getSTParams() {
        return new BitSorterParams();
    }


}

