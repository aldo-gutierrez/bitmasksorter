package com.aldogg.sorter;

import static com.aldogg.sorter.intType.IntSorterUtils.*;

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
        int length = end - start;
        if (length <= 8) {
            if (length <= 4) {
                if (length <= 2) {
                    if (length == 2) {
                        compareAndSwapSigned(array, start, start + 1);
                    }
                } else {
                    if (length == 3) {
                        compareAndSwapSigned(array, start, start + 2);
                        compareAndSwapSigned(array, start, start + 1);
                        compareAndSwapSigned(array, start + 1, start + 2);
                    } else { //4
                        compareAndSwapSigned(array, start, start + 2);
                        compareAndSwapSigned(array, start + 1, start + 3);
                        compareAndSwapSigned(array, start, start + 1);
                        compareAndSwapSigned(array, start + 2, start + 3);
                        compareAndSwapSigned(array, start + 1, start + 2);
                    }
                }
            } else {
                if (length <= 6) {
                    if (length == 5) {
                        compareAndSwapSigned(array, start, start + 3);
                        compareAndSwapSigned(array, start + 1, start + 4);
                        compareAndSwapSigned(array, start, start + 2);
                        compareAndSwapSigned(array, start + 1, start + 3);
                        compareAndSwapSigned(array, start, start + 1);
                        compareAndSwapSigned(array, start + 2, start + 4);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start + 2, start + 3);
                    }
                    if (length == 6) {
                        compareAndSwapSigned(array, start, start + 5);
                        compareAndSwapSigned(array, start + 1, start + 3);
                        compareAndSwapSigned(array, start + 2, start + 4);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start, start + 3);
                        compareAndSwapSigned(array, start + 2, start + 5);
                        compareAndSwapSigned(array, start, start + 1);
                        compareAndSwapSigned(array, start + 2, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 5);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 3, start + 4);
                    }
                } else {
                    if (length == 7) {
                        compareAndSwapSigned(array, start, start + 6);
                        compareAndSwapSigned(array, start + 2, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 5);
                        compareAndSwapSigned(array, start, start + 2);
                        compareAndSwapSigned(array, start + 1, start + 4);
                        compareAndSwapSigned(array, start + 3, start + 6);
                        compareAndSwapSigned(array, start, start + 1);
                        compareAndSwapSigned(array, start + 2, start + 5);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 4, start + 6);
                        compareAndSwapSigned(array, start + 2, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 5);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start + 5, start + 6);
                    } else { //8
                        compareAndSwapSigned(array, start, start + 2);
                        compareAndSwapSigned(array, start + 1, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 6);
                        compareAndSwapSigned(array, start + 5, start + 7);
                        compareAndSwapSigned(array, start, start + 4);
                        compareAndSwapSigned(array, start + 1, start + 5);
                        compareAndSwapSigned(array, start + 2, start + 6);
                        compareAndSwapSigned(array, start + 3, start + 7);
                        compareAndSwapSigned(array, start, start + 1);
                        compareAndSwapSigned(array, start + 2, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 5);
                        compareAndSwapSigned(array, start + 6, start + 7);
                        compareAndSwapSigned(array, start + 2, start + 4);
                        compareAndSwapSigned(array, start + 3, start + 5);
                        compareAndSwapSigned(array, start + 1, start + 4);
                        compareAndSwapSigned(array, start + 3, start + 6);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start + 5, start + 6);
                    }
                }
            }
        } else {
            if (length <= 12) {
                if (length <= 10) {
                    if (length == 9) {
                        compareAndSwapSigned(array, start, start + 3);
                        compareAndSwapSigned(array, start + 1, start + 7);
                        compareAndSwapSigned(array, start + 2, start + 5);
                        compareAndSwapSigned(array, start + 4, start + 8);
                        compareAndSwapSigned(array, start, start + 7);
                        compareAndSwapSigned(array, start + 2, start + 4);
                        compareAndSwapSigned(array, start + 3, start + 8);
                        compareAndSwapSigned(array, start + 5, start + 6);
                        compareAndSwapSigned(array, start, start + 2);
                        compareAndSwapSigned(array, start + 1, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 5);
                        compareAndSwapSigned(array, start + 7, start + 8);
                        compareAndSwapSigned(array, start + 1, start + 4);
                        compareAndSwapSigned(array, start + 3, start + 6);
                        compareAndSwapSigned(array, start + 5, start + 7);
                        compareAndSwapSigned(array, start, start + 1);
                        compareAndSwapSigned(array, start + 2, start + 4);
                        compareAndSwapSigned(array, start + 3, start + 5);
                        compareAndSwapSigned(array, start + 6, start + 8);
                        compareAndSwapSigned(array, start + 2, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 5);
                        compareAndSwapSigned(array, start + 6, start + 7);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start + 5, start + 6);
                    } else { //10
                        compareAndSwapSigned(array, start, start + 8);
                        compareAndSwapSigned(array, start + 1, start + 9);
                        compareAndSwapSigned(array, start + 2, start + 7);
                        compareAndSwapSigned(array, start + 3, start + 5);
                        compareAndSwapSigned(array, start + 4, start + 6);
                        compareAndSwapSigned(array, start, start + 2);
                        compareAndSwapSigned(array, start + 1, start + 4);
                        compareAndSwapSigned(array, start + 5, start + 8);
                        compareAndSwapSigned(array, start + 7, start + 9);
                        compareAndSwapSigned(array, start, start + 3);
                        compareAndSwapSigned(array, start + 2, start + 4);
                        compareAndSwapSigned(array, start + 5, start + 7);
                        compareAndSwapSigned(array, start + 6, start + 9);
                        compareAndSwapSigned(array, start, start + 1);
                        compareAndSwapSigned(array, start + 3, start + 6);
                        compareAndSwapSigned(array, start + 8, start + 9);
                        compareAndSwapSigned(array, start + 1, start + 5);
                        compareAndSwapSigned(array, start + 2, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 8);
                        compareAndSwapSigned(array, start + 6, start + 7);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 3, start + 5);
                        compareAndSwapSigned(array, start + 4, start + 6);
                        compareAndSwapSigned(array, start + 7, start + 8);
                        compareAndSwapSigned(array, start + 2, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 5);
                        compareAndSwapSigned(array, start + 6, start + 7);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start + 5, start + 6);
                    }
                } else {
                    if (length == 11) {
                        compareAndSwapSigned(array, start, start + 9);
                        compareAndSwapSigned(array, start + 1, start + 6);
                        compareAndSwapSigned(array, start + 2, start + 4);
                        compareAndSwapSigned(array, start + 3, start + 7);
                        compareAndSwapSigned(array, start + 5, start + 8);
                        compareAndSwapSigned(array, start, start + 1);
                        compareAndSwapSigned(array, start + 3, start + 5);
                        compareAndSwapSigned(array, start + 4, start + 10);
                        compareAndSwapSigned(array, start + 6, start + 9);
                        compareAndSwapSigned(array, start + 7, start + 8);
                        compareAndSwapSigned(array, start + 1, start + 3);
                        compareAndSwapSigned(array, start + 2, start + 5);
                        compareAndSwapSigned(array, start + 4, start + 7);
                        compareAndSwapSigned(array, start + 8, start + 10);
                        compareAndSwapSigned(array, start, start + 4);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 3, start + 7);
                        compareAndSwapSigned(array, start + 5, start + 9);
                        compareAndSwapSigned(array, start + 6, start + 8);
                        compareAndSwapSigned(array, start, start + 1);
                        compareAndSwapSigned(array, start + 2, start + 6);
                        compareAndSwapSigned(array, start + 4, start + 5);
                        compareAndSwapSigned(array, start + 7, start + 8);
                        compareAndSwapSigned(array, start + 9, start + 10);
                        compareAndSwapSigned(array, start + 2, start + 4);
                        compareAndSwapSigned(array, start + 3, start + 6);
                        compareAndSwapSigned(array, start + 5, start + 7);
                        compareAndSwapSigned(array, start + 8, start + 9);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start + 5, start + 6);
                        compareAndSwapSigned(array, start + 7, start + 8);
                        compareAndSwapSigned(array, start + 2, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 5);
                        compareAndSwapSigned(array, start + 6, start + 7);
                    } else { //12
                        compareAndSwapSigned(array, start, start + 8);
                        compareAndSwapSigned(array, start + 1, start + 7);
                        compareAndSwapSigned(array, start + 2, start + 6);
                        compareAndSwapSigned(array, start + 3, start + 11);
                        compareAndSwapSigned(array, start + 4, start + 10);
                        compareAndSwapSigned(array, start + 5, start + 9);
                        compareAndSwapSigned(array, start, start + 1);
                        compareAndSwapSigned(array, start + 2, start + 5);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start + 6, start + 9);
                        compareAndSwapSigned(array, start + 7, start + 8);
                        compareAndSwapSigned(array, start + 10, start + 11);
                        compareAndSwapSigned(array, start, start + 2);
                        compareAndSwapSigned(array, start + 1, start + 6);
                        compareAndSwapSigned(array, start + 5, start + 10);
                        compareAndSwapSigned(array, start + 9, start + 11);
                        compareAndSwapSigned(array, start, start + 3);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 4, start + 6);
                        compareAndSwapSigned(array, start + 5, start + 7);
                        compareAndSwapSigned(array, start + 8, start + 11);
                        compareAndSwapSigned(array, start + 9, start + 10);
                        compareAndSwapSigned(array, start + 1, start + 4);
                        compareAndSwapSigned(array, start + 3, start + 5);
                        compareAndSwapSigned(array, start + 6, start + 8);
                        compareAndSwapSigned(array, start + 7, start + 10);
                        compareAndSwapSigned(array, start + 1, start + 3);
                        compareAndSwapSigned(array, start + 2, start + 5);
                        compareAndSwapSigned(array, start + 6, start + 9);
                        compareAndSwapSigned(array, start + 8, start + 10);
                        compareAndSwapSigned(array, start + 2, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 5);
                        compareAndSwapSigned(array, start + 6, start + 7);
                        compareAndSwapSigned(array, start + 8, start + 9);
                        compareAndSwapSigned(array, start + 4, start + 6);
                        compareAndSwapSigned(array, start + 5, start + 7);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start + 5, start + 6);
                        compareAndSwapSigned(array, start + 7, start + 8);
                    }
                }
            } else {
                if (length <= 14) {
                    if (length == 13) {
                        compareAndSwapSigned(array, start, start + 12);
                        compareAndSwapSigned(array, start + 1, start + 10);
                        compareAndSwapSigned(array, start + 2, start + 9);
                        compareAndSwapSigned(array, start + 3, start + 7);
                        compareAndSwapSigned(array, start + 5, start + 11);
                        compareAndSwapSigned(array, start + 6, start + 8);
                        compareAndSwapSigned(array, start + 1, start + 6);
                        compareAndSwapSigned(array, start + 2, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 11);
                        compareAndSwapSigned(array, start + 7, start + 9);
                        compareAndSwapSigned(array, start + 8, start + 10);
                        compareAndSwapSigned(array, start, start + 4);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 3, start + 6);
                        compareAndSwapSigned(array, start + 7, start + 8);
                        compareAndSwapSigned(array, start + 9, start + 10);
                        compareAndSwapSigned(array, start + 11, start + 12);
                        compareAndSwapSigned(array, start + 4, start + 6);
                        compareAndSwapSigned(array, start + 5, start + 9);
                        compareAndSwapSigned(array, start + 8, start + 11);
                        compareAndSwapSigned(array, start + 10, start + 12);
                        compareAndSwapSigned(array, start, start + 5);
                        compareAndSwapSigned(array, start + 3, start + 8);
                        compareAndSwapSigned(array, start + 4, start + 7);
                        compareAndSwapSigned(array, start + 6, start + 11);
                        compareAndSwapSigned(array, start + 9, start + 10);
                        compareAndSwapSigned(array, start, start + 1);
                        compareAndSwapSigned(array, start + 2, start + 5);
                        compareAndSwapSigned(array, start + 6, start + 9);
                        compareAndSwapSigned(array, start + 7, start + 8);
                        compareAndSwapSigned(array, start + 10, start + 11);
                        compareAndSwapSigned(array, start + 1, start + 3);
                        compareAndSwapSigned(array, start + 2, start + 4);
                        compareAndSwapSigned(array, start + 5, start + 6);
                        compareAndSwapSigned(array, start + 9, start + 10);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start + 5, start + 7);
                        compareAndSwapSigned(array, start + 6, start + 8);
                        compareAndSwapSigned(array, start + 2, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 5);
                        compareAndSwapSigned(array, start + 6, start + 7);
                        compareAndSwapSigned(array, start + 8, start + 9);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start + 5, start + 6);
                    } else { //14
                        compareAndSwapSigned(array, start, start + 6);
                        compareAndSwapSigned(array, start + 1, start + 11);
                        compareAndSwapSigned(array, start + 2, start + 12);
                        compareAndSwapSigned(array, start + 3, start + 10);
                        compareAndSwapSigned(array, start + 4, start + 5);
                        compareAndSwapSigned(array, start + 7, start + 13);
                        compareAndSwapSigned(array, start + 8, start + 9);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 3, start + 7);
                        compareAndSwapSigned(array, start + 4, start + 8);
                        compareAndSwapSigned(array, start + 5, start + 9);
                        compareAndSwapSigned(array, start + 6, start + 10);
                        compareAndSwapSigned(array, start + 11, start + 12);
                        compareAndSwapSigned(array, start, start + 4);
                        compareAndSwapSigned(array, start + 1, start + 3);
                        compareAndSwapSigned(array, start + 5, start + 6);
                        compareAndSwapSigned(array, start + 7, start + 8);
                        compareAndSwapSigned(array, start + 9, start + 13);
                        compareAndSwapSigned(array, start + 10, start + 12);
                        compareAndSwapSigned(array, start, start + 1);
                        compareAndSwapSigned(array, start + 2, start + 9);
                        compareAndSwapSigned(array, start + 3, start + 7);
                        compareAndSwapSigned(array, start + 4, start + 11);
                        compareAndSwapSigned(array, start + 6, start + 10);
                        compareAndSwapSigned(array, start + 12, start + 13);
                        compareAndSwapSigned(array, start + 2, start + 5);
                        compareAndSwapSigned(array, start + 4, start + 7);
                        compareAndSwapSigned(array, start + 6, start + 9);
                        compareAndSwapSigned(array, start + 8, start + 11);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start + 6, start + 7);
                        compareAndSwapSigned(array, start + 9, start + 10);
                        compareAndSwapSigned(array, start + 11, start + 12);
                        compareAndSwapSigned(array, start + 1, start + 3);
                        compareAndSwapSigned(array, start + 2, start + 4);
                        compareAndSwapSigned(array, start + 5, start + 6);
                        compareAndSwapSigned(array, start + 7, start + 8);
                        compareAndSwapSigned(array, start + 9, start + 11);
                        compareAndSwapSigned(array, start + 10, start + 12);
                        compareAndSwapSigned(array, start + 2, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 7);
                        compareAndSwapSigned(array, start + 6, start + 9);
                        compareAndSwapSigned(array, start + 10, start + 11);
                        compareAndSwapSigned(array, start + 4, start + 5);
                        compareAndSwapSigned(array, start + 6, start + 7);
                        compareAndSwapSigned(array, start + 8, start + 9);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start + 5, start + 6);
                        compareAndSwapSigned(array, start + 7, start + 8);
                        compareAndSwapSigned(array, start + 9, start + 10);
                    }

                } else {
                    if (length == 15) {
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 3, start + 10);
                        compareAndSwapSigned(array, start + 4, start + 14);
                        compareAndSwapSigned(array, start + 5, start + 8);
                        compareAndSwapSigned(array, start + 6, start + 13);
                        compareAndSwapSigned(array, start + 7, start + 12);
                        compareAndSwapSigned(array, start + 9, start + 11);
                        compareAndSwapSigned(array, start, start + 14);
                        compareAndSwapSigned(array, start + 1, start + 5);
                        compareAndSwapSigned(array, start + 2, start + 8);
                        compareAndSwapSigned(array, start + 3, start + 7);
                        compareAndSwapSigned(array, start + 6, start + 9);
                        compareAndSwapSigned(array, start + 10, start + 12);
                        compareAndSwapSigned(array, start + 11, start + 13);
                        compareAndSwapSigned(array, start, start + 7);
                        compareAndSwapSigned(array, start + 1, start + 6);
                        compareAndSwapSigned(array, start + 2, start + 9);
                        compareAndSwapSigned(array, start + 4, start + 10);
                        compareAndSwapSigned(array, start + 5, start + 11);
                        compareAndSwapSigned(array, start + 8, start + 13);
                        compareAndSwapSigned(array, start + 12, start + 14);
                        compareAndSwapSigned(array, start, start + 6);
                        compareAndSwapSigned(array, start + 2, start + 4);
                        compareAndSwapSigned(array, start + 3, start + 5);
                        compareAndSwapSigned(array, start + 7, start + 11);
                        compareAndSwapSigned(array, start + 8, start + 10);
                        compareAndSwapSigned(array, start + 9, start + 12);
                        compareAndSwapSigned(array, start + 13, start + 14);
                        compareAndSwapSigned(array, start, start + 3);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 4, start + 7);
                        compareAndSwapSigned(array, start + 5, start + 9);
                        compareAndSwapSigned(array, start + 6, start + 8);
                        compareAndSwapSigned(array, start + 10, start + 11);
                        compareAndSwapSigned(array, start + 12, start + 13);
                        compareAndSwapSigned(array, start, start + 1);
                        compareAndSwapSigned(array, start + 2, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 6);
                        compareAndSwapSigned(array, start + 7, start + 9);
                        compareAndSwapSigned(array, start + 10, start + 12);
                        compareAndSwapSigned(array, start + 11, start + 13);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 3, start + 5);
                        compareAndSwapSigned(array, start + 8, start + 10);
                        compareAndSwapSigned(array, start + 11, start + 12);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start + 5, start + 6);
                        compareAndSwapSigned(array, start + 7, start + 8);
                        compareAndSwapSigned(array, start + 9, start + 10);
                        compareAndSwapSigned(array, start + 2, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 5);
                        compareAndSwapSigned(array, start + 6, start + 7);
                        compareAndSwapSigned(array, start + 8, start + 9);
                        compareAndSwapSigned(array, start + 10, start + 11);
                        compareAndSwapSigned(array, start + 5, start + 6);
                        compareAndSwapSigned(array, start + 7, start + 8);
                    } else if (length == 16) {
                        compareAndSwapSigned(array, start, start + 13);
                        compareAndSwapSigned(array, start + 1, start + 12);
                        compareAndSwapSigned(array, start + 2, start + 15);
                        compareAndSwapSigned(array, start + 3, start + 14);
                        compareAndSwapSigned(array, start + 4, start + 8);
                        compareAndSwapSigned(array, start + 5, start + 6);
                        compareAndSwapSigned(array, start + 7, start + 11);
                        compareAndSwapSigned(array, start + 9, start + 10);
                        compareAndSwapSigned(array, start, start + 5);
                        compareAndSwapSigned(array, start + 1, start + 7);
                        compareAndSwapSigned(array, start + 2, start + 9);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start + 6, start + 13);
                        compareAndSwapSigned(array, start + 8, start + 14);
                        compareAndSwapSigned(array, start + 10, start + 15);
                        compareAndSwapSigned(array, start + 11, start + 12);
                        compareAndSwapSigned(array, start, start + 1);
                        compareAndSwapSigned(array, start + 2, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 5);
                        compareAndSwapSigned(array, start + 6, start + 8);
                        compareAndSwapSigned(array, start + 7, start + 9);
                        compareAndSwapSigned(array, start + 10, start + 11);
                        compareAndSwapSigned(array, start + 12, start + 13);
                        compareAndSwapSigned(array, start + 14, start + 15);
                        compareAndSwapSigned(array, start, start + 2);
                        compareAndSwapSigned(array, start + 1, start + 3);
                        compareAndSwapSigned(array, start + 4, start + 10);
                        compareAndSwapSigned(array, start + 5, start + 11);
                        compareAndSwapSigned(array, start + 6, start + 7);
                        compareAndSwapSigned(array, start + 8, start + 9);
                        compareAndSwapSigned(array, start + 12, start + 14);
                        compareAndSwapSigned(array, start + 13, start + 15);
                        compareAndSwapSigned(array, start + 1, start + 2);
                        compareAndSwapSigned(array, start + 3, start + 12);
                        compareAndSwapSigned(array, start + 4, start + 6);
                        compareAndSwapSigned(array, start + 5, start + 7);
                        compareAndSwapSigned(array, start + 8, start + 10);
                        compareAndSwapSigned(array, start + 9, start + 11);
                        compareAndSwapSigned(array, start + 13, start + 14);
                        compareAndSwapSigned(array, start + 1, start + 4);
                        compareAndSwapSigned(array, start + 2, start + 6);
                        compareAndSwapSigned(array, start + 5, start + 8);
                        compareAndSwapSigned(array, start + 7, start + 10);
                        compareAndSwapSigned(array, start + 9, start + 13);
                        compareAndSwapSigned(array, start + 11, start + 14);
                        compareAndSwapSigned(array, start + 2, start + 4);
                        compareAndSwapSigned(array, start + 3, start + 6);
                        compareAndSwapSigned(array, start + 9, start + 12);
                        compareAndSwapSigned(array, start + 11, start + 13);
                        compareAndSwapSigned(array, start + 3, start + 5);
                        compareAndSwapSigned(array, start + 6, start + 8);
                        compareAndSwapSigned(array, start + 7, start + 9);
                        compareAndSwapSigned(array, start + 10, start + 12);
                        compareAndSwapSigned(array, start + 3, start + 4);
                        compareAndSwapSigned(array, start + 5, start + 6);
                        compareAndSwapSigned(array, start + 7, start + 8);
                        compareAndSwapSigned(array, start + 9, start + 10);
                        compareAndSwapSigned(array, start + 11, start + 12);
                        compareAndSwapSigned(array, start + 6, start + 7);
                        compareAndSwapSigned(array, start + 8, start + 9);
                    }
                }
            }
        }
    }

    //is this necessary?? verify
    public static void sortVerySmallListUnSigned(final int[] array, final int start, final int end) {
        int length = end - start;
        if (length <= 8) {
            if (length <= 4) {
                if (length <= 2) {
                    if (length == 2) {
                        compareAndSwapUnsigned(array, start, start + 1);
                    }
                } else {
                    if (length == 3) {
                        compareAndSwapUnsigned(array, start, start + 2);
                        compareAndSwapUnsigned(array, start, start + 1);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                    } else { //4
                        compareAndSwapUnsigned(array, start, start + 2);
                        compareAndSwapUnsigned(array, start + 1, start + 3);
                        compareAndSwapUnsigned(array, start, start + 1);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                    }
                }
            } else {
                if (length <= 6) {
                    if (length == 5) {
                        compareAndSwapUnsigned(array, start, start + 3);
                        compareAndSwapUnsigned(array, start + 1, start + 4);
                        compareAndSwapUnsigned(array, start, start + 2);
                        compareAndSwapUnsigned(array, start + 1, start + 3);
                        compareAndSwapUnsigned(array, start, start + 1);
                        compareAndSwapUnsigned(array, start + 2, start + 4);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                    }
                    if (length == 6) {
                        compareAndSwapUnsigned(array, start, start + 5);
                        compareAndSwapUnsigned(array, start + 1, start + 3);
                        compareAndSwapUnsigned(array, start + 2, start + 4);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start, start + 3);
                        compareAndSwapUnsigned(array, start + 2, start + 5);
                        compareAndSwapUnsigned(array, start, start + 1);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 5);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                    }
                } else {
                    if (length == 7) {
                        compareAndSwapUnsigned(array, start, start + 6);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 5);
                        compareAndSwapUnsigned(array, start, start + 2);
                        compareAndSwapUnsigned(array, start + 1, start + 4);
                        compareAndSwapUnsigned(array, start + 3, start + 6);
                        compareAndSwapUnsigned(array, start, start + 1);
                        compareAndSwapUnsigned(array, start + 2, start + 5);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 4, start + 6);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 5);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start + 5, start + 6);
                    } else { //8
                        compareAndSwapUnsigned(array, start, start + 2);
                        compareAndSwapUnsigned(array, start + 1, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 6);
                        compareAndSwapUnsigned(array, start + 5, start + 7);
                        compareAndSwapUnsigned(array, start, start + 4);
                        compareAndSwapUnsigned(array, start + 1, start + 5);
                        compareAndSwapUnsigned(array, start + 2, start + 6);
                        compareAndSwapUnsigned(array, start + 3, start + 7);
                        compareAndSwapUnsigned(array, start, start + 1);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 5);
                        compareAndSwapUnsigned(array, start + 6, start + 7);
                        compareAndSwapUnsigned(array, start + 2, start + 4);
                        compareAndSwapUnsigned(array, start + 3, start + 5);
                        compareAndSwapUnsigned(array, start + 1, start + 4);
                        compareAndSwapUnsigned(array, start + 3, start + 6);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start + 5, start + 6);
                    }
                }
            }
        } else {
            if (length <= 12) {
                if (length <= 10) {
                    if (length == 9) {
                        compareAndSwapUnsigned(array, start, start + 3);
                        compareAndSwapUnsigned(array, start + 1, start + 7);
                        compareAndSwapUnsigned(array, start + 2, start + 5);
                        compareAndSwapUnsigned(array, start + 4, start + 8);
                        compareAndSwapUnsigned(array, start, start + 7);
                        compareAndSwapUnsigned(array, start + 2, start + 4);
                        compareAndSwapUnsigned(array, start + 3, start + 8);
                        compareAndSwapUnsigned(array, start + 5, start + 6);
                        compareAndSwapUnsigned(array, start, start + 2);
                        compareAndSwapUnsigned(array, start + 1, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 5);
                        compareAndSwapUnsigned(array, start + 7, start + 8);
                        compareAndSwapUnsigned(array, start + 1, start + 4);
                        compareAndSwapUnsigned(array, start + 3, start + 6);
                        compareAndSwapUnsigned(array, start + 5, start + 7);
                        compareAndSwapUnsigned(array, start, start + 1);
                        compareAndSwapUnsigned(array, start + 2, start + 4);
                        compareAndSwapUnsigned(array, start + 3, start + 5);
                        compareAndSwapUnsigned(array, start + 6, start + 8);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 5);
                        compareAndSwapUnsigned(array, start + 6, start + 7);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start + 5, start + 6);
                    } else { //10
                        compareAndSwapUnsigned(array, start, start + 8);
                        compareAndSwapUnsigned(array, start + 1, start + 9);
                        compareAndSwapUnsigned(array, start + 2, start + 7);
                        compareAndSwapUnsigned(array, start + 3, start + 5);
                        compareAndSwapUnsigned(array, start + 4, start + 6);
                        compareAndSwapUnsigned(array, start, start + 2);
                        compareAndSwapUnsigned(array, start + 1, start + 4);
                        compareAndSwapUnsigned(array, start + 5, start + 8);
                        compareAndSwapUnsigned(array, start + 7, start + 9);
                        compareAndSwapUnsigned(array, start, start + 3);
                        compareAndSwapUnsigned(array, start + 2, start + 4);
                        compareAndSwapUnsigned(array, start + 5, start + 7);
                        compareAndSwapUnsigned(array, start + 6, start + 9);
                        compareAndSwapUnsigned(array, start, start + 1);
                        compareAndSwapUnsigned(array, start + 3, start + 6);
                        compareAndSwapUnsigned(array, start + 8, start + 9);
                        compareAndSwapUnsigned(array, start + 1, start + 5);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 8);
                        compareAndSwapUnsigned(array, start + 6, start + 7);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 3, start + 5);
                        compareAndSwapUnsigned(array, start + 4, start + 6);
                        compareAndSwapUnsigned(array, start + 7, start + 8);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 5);
                        compareAndSwapUnsigned(array, start + 6, start + 7);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start + 5, start + 6);
                    }
                } else {
                    if (length == 11) {
                        compareAndSwapUnsigned(array, start, start + 9);
                        compareAndSwapUnsigned(array, start + 1, start + 6);
                        compareAndSwapUnsigned(array, start + 2, start + 4);
                        compareAndSwapUnsigned(array, start + 3, start + 7);
                        compareAndSwapUnsigned(array, start + 5, start + 8);
                        compareAndSwapUnsigned(array, start, start + 1);
                        compareAndSwapUnsigned(array, start + 3, start + 5);
                        compareAndSwapUnsigned(array, start + 4, start + 10);
                        compareAndSwapUnsigned(array, start + 6, start + 9);
                        compareAndSwapUnsigned(array, start + 7, start + 8);
                        compareAndSwapUnsigned(array, start + 1, start + 3);
                        compareAndSwapUnsigned(array, start + 2, start + 5);
                        compareAndSwapUnsigned(array, start + 4, start + 7);
                        compareAndSwapUnsigned(array, start + 8, start + 10);
                        compareAndSwapUnsigned(array, start, start + 4);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 3, start + 7);
                        compareAndSwapUnsigned(array, start + 5, start + 9);
                        compareAndSwapUnsigned(array, start + 6, start + 8);
                        compareAndSwapUnsigned(array, start, start + 1);
                        compareAndSwapUnsigned(array, start + 2, start + 6);
                        compareAndSwapUnsigned(array, start + 4, start + 5);
                        compareAndSwapUnsigned(array, start + 7, start + 8);
                        compareAndSwapUnsigned(array, start + 9, start + 10);
                        compareAndSwapUnsigned(array, start + 2, start + 4);
                        compareAndSwapUnsigned(array, start + 3, start + 6);
                        compareAndSwapUnsigned(array, start + 5, start + 7);
                        compareAndSwapUnsigned(array, start + 8, start + 9);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start + 5, start + 6);
                        compareAndSwapUnsigned(array, start + 7, start + 8);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 5);
                        compareAndSwapUnsigned(array, start + 6, start + 7);
                    } else { //12
                        compareAndSwapUnsigned(array, start, start + 8);
                        compareAndSwapUnsigned(array, start + 1, start + 7);
                        compareAndSwapUnsigned(array, start + 2, start + 6);
                        compareAndSwapUnsigned(array, start + 3, start + 11);
                        compareAndSwapUnsigned(array, start + 4, start + 10);
                        compareAndSwapUnsigned(array, start + 5, start + 9);
                        compareAndSwapUnsigned(array, start, start + 1);
                        compareAndSwapUnsigned(array, start + 2, start + 5);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start + 6, start + 9);
                        compareAndSwapUnsigned(array, start + 7, start + 8);
                        compareAndSwapUnsigned(array, start + 10, start + 11);
                        compareAndSwapUnsigned(array, start, start + 2);
                        compareAndSwapUnsigned(array, start + 1, start + 6);
                        compareAndSwapUnsigned(array, start + 5, start + 10);
                        compareAndSwapUnsigned(array, start + 9, start + 11);
                        compareAndSwapUnsigned(array, start, start + 3);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 4, start + 6);
                        compareAndSwapUnsigned(array, start + 5, start + 7);
                        compareAndSwapUnsigned(array, start + 8, start + 11);
                        compareAndSwapUnsigned(array, start + 9, start + 10);
                        compareAndSwapUnsigned(array, start + 1, start + 4);
                        compareAndSwapUnsigned(array, start + 3, start + 5);
                        compareAndSwapUnsigned(array, start + 6, start + 8);
                        compareAndSwapUnsigned(array, start + 7, start + 10);
                        compareAndSwapUnsigned(array, start + 1, start + 3);
                        compareAndSwapUnsigned(array, start + 2, start + 5);
                        compareAndSwapUnsigned(array, start + 6, start + 9);
                        compareAndSwapUnsigned(array, start + 8, start + 10);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 5);
                        compareAndSwapUnsigned(array, start + 6, start + 7);
                        compareAndSwapUnsigned(array, start + 8, start + 9);
                        compareAndSwapUnsigned(array, start + 4, start + 6);
                        compareAndSwapUnsigned(array, start + 5, start + 7);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start + 5, start + 6);
                        compareAndSwapUnsigned(array, start + 7, start + 8);
                    }
                }
            } else {
                if (length <= 14) {
                    if (length == 13) {
                        compareAndSwapUnsigned(array, start, start + 12);
                        compareAndSwapUnsigned(array, start + 1, start + 10);
                        compareAndSwapUnsigned(array, start + 2, start + 9);
                        compareAndSwapUnsigned(array, start + 3, start + 7);
                        compareAndSwapUnsigned(array, start + 5, start + 11);
                        compareAndSwapUnsigned(array, start + 6, start + 8);
                        compareAndSwapUnsigned(array, start + 1, start + 6);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 11);
                        compareAndSwapUnsigned(array, start + 7, start + 9);
                        compareAndSwapUnsigned(array, start + 8, start + 10);
                        compareAndSwapUnsigned(array, start, start + 4);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 3, start + 6);
                        compareAndSwapUnsigned(array, start + 7, start + 8);
                        compareAndSwapUnsigned(array, start + 9, start + 10);
                        compareAndSwapUnsigned(array, start + 11, start + 12);
                        compareAndSwapUnsigned(array, start + 4, start + 6);
                        compareAndSwapUnsigned(array, start + 5, start + 9);
                        compareAndSwapUnsigned(array, start + 8, start + 11);
                        compareAndSwapUnsigned(array, start + 10, start + 12);
                        compareAndSwapUnsigned(array, start, start + 5);
                        compareAndSwapUnsigned(array, start + 3, start + 8);
                        compareAndSwapUnsigned(array, start + 4, start + 7);
                        compareAndSwapUnsigned(array, start + 6, start + 11);
                        compareAndSwapUnsigned(array, start + 9, start + 10);
                        compareAndSwapUnsigned(array, start, start + 1);
                        compareAndSwapUnsigned(array, start + 2, start + 5);
                        compareAndSwapUnsigned(array, start + 6, start + 9);
                        compareAndSwapUnsigned(array, start + 7, start + 8);
                        compareAndSwapUnsigned(array, start + 10, start + 11);
                        compareAndSwapUnsigned(array, start + 1, start + 3);
                        compareAndSwapUnsigned(array, start + 2, start + 4);
                        compareAndSwapUnsigned(array, start + 5, start + 6);
                        compareAndSwapUnsigned(array, start + 9, start + 10);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start + 5, start + 7);
                        compareAndSwapUnsigned(array, start + 6, start + 8);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 5);
                        compareAndSwapUnsigned(array, start + 6, start + 7);
                        compareAndSwapUnsigned(array, start + 8, start + 9);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start + 5, start + 6);
                    } else { //14
                        compareAndSwapUnsigned(array, start, start + 6);
                        compareAndSwapUnsigned(array, start + 1, start + 11);
                        compareAndSwapUnsigned(array, start + 2, start + 12);
                        compareAndSwapUnsigned(array, start + 3, start + 10);
                        compareAndSwapUnsigned(array, start + 4, start + 5);
                        compareAndSwapUnsigned(array, start + 7, start + 13);
                        compareAndSwapUnsigned(array, start + 8, start + 9);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 3, start + 7);
                        compareAndSwapUnsigned(array, start + 4, start + 8);
                        compareAndSwapUnsigned(array, start + 5, start + 9);
                        compareAndSwapUnsigned(array, start + 6, start + 10);
                        compareAndSwapUnsigned(array, start + 11, start + 12);
                        compareAndSwapUnsigned(array, start, start + 4);
                        compareAndSwapUnsigned(array, start + 1, start + 3);
                        compareAndSwapUnsigned(array, start + 5, start + 6);
                        compareAndSwapUnsigned(array, start + 7, start + 8);
                        compareAndSwapUnsigned(array, start + 9, start + 13);
                        compareAndSwapUnsigned(array, start + 10, start + 12);
                        compareAndSwapUnsigned(array, start, start + 1);
                        compareAndSwapUnsigned(array, start + 2, start + 9);
                        compareAndSwapUnsigned(array, start + 3, start + 7);
                        compareAndSwapUnsigned(array, start + 4, start + 11);
                        compareAndSwapUnsigned(array, start + 6, start + 10);
                        compareAndSwapUnsigned(array, start + 12, start + 13);
                        compareAndSwapUnsigned(array, start + 2, start + 5);
                        compareAndSwapUnsigned(array, start + 4, start + 7);
                        compareAndSwapUnsigned(array, start + 6, start + 9);
                        compareAndSwapUnsigned(array, start + 8, start + 11);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start + 6, start + 7);
                        compareAndSwapUnsigned(array, start + 9, start + 10);
                        compareAndSwapUnsigned(array, start + 11, start + 12);
                        compareAndSwapUnsigned(array, start + 1, start + 3);
                        compareAndSwapUnsigned(array, start + 2, start + 4);
                        compareAndSwapUnsigned(array, start + 5, start + 6);
                        compareAndSwapUnsigned(array, start + 7, start + 8);
                        compareAndSwapUnsigned(array, start + 9, start + 11);
                        compareAndSwapUnsigned(array, start + 10, start + 12);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 7);
                        compareAndSwapUnsigned(array, start + 6, start + 9);
                        compareAndSwapUnsigned(array, start + 10, start + 11);
                        compareAndSwapUnsigned(array, start + 4, start + 5);
                        compareAndSwapUnsigned(array, start + 6, start + 7);
                        compareAndSwapUnsigned(array, start + 8, start + 9);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start + 5, start + 6);
                        compareAndSwapUnsigned(array, start + 7, start + 8);
                        compareAndSwapUnsigned(array, start + 9, start + 10);
                    }

                } else {
                    if (length == 15) {
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 3, start + 10);
                        compareAndSwapUnsigned(array, start + 4, start + 14);
                        compareAndSwapUnsigned(array, start + 5, start + 8);
                        compareAndSwapUnsigned(array, start + 6, start + 13);
                        compareAndSwapUnsigned(array, start + 7, start + 12);
                        compareAndSwapUnsigned(array, start + 9, start + 11);
                        compareAndSwapUnsigned(array, start, start + 14);
                        compareAndSwapUnsigned(array, start + 1, start + 5);
                        compareAndSwapUnsigned(array, start + 2, start + 8);
                        compareAndSwapUnsigned(array, start + 3, start + 7);
                        compareAndSwapUnsigned(array, start + 6, start + 9);
                        compareAndSwapUnsigned(array, start + 10, start + 12);
                        compareAndSwapUnsigned(array, start + 11, start + 13);
                        compareAndSwapUnsigned(array, start, start + 7);
                        compareAndSwapUnsigned(array, start + 1, start + 6);
                        compareAndSwapUnsigned(array, start + 2, start + 9);
                        compareAndSwapUnsigned(array, start + 4, start + 10);
                        compareAndSwapUnsigned(array, start + 5, start + 11);
                        compareAndSwapUnsigned(array, start + 8, start + 13);
                        compareAndSwapUnsigned(array, start + 12, start + 14);
                        compareAndSwapUnsigned(array, start, start + 6);
                        compareAndSwapUnsigned(array, start + 2, start + 4);
                        compareAndSwapUnsigned(array, start + 3, start + 5);
                        compareAndSwapUnsigned(array, start + 7, start + 11);
                        compareAndSwapUnsigned(array, start + 8, start + 10);
                        compareAndSwapUnsigned(array, start + 9, start + 12);
                        compareAndSwapUnsigned(array, start + 13, start + 14);
                        compareAndSwapUnsigned(array, start, start + 3);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 4, start + 7);
                        compareAndSwapUnsigned(array, start + 5, start + 9);
                        compareAndSwapUnsigned(array, start + 6, start + 8);
                        compareAndSwapUnsigned(array, start + 10, start + 11);
                        compareAndSwapUnsigned(array, start + 12, start + 13);
                        compareAndSwapUnsigned(array, start, start + 1);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 6);
                        compareAndSwapUnsigned(array, start + 7, start + 9);
                        compareAndSwapUnsigned(array, start + 10, start + 12);
                        compareAndSwapUnsigned(array, start + 11, start + 13);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 3, start + 5);
                        compareAndSwapUnsigned(array, start + 8, start + 10);
                        compareAndSwapUnsigned(array, start + 11, start + 12);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start + 5, start + 6);
                        compareAndSwapUnsigned(array, start + 7, start + 8);
                        compareAndSwapUnsigned(array, start + 9, start + 10);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 5);
                        compareAndSwapUnsigned(array, start + 6, start + 7);
                        compareAndSwapUnsigned(array, start + 8, start + 9);
                        compareAndSwapUnsigned(array, start + 10, start + 11);
                        compareAndSwapUnsigned(array, start + 5, start + 6);
                        compareAndSwapUnsigned(array, start + 7, start + 8);
                    } else if (length == 16) {
                        compareAndSwapUnsigned(array, start, start + 13);
                        compareAndSwapUnsigned(array, start + 1, start + 12);
                        compareAndSwapUnsigned(array, start + 2, start + 15);
                        compareAndSwapUnsigned(array, start + 3, start + 14);
                        compareAndSwapUnsigned(array, start + 4, start + 8);
                        compareAndSwapUnsigned(array, start + 5, start + 6);
                        compareAndSwapUnsigned(array, start + 7, start + 11);
                        compareAndSwapUnsigned(array, start + 9, start + 10);
                        compareAndSwapUnsigned(array, start, start + 5);
                        compareAndSwapUnsigned(array, start + 1, start + 7);
                        compareAndSwapUnsigned(array, start + 2, start + 9);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start + 6, start + 13);
                        compareAndSwapUnsigned(array, start + 8, start + 14);
                        compareAndSwapUnsigned(array, start + 10, start + 15);
                        compareAndSwapUnsigned(array, start + 11, start + 12);
                        compareAndSwapUnsigned(array, start, start + 1);
                        compareAndSwapUnsigned(array, start + 2, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 5);
                        compareAndSwapUnsigned(array, start + 6, start + 8);
                        compareAndSwapUnsigned(array, start + 7, start + 9);
                        compareAndSwapUnsigned(array, start + 10, start + 11);
                        compareAndSwapUnsigned(array, start + 12, start + 13);
                        compareAndSwapUnsigned(array, start + 14, start + 15);
                        compareAndSwapUnsigned(array, start, start + 2);
                        compareAndSwapUnsigned(array, start + 1, start + 3);
                        compareAndSwapUnsigned(array, start + 4, start + 10);
                        compareAndSwapUnsigned(array, start + 5, start + 11);
                        compareAndSwapUnsigned(array, start + 6, start + 7);
                        compareAndSwapUnsigned(array, start + 8, start + 9);
                        compareAndSwapUnsigned(array, start + 12, start + 14);
                        compareAndSwapUnsigned(array, start + 13, start + 15);
                        compareAndSwapUnsigned(array, start + 1, start + 2);
                        compareAndSwapUnsigned(array, start + 3, start + 12);
                        compareAndSwapUnsigned(array, start + 4, start + 6);
                        compareAndSwapUnsigned(array, start + 5, start + 7);
                        compareAndSwapUnsigned(array, start + 8, start + 10);
                        compareAndSwapUnsigned(array, start + 9, start + 11);
                        compareAndSwapUnsigned(array, start + 13, start + 14);
                        compareAndSwapUnsigned(array, start + 1, start + 4);
                        compareAndSwapUnsigned(array, start + 2, start + 6);
                        compareAndSwapUnsigned(array, start + 5, start + 8);
                        compareAndSwapUnsigned(array, start + 7, start + 10);
                        compareAndSwapUnsigned(array, start + 9, start + 13);
                        compareAndSwapUnsigned(array, start + 11, start + 14);
                        compareAndSwapUnsigned(array, start + 2, start + 4);
                        compareAndSwapUnsigned(array, start + 3, start + 6);
                        compareAndSwapUnsigned(array, start + 9, start + 12);
                        compareAndSwapUnsigned(array, start + 11, start + 13);
                        compareAndSwapUnsigned(array, start + 3, start + 5);
                        compareAndSwapUnsigned(array, start + 6, start + 8);
                        compareAndSwapUnsigned(array, start + 7, start + 9);
                        compareAndSwapUnsigned(array, start + 10, start + 12);
                        compareAndSwapUnsigned(array, start + 3, start + 4);
                        compareAndSwapUnsigned(array, start + 5, start + 6);
                        compareAndSwapUnsigned(array, start + 7, start + 8);
                        compareAndSwapUnsigned(array, start + 9, start + 10);
                        compareAndSwapUnsigned(array, start + 11, start + 12);
                        compareAndSwapUnsigned(array, start + 6, start + 7);
                        compareAndSwapUnsigned(array, start + 8, start + 9);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int listLength = 2; listLength <= 16; listLength++) {
            int[][] swap = swaps[listLength];
            sb.append("if (length == " + listLength + ") {\n");
            for (int i = 0; i < swap.length; i++) {
                int[] parts = swap[i];
                //        left = 0; right = 1; aL = array[left]; aR = array[right]; if (aL > aR) { array[left] = aR; array[right] = aL;}
                //sb.append("\tcompareAndSwapUnsigned(array, start "+(parts[0] > 0 ? "+ "+parts[0]:"")
                //        +", start " +(parts[1] > 0 ? "+ "+parts[1]:"") + ");\n");
                sb.append("left = start" + (parts[0] > 0 ? " + " + parts[0] : "") + "; right = start" + (parts[1] > 0 ? "+ " + parts[1] : "") + "; aL = array[left]; aR = array[right]; if (aL > aR) { array[left] = aR; array[right] = aL;};\n");
            }
            sb.append("}\n");
        }
        System.out.println(sb);
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
