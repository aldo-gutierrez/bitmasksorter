package com.aldogg.sorter;

import static com.aldogg.sorter.intType.IntSorterUtils.compareAndSwapSigned;
import static com.aldogg.sorter.intType.IntSorterUtils.compareAndSwapUnsigned;

//http://users.telenet.be/bertdobbelaere/SorterHunter/sorting_networks.html
public class SortingNetworks {
    public final static int[][][] swaps = {
            null, //0
            null, //1
            {{0, 1}}, //2 x
            {{0, 2}, //3 x
                    {0, 1},
                    {1, 2}},
            {{0, 2}, {1, 3}, //4 1.25x
                    {0, 1}, {2, 3},
                    {1, 2}},
            {{0, 3}, {1, 4}, //5 1.75x
                    {0, 2}, {1, 3},
                    {0, 1}, {2, 4},
                    {1, 2}, {3, 4},
                    {2, 3}},
            {{0, 5}, {1, 3}, {2, 4}, //6 2x
                    {1, 2}, {3, 4},
                    {0, 3}, {2, 5},
                    {0, 1}, {2, 3}, {4, 5},
                    {1, 2}, {3, 4}},
            {{0, 6}, {2, 3}, {4, 5},//7 2.4x
                    {0, 2}, {1, 4}, {3, 6},
                    {0, 1}, {2, 5}, {3, 4},
                    {1, 2}, {4, 6},
                    {2, 3}, {4, 5},
                    {1, 2}, {3, 4}, {5, 6}},
            {{0, 2}, {1, 3}, {4, 6}, {5, 7}, //8  2.4x
                    {0, 4}, {1, 5}, {2, 6}, {3, 7},
                    {0, 1}, {2, 3}, {4, 5}, {6, 7},
                    {2, 4}, {3, 5},
                    {1, 4}, {3, 6},
                    {1, 2}, {3, 4}, {5, 6}},
            {{0, 3}, {1, 7}, {2, 5}, {4, 8}, //9
                    {0, 7}, {2, 4}, {3, 8}, {5, 6},
                    {0, 2}, {1, 3}, {4, 5}, {7, 8},
                    {1, 4}, {3, 6}, {5, 7},
                    {0, 1}, {2, 4}, {3, 5}, {6, 8},
                    {2, 3}, {4, 5}, {6, 7},
                    {1, 2}, {3, 4}, {5, 6}},
            {{0, 8}, {1, 9}, {2, 7}, {3, 5}, {4, 6}, //10
                    {0, 2}, {1, 4}, {5, 8}, {7, 9},
                    {0, 3}, {2, 4}, {5, 7}, {6, 9},
                    {0, 1}, {3, 6}, {8, 9},
                    {1, 5}, {2, 3}, {4, 8}, {6, 7},
                    {1, 2}, {3, 5}, {4, 6}, {7, 8},
                    {2, 3}, {4, 5}, {6, 7},
                    {3, 4}, {5, 6}},
            {{0, 9}, {1, 6}, {2, 4}, {3, 7}, {5, 8}, //11
                    {0, 1}, {3, 5}, {4, 10}, {6, 9}, {7, 8},
                    {1, 3}, {2, 5}, {4, 7}, {8, 10},
                    {0, 4}, {1, 2}, {3, 7}, {5, 9}, {6, 8},
                    {0, 1}, {2, 6}, {4, 5}, {7, 8}, {9, 10},
                    {2, 4}, {3, 6}, {5, 7}, {8, 9},
                    {1, 2}, {3, 4}, {5, 6}, {7, 8},
                    {2, 3}, {4, 5}, {6, 7}},
            {{0, 8}, {1, 7}, {2, 6}, {3, 11}, {4, 10}, {5, 9}, //12
                    {0, 1}, {2, 5}, {3, 4}, {6, 9}, {7, 8}, {10, 11},
                    {0, 2}, {1, 6}, {5, 10}, {9, 11},
                    {0, 3}, {1, 2}, {4, 6}, {5, 7}, {8, 11}, {9, 10},
                    {1, 4}, {3, 5}, {6, 8}, {7, 10},
                    {1, 3}, {2, 5}, {6, 9}, {8, 10},
                    {2, 3}, {4, 5}, {6, 7}, {8, 9},
                    {4, 6}, {5, 7},
                    {3, 4}, {5, 6}, {7, 8}},
            {{0, 12}, {1, 10}, {2, 9}, {3, 7}, {5, 11}, {6, 8}, //13
                    {1, 6}, {2, 3}, {4, 11}, {7, 9}, {8, 10},
                    {0, 4}, {1, 2}, {3, 6}, {7, 8}, {9, 10}, {11, 12},
                    {4, 6}, {5, 9}, {8, 11}, {10, 12},
                    {0, 5}, {3, 8}, {4, 7}, {6, 11}, {9, 10},
                    {0, 1}, {2, 5}, {6, 9}, {7, 8}, {10, 11},
                    {1, 3}, {2, 4}, {5, 6}, {9, 10},
                    {1, 2}, {3, 4}, {5, 7}, {6, 8},
                    {2, 3}, {4, 5}, {6, 7}, {8, 9},
                    {3, 4}, {5, 6}},
            {{0, 6}, {1, 11}, {2, 12}, {3, 10}, {4, 5}, {7, 13}, {8, 9}, //14
                    {1, 2}, {3, 7}, {4, 8}, {5, 9}, {6, 10}, {11, 12},
                    {0, 4}, {1, 3}, {5, 6}, {7, 8}, {9, 13}, {10, 12},
                    {0, 1}, {2, 9}, {3, 7}, {4, 11}, {6, 10}, {12, 13},
                    {2, 5}, {4, 7}, {6, 9}, {8, 11},
                    {1, 2}, {3, 4}, {6, 7}, {9, 10}, {11, 12},
                    {1, 3}, {2, 4}, {5, 6}, {7, 8}, {9, 11}, {10, 12},
                    {2, 3}, {4, 7}, {6, 9}, {10, 11},
                    {4, 5}, {6, 7}, {8, 9},
                    {3, 4}, {5, 6}, {7, 8}, {9, 10}},
            {{1, 2}, {3, 10}, {4, 14}, {5, 8}, {6, 13}, {7, 12}, {9, 11}, //15
                    {0, 14}, {1, 5}, {2, 8}, {3, 7}, {6, 9}, {10, 12}, {11, 13},
                    {0, 7}, {1, 6}, {2, 9}, {4, 10}, {5, 11}, {8, 13}, {12, 14},
                    {0, 6}, {2, 4}, {3, 5}, {7, 11}, {8, 10}, {9, 12}, {13, 14},
                    {0, 3}, {1, 2}, {4, 7}, {5, 9}, {6, 8}, {10, 11}, {12, 13},
                    {0, 1}, {2, 3}, {4, 6}, {7, 9}, {10, 12}, {11, 13},
                    {1, 2}, {3, 5}, {8, 10}, {11, 12},
                    {3, 4}, {5, 6}, {7, 8}, {9, 10},
                    {2, 3}, {4, 5}, {6, 7}, {8, 9}, {10, 11},
                    {5, 6}, {7, 8}},
            {{0, 13}, {1, 12}, {2, 15}, {3, 14}, {4, 8}, {5, 6}, {7, 11}, {9, 10},//16
                    {0, 5}, {1, 7}, {2, 9}, {3, 4}, {6, 13}, {8, 14}, {10, 15}, {11, 12},
                    {0, 1}, {2, 3}, {4, 5}, {6, 8}, {7, 9}, {10, 11}, {12, 13}, {14, 15},
                    {0, 2}, {1, 3}, {4, 10}, {5, 11}, {6, 7}, {8, 9}, {12, 14}, {13, 15},
                    {1, 2}, {3, 12}, {4, 6}, {5, 7}, {8, 10}, {9, 11}, {13, 14},
                    {1, 4}, {2, 6}, {5, 8}, {7, 10}, {9, 13}, {11, 14},
                    {2, 4}, {3, 6}, {9, 12}, {11, 13},
                    {3, 5}, {6, 8}, {7, 9}, {10, 12},
                    {3, 4}, {5, 6}, {7, 8}, {9, 10}, {11, 12},
                    {6, 7}, {8, 9}}
    };

