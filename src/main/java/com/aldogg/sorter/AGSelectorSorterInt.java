package com.aldogg.sorter;

import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;
import com.aldogg.sorter.intType.other.JavaSorterInt;

import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.BitSorterUtils.getMaskAsArray;

/*
Algorithm Selector Sorter
It chooses the best algorithm to use depending on N and K
 */
public class AGSelectorSorterInt implements IntSorter {
    boolean unsigned = false;

    IntSorter intSorter;

    @Override
    public void sort(int[] array, int start, int end) {
        int n = end - start;
        if (n < 2) {
            return;
        }
        int ordered = isUnsigned() ? listIsOrderedUnSigned(array, start, end) : listIsOrderedSigned(array, start, end);
        if (ordered == AnalysisResult.DESCENDING) {
            IntSorterUtils.reverse(array, start, end);
        }
        if (ordered != AnalysisResult.UNORDERED) return;

        int[] maskParts = getMaskBit(array, start, end);
        int mask = maskParts[0] & maskParts[1];
        int[] kList = getMaskAsArray(mask);
        if (kList.length == 0) {
            return;
        }
        sort(array, start, end, kList);
    }

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

    @Override
    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }
}
