package com.aldogg;

import java.security.InvalidParameterException;

public class BitSorterParams {
    public static final int SMALL_LIST_SIZE = 8;
    public static final int COUNT_SORT_SMALL_NUMBER_SHIFT = 4;

    private int countingSortBits = 16;

    private int dataSizeForThreads = 1024;

    private int maxThreads = 1;

    private int maxThreadsBits = 1;

    public int getCountingSortBits() {
        return countingSortBits;
    }

    public int getMaxThreadsBits() {
        return maxThreadsBits;
    }

    public void setCountingSortBits(int countingSortBits) {
        if (countingSortBits < 4) {
            throw new InvalidParameterException("maxBitsForCountingSort needs to be >=4");
        }
        this.countingSortBits = countingSortBits;
    }

    public int getDataSizeForThreads() {
        return dataSizeForThreads;
    }

    public void setDataSizeForThreads(int dataSizeForThreads) {
        this.dataSizeForThreads = dataSizeForThreads;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    private BitSorterParams() {

    }

    public static BitSorterParams getSTParams() {
        BitSorterParams params = new BitSorterParams();
        params.maxThreads = 1;
        return params;
    }

    public static BitSorterParams getMTParams() {
        int cpuMaxThreadCount = Runtime.getRuntime().availableProcessors();
        BitSorterParams params = new BitSorterParams();
        params.maxThreads = cpuMaxThreadCount;
        params.maxThreadsBits = (int)(Math.log(cpuMaxThreadCount) / Math.log(2));
        return params;
    }

    public static int twoPowerX(int k) {
        return 1<<k;
    }

}

