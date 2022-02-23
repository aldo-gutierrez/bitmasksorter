package com.aldogg;

import java.security.InvalidParameterException;

public class BitSorterParams {

    private int countingSortBits = 16;

    private int countingSortBufferSize = (int) Math.pow(2, countingSortBits);

    private int dataSizeForThreads = 256;

    private int maxThreads = 1;

    private int maxThreadsBits = 1;

    public int getCountingSortBits() {
        return countingSortBits;
    }

    public int getCountingSortBufferSize() {
        return countingSortBufferSize;
    }

    public int getMaxThreadsBits() {
        return maxThreadsBits;
    }

    public void setCountingSortBits(int countingSortBits) {
        if (countingSortBits < 4) {
            throw new InvalidParameterException("maxBitsForCountingSort needs to be >=4");
        }
        this.countingSortBits = countingSortBits;
        this.countingSortBufferSize = (int) Math.pow(2, countingSortBits);
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

}

