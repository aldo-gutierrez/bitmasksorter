package com.aldogg.sorter;

import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;

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
        if (n == 2) {

        }

        if (n <= 64) {
            //new JavaIntSorter();
        }

        if (k >= 19) { //2^19 = 524288 values
            if (n >= 2048) {
                if (k >= 28) { //2^28 =268435456 values
                    intSorter = new RadixByteSorterInt();
                    intSorter.setUnsigned(unsigned);
                    intSorter.sort(array, start, end, kList);
                } else {
                    intSorter = new RadixBitSorterInt();
                    intSorter.setUnsigned(unsigned);
                    intSorter.sort(array, start, end, kList);
                }
            } else {

            }
        } else { // 2^18
            //QuickBitSorter/

        }

        if (n >= 2048) {
        } else {
            if (n <= 64) {
                //new JavaIntSorter();
                //RadixBitSorter or RadixByteSorer
            } else {

            }
        }

    }

    @Override
    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }
}
