package com.aldogg.sorter.int_.st;

import com.aldogg.sorter.FieldOptions;
import com.aldogg.sorter.generic.SortingNetworks;
import com.aldogg.sorter.int_.SorterInt;
import com.aldogg.sorter.int_.SorterUtilsInt;

import java.util.Arrays;

import static com.aldogg.sorter.shared.FieldType.UNSIGNED_INTEGER;

public final class SortingNetworksInt implements SorterInt {

    private static final SortingNetworksInt INSTANCE = new SortingNetworksInt();

    public static SortingNetworksInt getInstance() {
        return INSTANCE;
    }

    private SortingNetworksInt() {

    }

    @Override
    public void sort(int[] array, int start, int endP1, FieldOptions options) {
        final int n = endP1 - start;
        if (n < 2) {
            return;
        }
        if (n <= 16) {
            int[][] swaps = SortingNetworks.swaps[n];
            if (options.getFieldType().equals(UNSIGNED_INTEGER)) {
                for (int i = 0; i < swaps.length; i++) {
                    int[] swap = swaps[i];
                    int e0 = swap[0] + start;
                    int e1 = swap[1] + start;
                    if (array[e0] + 0x80000000 > array[e1] + 0x80000000) {
                        SorterUtilsInt.swap(array, e0, e1);
                    }
                }
            } else {
                for (int i = 0; i < swaps.length; i++) {
                    int[] swap = swaps[i];
                    int e0 = swap[0] + start;
                    int e1 = swap[1] + start;
                    if (array[e0] > array[e1]) {
                        SorterUtilsInt.swap(array, e0, e1);
                    }
                }
            }
        } else {
            Arrays.sort(array, start, endP1);
        }
    }
}
