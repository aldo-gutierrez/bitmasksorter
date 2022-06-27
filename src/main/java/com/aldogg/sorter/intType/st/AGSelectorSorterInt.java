package com.aldogg.sorter.intType.st;

import com.aldogg.sorter.intType.IntBitMaskSorter;
import com.aldogg.sorter.intType.IntSorter;


/*
Algorithm Selector Sorter
It chooses the best algorithm to use depending on N and K
SorterTest2.speedTestPositiveIntSTBase2 generates this logic
 */
public class AGSelectorSorterInt extends IntBitMaskSorter {
    IntSorter intSorter;

    @Override
    public void sort(int[] array, int start, int end, int[] kList) {
        int n = end - start;
        int k = kList.length;
        if (n < 2) {
            return;
        }
        if (n <= 32) {
            if (unsigned) {
                intSorter = new RadixBitSorterInt();
                intSorter.setUnsigned(unsigned);
                intSorter.sort(array, start, end, kList);
            } else {
                intSorter = new JavaSorterInt();
                intSorter.sort(array, start, end, kList);
            }
        }

        if (k >= 19) { //2^19 = 524288 values
            if (n >= 268435456) {
                intSorter = new RadixByteSorterInt();
                intSorter.setUnsigned(unsigned);
                intSorter.sort(array, start, end, kList);
            } else {
                if (n <= 1024) {
                    intSorter = new RadixByteSorterInt();
                    intSorter.setUnsigned(unsigned);
                    intSorter.sort(array, start, end, kList);
                } else {
                    intSorter = new RadixBitSorterInt();
                    intSorter.setUnsigned(unsigned);
                    intSorter.sort(array, start, end, kList);
                }
            }
        } else { // 2^18
            if (k == 1) {
                if (unsigned) {
                    intSorter = new RadixBitSorterInt();
                    intSorter.setUnsigned(unsigned);
                    intSorter.sort(array, start, end, kList);
                } else {
                    intSorter = new JavaSorterInt();
                    intSorter.sort(array, start, end, kList);
                }
            } else {
                if (n >= 4194304) {
                    //QuickBitSorter/
                    intSorter = new QuickBitSorterInt();
                    intSorter.setUnsigned(unsigned);
                    intSorter.sort(array, start, end, kList);
                } else {
                    if (k <= 16) {
                        if (n > 32678) {
                            //QuickBitSorter/
                            intSorter = new QuickBitSorterInt();
                            intSorter.setUnsigned(unsigned);
                            intSorter.sort(array, start, end, kList);
                        } else {
                            if (k >= 13) {//13 14 15 16
                                intSorter = new RadixByteSorterInt();
                                intSorter.setUnsigned(unsigned);
                                intSorter.sort(array, start, end, kList);
                            } else {
                                if (k >= 10) {
                                    if (n <= 512) {
                                        intSorter = new RadixByteSorterInt();
                                        intSorter.setUnsigned(unsigned);
                                        intSorter.sort(array, start, end, kList);
                                    } else {
                                        intSorter = new RadixBitSorterInt();
                                        intSorter.setUnsigned(unsigned);
                                        intSorter.sort(array, start, end, kList);
                                    }
                                } else if (k > 2) {
                                    if (n >= 4096) {
                                        intSorter = new QuickBitSorterInt();
                                        intSorter.setUnsigned(unsigned);
                                        intSorter.sort(array, start, end, kList);
                                    } else {
                                        intSorter = new RadixBitSorterInt();
                                        intSorter.setUnsigned(unsigned);
                                        intSorter.sort(array, start, end, kList);
                                    }
                                } else {//k==2
                                    intSorter = new RadixBitSorterInt();
                                    intSorter.setUnsigned(unsigned);
                                    intSorter.sort(array, start, end, kList);
                                }
                            }
                        }
                    } else {//k=17 or k=18
                        if (n <= 1024) {
                            intSorter = new RadixByteSorterInt();
                            intSorter.setUnsigned(unsigned);
                            intSorter.sort(array, start, end, kList);
                        } else {
                            intSorter = new RadixBitSorterInt();
                            intSorter.setUnsigned(unsigned);
                            intSorter.sort(array, start, end, kList);
                        }
                    }
                    //QuickBitSorter/
                    intSorter = new QuickBitSorterInt();
                    intSorter.setUnsigned(unsigned);
                    intSorter.sort(array, start, end, kList);
                }
            }
        }
    }

}
