package com.aldogg.sorter.test;

import com.aldogg.sorter.*;
import com.aldogg.sorter.intType.CountSort;
import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static com.aldogg.sorter.BitSorterUtils.getMaskAsList;
import static com.aldogg.sorter.BitSorterUtils.getMaskBit;
import static com.aldogg.sorter.RadixBitSorterInt.radixSort;

public class BasicTest extends SorterTest{
    @Test
    public void basicTests() {
        IntSorter[] sorters = new IntSorter[] {new MixedBitSorterMTInt(), new QuickBitSorterInt(), new QuickBitSorterMTInt(), new RadixBitSorterInt(), new RadixBitSorterMTInt(), new RadixByteSorterInt()};
        TestSortResults sorterTests = new TestSortResults(sorters.length);
        testIntSort(new int[] {}, sorterTests, sorters);
        testIntSort(new int[] {1}, sorterTests, sorters);
        testIntSort(new int[] {2, 1}, sorterTests, sorters);
        testIntSort(new int[] {1, 2}, sorterTests, sorters);
        testIntSort(new int[] {1, 1}, sorterTests, sorters);
        testIntSort(new int[] {53,11,13}, sorterTests, sorters);
        testIntSort(new int[] {70,11,13,53}, sorterTests, sorters);
        testIntSort(new int[] {54,46,95,96,59,58,29,18,6,12,56,76,55,16,85,88,87,54,21,90,27,79,29,23,41,74}, sorterTests, sorters);
        testIntSort(new int[] {
                70,11,13,53,54,46,95,96,59,58,29,18,6,12,56,76,55,16,85,88,
                87,54,21,90,27,79,29,23,41,74,55,8,87,87,17,73,9,47,21,22,
                77,53,67,24,11,24,47,38,26,42,14,91,36,19,12,35,79,91,71,81,
                70,51,94,43,33,7,47,32,6,66,76,81,89,18,10,83,19,67,87,86,45,
                31,70,13,16,40,31,55,81,75,71,16,31,27,17,5,36,29,63,60},sorterTests, sorters);
        //test bit mask 110110000 and 111110000
        testIntSort(new int[] {432,496,432,496,432,496,432,496,432,496,432,496,432,496,432,496,432,496,432,432,496,496,496,496,496,432}, sorterTests, sorters);
    }

    @Test
    public void testNegativeNumbers() {
        IntSorter[] sorters = new IntSorter[] {new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt(), new JavaParallelSorterInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};
        TestSortResults testSorter = new TestSortResults(sorters.length);
        testIntSort(new int[] {}, testSorter, sorters);
        testIntSort(new int[] {1}, testSorter, sorters);
        testIntSort(new int[] {2, 1}, testSorter, sorters);
        testIntSort(new int[] {1, 2}, testSorter, sorters);
        testIntSort(new int[] {1, 1}, testSorter, sorters);
        testIntSort(new int[] {53,11,13}, testSorter, sorters);
        testIntSort(new int[] {70,11,13,53}, testSorter, sorters);
        testIntSort(new int[] {-70,-11,-13,-53}, testSorter, sorters);
        testIntSort(new int[] {-54,-46,-95,-96,-59,-58,-29,18,6,12,56,76,55,16,85,88,87,54,21,90,27,79,29,23,41,74}, testSorter, sorters);

    }

    @Test
    public void testBooleans() {
        IntSorter[] sorters = new IntSorter[]{new JavaSorterInt(), new QuickBitSorterInt()};
        TestSortResults sorter = new TestSortResults(sorters.length);
        testIntSort(new int[]{33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0}, sorter, sorters);
    }

    @Test
    public void smallListAlgorithmSpeedTest() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("small.csv"));
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");


        IntSorter[] sorters = new IntSorter[] {new IntSorter() {
            @Override
            public void sort(int[] list) {
                final int start = 0;
                final int end = list.length;
                int[] maskParts = getMaskBit(list, start, end);
                int mask = maskParts[0] & maskParts[1];
                int[] kList = getMaskAsList(mask);
                int length = end - start;
                int[] aux = new int[length];
                radixSort(list, start, end, aux, kList, kList.length - 1, 0);
            }
            @Override
            public String name() {
                return "StableByte";
            }

            @Override
            public void setUnsigned(boolean unsigned) {

            }
        },  new IntSorter() {
            @Override
            public void sort(int[] list) {
                int[] maskParts = getMaskBit(list, 0, list.length);
                int mask = maskParts[0] & maskParts[1];
                int[] listK = getMaskAsList(mask);
                int[] aux = new int[list.length];
                for (int i = listK.length - 1; i >= 0; i--) {
                    int sortMask = getMaskBit(listK[i]);
                    IntSorterUtils.partitionStable(list, 0, list.length, sortMask, aux);
                }
            }
            @Override
            public String name() {
                return "StableBit";
            }

            @Override
            public void setUnsigned(boolean unsigned) {

            }
        }, new IntSorter() {
            @Override
            public void sort(int[] list) {
                int[] maskParts = getMaskBit(list, 0, list.length);
                int mask = maskParts[0] & maskParts[1];
                int[] listK = getMaskAsList(mask);
                CountSort.countSort(list, 0, list.length, listK, 0);
            }

            @Override
            public String name() {
                return "CountSort";
            }

            @Override
            public void setUnsigned(boolean unsigned) {

            }
        }};
        TestSortResults testSortResults;

        int iterations = 20;
        int[] limitHigh = new int[] {2, 4, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192,16384,32768,65536};

        for (int limitH : limitHigh) {
            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 16, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 32, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 64, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 128, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 256, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 512, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 1024, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 2048, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 4096, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 8192, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 16384, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 32768, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 65536, 0, limitH, testSortResults, sorters, writer);

            //System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }



}