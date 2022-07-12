package com.aldogg.sorter.intType.mt;

import com.aldogg.sorter.intType.IntBitMaskSorter;
import com.aldogg.sorter.intType.IntBitMaskSorterMT;
import com.aldogg.sorter.intType.st.RadixByteSorterInt;

/**
 * If first byte contains enough bits for the number of threads to sort then splits the threads for that byte
 * otherwise uses two bytes, so the maximum number of threads in parallel is 512 one byte + one bit 2^9
 * In each thread it does RadixByteSort by the remaining bytes, 1, 2 or 3 bytes
 */
public class RadixByteSorterMTInt extends IntBitMaskSorterMT {

    @Override
    public void sort(int[] array, int start, int end, int[] kList) {
        //TODO
    }

    @Override
    public IntBitMaskSorter getSTIntSorter() {
        return new RadixByteSorterInt();
    }

}
