package com.aldogg.sorter.byteType.st;

import com.aldogg.sorter.byteType.ByteSorter;

public class RadixByteSorterByte implements ByteSorter {

    protected boolean unsigned = false;

    @Override
    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }


//    @Override
//    public void sort(byte[] array, int start, int end) {
//        int n = end - start;
//        byte[] aux = new byte[n];
//        int[] count = new int[256];
//        for (int i = start; i < end; i++) {
//            count[array[i] + 128]++;
//        }
//        int cLength = count.length;
//        for (int i = 0, sum = 0; i < cLength; i++) {
//            int countI = count[i];
//            count[i] = sum;
//            sum += countI;
//        }
//        for (int i = start; i < end; i++) {
//            byte element = array[i];
//            aux[count[element + 128]++] = element;
//        }
//        System.arraycopy(aux, 0, array, start, n);
//    }


    @Override
    public void sort(byte[] array, int start, int end) {
        int[] count = new int[256];
        if (!isUnsigned()) {
            for (int i = start; i < end; ++i) {
                count[array[i] + 128]++;
            }
            int i = start;
            for (int j = 0; j < 256; ++j) {
                int countJ = count[j];
                if (countJ > 0) {
                    byte jb = (byte) (j - 128);
                    for (int k = 0; k < countJ; ++k) {
                        array[i] = jb;
                        i++;
                    }
                }
            }
        } else {
            for (int i = start; i < end; ++i) {
                count[array[i] & 0xFF]++;
            }
            int i = start;
            for (int j = 0; j < 256; ++j) {
                int countJ = count[j];
                if (countJ > 0) {
                    byte jb = (byte) (j);
                    for (int k = 0; k < countJ; ++k) {
                        array[i] = jb;
                        i++;
                    }
                }
            }
        }
    }
}
