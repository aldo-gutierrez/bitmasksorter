package com.aldogg;

import java.security.InvalidParameterException;

public class BitSorterParams {

    private int countingSortBits = 16;

    private int dataSizeForThreads = 256;

    private int maxThreads = 4;

    public int getCountingSortBits() {
        return countingSortBits;
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
        return params;
    }

}

