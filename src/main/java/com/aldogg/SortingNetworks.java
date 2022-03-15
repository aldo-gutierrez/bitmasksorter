package com.aldogg;

import static com.aldogg.intType.IntSorterUtils.compareAndSwapSigned;
import static com.aldogg.intType.IntSorterUtils.compareAndSwapUnsigned;

//http://users.telenet.be/bertdobbelaere/SorterHunter/sorting_networks.html
public class SortingNetworks {
    public final static int[][][] swaps = {
            null, //0
            null, //1
            {{0, 1}}, //2
            {{0, 2}, //3
                    {0, 1},
                    {1, 2}},
            {{0, 2}, {1, 3}, //4
                    {0, 1}, {2, 3},
                    {1, 2}},
            {{0, 3}, {1, 4}, //5
                    {0, 2}, {1, 3},
                    {0, 1}, {2, 4},
                    {1, 2}, {3, 4},
                    {2, 3}},
            {{0, 5}, {1, 3}, {2, 4}, //6
                    {1, 2}, {3, 4},
                    {0, 3}, {2, 5},
                    {0, 1}, {2, 3}, {4, 5},
                    {1, 2}, {3, 4}},
            {{0, 6}, {2, 3}, {4, 5},//7
                    {0, 2}, {1, 4}, {3, 6},
                    {0, 1}, {2, 5}, {3, 4},
                    {1, 2}, {4, 6},
                    {2, 3}, {4, 5},
                    {1, 2}, {3, 4}, {5, 6}},
            {{0, 2}, {1, 3}, {4, 6}, {5, 7}, //8
                    {0, 4}, {1, 5}, {2, 6}, {3, 7},
                    {0, 1}, {2, 3}, {4, 5}, {6, 7},
                    {2, 4}, {3, 5},
                    {1, 4}, {3, 6},
                    {1, 2}, {3, 4}, {5, 6}}
    };

    public static void sortVerySmallListSigned(final int[] list, final int start, final int end) {
        int listLength = end - start;
        if (listLength == 2) {
            compareAndSwapSigned(list, start, end - 1);
        } else if (listLength == 3) {
            compareAndSwapSigned(list, start, end - 1);
            compareAndSwapSigned(list, start, end - 2);
            compareAndSwapSigned(list, end - 2, end - 1);
        } else if (listLength == 4) {
            compareAndSwapSigned(list, start, start + 1);
            compareAndSwapSigned(list, end - 2, end - 1);
            compareAndSwapSigned(list, start, end - 2);
            compareAndSwapSigned(list, start + 1, end - 1);
            compareAndSwapSigned(list, start + 1, end - 2);
        } else {
            int[][] swap = swaps[listLength];
            for (int i = 0; i < swap.length; i++) {
                int[] parts = swap[i];
                compareAndSwapSigned(list, start + parts[0], start + parts[1]);
            }
        }
    }

    //is this necessary?? verify
    public static void sortVerySmallListUnSigned(final int[] list, final int start, final int end) {
        int listLength = end - start;
        if (listLength == 2) {
            compareAndSwapUnsigned(list, start, end - 1);
        } else if (listLength == 3) {
            compareAndSwapUnsigned(list, start, end - 1);
            compareAndSwapUnsigned(list, start, end - 2);
            compareAndSwapUnsigned(list, end - 2, end - 1);
        } else if (listLength == 4) {
            compareAndSwapUnsigned(list, start, start + 1);
            compareAndSwapUnsigned(list, end - 2, end - 1);
            compareAndSwapUnsigned(list, start, end - 2);
            compareAndSwapUnsigned(list, start + 1, end - 1);
            compareAndSwapUnsigned(list, start + 1, end - 2);
        } else {
            int[][] swap = swaps[listLength];
            for (int i = 0; i < swap.length; i++) {
                int[] parts = swap[i];
                compareAndSwapUnsigned(list, start + parts[0], start + parts[1]);
            }
        }
    }

    /*
        } else if (listLength == 5) {
            compareAndSwap(list, start + 1, start + 2);  //1 < 2
            compareAndSwap(list, start + 3, start + 4);  //3 < 4
            if (compareAndSwap(list, start, start + 1)) { //0 < 1?
                compareAndSwap(list, start + 1, start + 2); // 0 < 1 < 2
            }
            compareAndSwap(list, start, start + 3); // 0 < 3, 0 lesser than all
            compareAndSwap(list, start + 2, start + 4); // 2 < 4, 4 greater than all

            compareAndSwap(list, start + 1, start + 2); // 1 < 2
            compareAndSwap(list, start + 1, start + 3); // 1 < 3
            compareAndSwap(list, start + 2, start + 3); // 0 <1 < 2 < 3 <4
        }
    */
}
