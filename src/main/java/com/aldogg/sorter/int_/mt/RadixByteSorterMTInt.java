package com.aldogg.sorter.int_.mt;

import com.aldogg.sorter.SortingNetworks;
import com.aldogg.sorter.int_.BitMaskSorterInt;
import com.aldogg.sorter.int_.BitMaskSorterMTInt;
import com.aldogg.sorter.int_.st.RadixByteSorterInt;

/**
 * If first byte contains enough bits for the number of threads to sort then splits the threads for that byte
 * otherwise uses two bytes, so the maximum number of threads in parallel is 512 one byte + one bit 2^9
 * In each thread it does RadixByteSort by the remaining bytes, 1, 2 or 3 bytes
 */
public class RadixByteSorterMTInt extends BitMaskSorterMTInt {

    @Override
    public void sort(int[] array, int start, int endP1, int[] bList, Object params) {
        //TODO
    }

    @Override
    public BitMaskSorterInt getSTIntSorter() {
        RadixByteSorterInt sorter = new RadixByteSorterInt();
        sorter.setUnsigned(isUnsigned());
        sorter.setSNFunctions(isUnsigned() ? SortingNetworks.unsignedSNFunctions : SortingNetworks.signedSNFunctions);
        return sorter;
    }

}
