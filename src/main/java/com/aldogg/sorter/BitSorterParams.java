package com.aldogg.sorter;

public class BitSorterParams {

    public static final int RADIX_SORT_MAX_BITS;

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

    //NEES TO BE >=4 and <=24
    public static final int SHORT_K_BITS = 20;

    public static int SHORT_N = 262144;

    public BitSorterParams() {

    }

    public static BitSorterParams getSTParams() {
        return new BitSorterParams();
    }


}

