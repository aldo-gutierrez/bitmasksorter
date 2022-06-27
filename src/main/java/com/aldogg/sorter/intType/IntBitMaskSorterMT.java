package com.aldogg.sorter.intType;

import com.aldogg.sorter.BitSorterMTParams;

import java.util.concurrent.atomic.AtomicInteger;

public class IntBitMaskSorterMT extends IntBitMaskSorter {
    protected final AtomicInteger numThreads = new AtomicInteger(1);

    protected final BitSorterMTParams params = BitSorterMTParams.getMTParams();

}
