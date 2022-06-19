package com.aldogg.sorter;

public class BitSorterMTParams extends BitSorterParams {
    private int maxThreads = 1;
    private int dataSizeForThreads = 65536;

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getMaxThreadsBits() {
        return (int) (Math.log(maxThreads) / Math.log(2));
    }

    public int getDataSizeForThreads() {
        return dataSizeForThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public void setDataSizeForThreads(int dataSizeForThreads) {
        this.dataSizeForThreads = dataSizeForThreads;
    }

    public static BitSorterMTParams getMTParams() {
        int cpuMaxThreadCount = Runtime.getRuntime().availableProcessors();
        BitSorterMTParams params = new BitSorterMTParams();
        params.setMaxThreads(cpuMaxThreadCount);
        return params;
    }

}