    public static void sortVerySmallListSigned(final int[] array, final int start, final int end) {
        int listLength = end - start;
        if (listLength == 2) {
            compareAndSwapSigned(array, start, end - 1);
        } else if (listLength == 3) {
            compareAndSwapSigned(array, start, end - 1);
            compareAndSwapSigned(array, start, end - 2);
            compareAndSwapSigned(array, end - 2, end - 1);
        } else if (listLength == 4) {
            compareAndSwapSigned(array, start, start + 1);
            compareAndSwapSigned(array, end - 2, end - 1);
            compareAndSwapSigned(array, start, end - 2);
            compareAndSwapSigned(array, start + 1, end - 1);
            compareAndSwapSigned(array, start + 1, end - 2);
        } else {
            int[][] swap = swaps[listLength];
            for (int i = 0; i < swap.length; i++) {
                int[] parts = swap[i];
                compareAndSwapSigned(array, start + parts[0], start + parts[1]);
            }
        }
    }

    //is this necessary?? verify
    public static void sortVerySmallListUnSigned(final int[] array, final int start, final int end) {
        int listLength = end - start;
        if (listLength == 2) {
            compareAndSwapUnsigned(array, start, end - 1);
        } else if (listLength == 3) {
            compareAndSwapUnsigned(array, start, end - 1);
            compareAndSwapUnsigned(array, start, end - 2);
            compareAndSwapUnsigned(array, end - 2, end - 1);
        } else if (listLength == 4) {
            compareAndSwapUnsigned(array, start, start + 1);
            compareAndSwapUnsigned(array, end - 2, end - 1);
            compareAndSwapUnsigned(array, start, end - 2);
            compareAndSwapUnsigned(array, start + 1, end - 1);
            compareAndSwapUnsigned(array, start + 1, end - 2);
        } else {
            int[][] swap = swaps[listLength];
            for (int i = 0; i < swap.length; i++) {
                int[] parts = swap[i];
                compareAndSwapUnsigned(array, start + parts[0], start + parts[1]);
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
