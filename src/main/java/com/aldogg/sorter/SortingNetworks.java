package com.aldogg.sorter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

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

    private static Map<Integer, BiConsumer<int[], Integer>> sFunctions = new HashMap<>();
    private static Map<Integer, BiConsumer<int[], Integer>> uFunctions = new HashMap<>();

    static {
        sFunctions.put(2, SortingNetworks::sortVSLS2);
        sFunctions.put(3, SortingNetworks::sortVSLS3);
        sFunctions.put(4, SortingNetworks::sortVSLS4);
        sFunctions.put(5, SortingNetworks::sortVSLS5);
        sFunctions.put(6, SortingNetworks::sortVSLS6);
        sFunctions.put(7, SortingNetworks::sortVSLS7);
        sFunctions.put(8, SortingNetworks::sortVSLS8);
        sFunctions.put(9, SortingNetworks::sortVSLS9);
        sFunctions.put(10, SortingNetworks::sortVSLS10);
        sFunctions.put(11, SortingNetworks::sortVSLS11);
        sFunctions.put(12, SortingNetworks::sortVSLS12);
        sFunctions.put(13, SortingNetworks::sortVSLS13);
        sFunctions.put(14, SortingNetworks::sortVSLS14);
        sFunctions.put(15, SortingNetworks::sortVSLS15);
        sFunctions.put(16, SortingNetworks::sortVSLS16);

        uFunctions.put(2, SortingNetworks::sortVSLU2);
        uFunctions.put(3, SortingNetworks::sortVSLU3);
        uFunctions.put(4, SortingNetworks::sortVSLU4);
        uFunctions.put(5, SortingNetworks::sortVSLU5);
        uFunctions.put(6, SortingNetworks::sortVSLU6);
        uFunctions.put(7, SortingNetworks::sortVSLU7);
        uFunctions.put(8, SortingNetworks::sortVSLU8);
        uFunctions.put(9, SortingNetworks::sortVSLU9);
        uFunctions.put(10, SortingNetworks::sortVSLU10);
        uFunctions.put(11, SortingNetworks::sortVSLU11);
        uFunctions.put(12, SortingNetworks::sortVSLU12);
        uFunctions.put(13, SortingNetworks::sortVSLU13);
        uFunctions.put(14, SortingNetworks::sortVSLU14);
        uFunctions.put(15, SortingNetworks::sortVSLU15);
        uFunctions.put(16, SortingNetworks::sortVSLU16);
        
    }

    public static void sortVerySmallListSigned(final int[] array, final int start, final int end) {
        int length = end - start;
        sFunctions.get(length).accept(array, start);
    }


    //is this necessary?? verify
    public static void sortVerySmallListUnSigned(final int[] array, final int start, final int end) {
        int length = end - start;
        uFunctions.get(length).accept(array, start);
    }

    public static void sortVSLS2(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLS3(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLS4(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLS5(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLS6(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLS7(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLS8(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLS9(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLS10(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLS11(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLS12(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLS13(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLS14(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 13; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 13; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 12; right = start+ 13; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLS15(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 14; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 13; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 14; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 13; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 13; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 12; right = start+ 14; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 13; right = start+ 14; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 12; right = start+ 13; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 13; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLS16(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 13; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 15; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 14; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 13; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 14; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 15; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 12; right = start+ 13; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 14; right = start+ 15; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 12; right = start+ 14; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 13; right = start+ 15; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 11; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 13; right = start+ 14; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 13; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 14; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 13; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 12; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL > aR ) { array[left] = aR; array[right] = aL;}
    }

    public static void sortVSLU2(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLU3(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLU4(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLU5(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLU6(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLU7(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLU8(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLU9(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLU10(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLU11(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLU12(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLU13(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLU14(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 13; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 13; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 12; right = start+ 13; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLU15(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 14; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 13; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 14; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 13; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 13; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 12; right = start+ 14; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 13; right = start+ 14; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 12; right = start+ 13; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 13; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
    }
    public static void sortVSLU16(final int[] array, final int start) {
        int left; int right; int aL;int aR;
        left = start; right = start+ 13; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 15; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 14; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 13; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 14; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 15; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 1; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 12; right = start+ 13; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 14; right = start+ 15; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 3; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 12; right = start+ 14; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 13; right = start+ 15; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 2; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 4; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 11; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 13; right = start+ 14; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 1; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 13; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 14; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 2; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 13; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 5; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 10; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 3; right = start+ 4; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 5; right = start+ 6; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 7; right = start+ 8; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 9; right = start+ 10; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 11; right = start+ 12; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 6; right = start+ 7; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
        left = start + 8; right = start+ 9; aL = array[left]; aR = array[right]; if (aL + 0x80000000> aR + 0x80000000) { array[left] = aR; array[right] = aL;}
    }
    

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        boolean unsigned =false;
        for (int listLength = 2; listLength <= 16; listLength++) {
            int[][] swap = swaps[listLength];
            sb.append("\tpublic static void sortVSL"+(unsigned?"U":"S")+listLength+"(final int[] array, final int start) {\n");
            sb.append("\t\tint left; int right; int aL;int aR;\n");
            //sb.append("if (length == " + listLength + ") {\n");
            for (int i = 0; i < swap.length; i++) {
                int[] parts = swap[i];
                //        left = 0; right = 1; aL = array[left]; aR = array[right]; if (aL > aR) { array[left] = aR; array[right] = aL;}
                //sb.append("\tcompareAndSwapUnsigned(array, start "+(parts[0] > 0 ? "+ "+parts[0]:"")
                //        +", start " +(parts[1] > 0 ? "+ "+parts[1]:"") + ");\n");
                sb.append("\t\tleft = start" + (parts[0] > 0 ? " + " + parts[0] : "") + "; right = start" + (parts[1] > 0 ? "+ " + parts[1] : "") + "; aL = array[left]; aR = array[right]; if (aL "+(unsigned? "+ 0x80000000":"")+"> aR "+(unsigned? "+ 0x80000000":"")+") { array[left] = aR; array[right] = aL;}\n");
            }
            //sb.append("}\n");
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
