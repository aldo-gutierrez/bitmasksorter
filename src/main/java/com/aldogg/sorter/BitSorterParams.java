package com.aldogg.sorter;

import java.security.InvalidParameterException;

public class BitSorterParams {
    public static final int VERY_SMALL_N_SIZE = 16;

    public static final int MAX_BITS_RADIX_SORT;

    static {
        //looks like MAX_BITS_RADIX_SORT depends on cache size, an approximation is to use the number of cores
        int cores = Runtime.getRuntime().availableProcessors();
        if (cores <= 4) {
            //8bits looks faster on Single Thread on Core i5-5200U
            MAX_BITS_RADIX_SORT = 8;
        } else if (cores <=6) {
            MAX_BITS_RADIX_SORT = 9;
        } else if (cores <=8 ) {
            MAX_BITS_RADIX_SORT = 10;
        } else {
            //11bits looks faster than 8 on AMD 4800H, 15 is slower
            MAX_BITS_RADIX_SORT = 11;
        }
    }

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
        return new BitSorterParams();
    }


}

