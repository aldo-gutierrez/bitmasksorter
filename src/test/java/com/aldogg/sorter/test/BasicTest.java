package com.aldogg.sorter.test;

import com.aldogg.sorter.*;
import com.aldogg.sorter.generators.Generator;
import com.aldogg.sorter.generators.GeneratorParams;
import com.aldogg.sorter.intType.CountSort;
import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;
import com.aldogg.sorter.sorters.JavaParallelSorterInt;
import com.aldogg.sorter.sorters.JavaSorterInt;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import static com.aldogg.sorter.BitSorterUtils.*;
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
            public void sort(int[] array, int start, int end) {
                int[] maskParts = getMaskBit(array, start, end);
                int mask = maskParts[0] & maskParts[1];
                int[] kList = getMaskAsArray(mask);
                int length = end - start;
                int[] aux = new int[length];
                radixSort(array, start, end, kList, 0, kList.length - 1, aux);
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
            public void sort(int[] array, int start, int end) {
                int[] maskParts = getMaskBit(array, start, end);
                int mask = maskParts[0] & maskParts[1];
                int[] kList = getMaskAsArray(mask);
                int[] aux = new int[end - start];
                for (int i = kList.length - 1; i >= 0; i--) {
                    int sortMask = 1 << kList[i];
                    IntSorterUtils.partitionStable(array, start, end, sortMask, aux);
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
            public void sort(int[] array, int start, int end) {
                int[] maskParts = getMaskBit(array, 0, array.length);
                int mask = maskParts[0] & maskParts[1];
                int[] kList = getMaskAsArray(mask);
                CountSort.countSort(array, start, end, kList, 0);
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

        int iterations = 200;
        int[] limitHigh = new int[]{1, 3, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535};
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(123456789);
        params.size = 16;
        params.limitLow = 0;
        params.function = Generator.GeneratorFunctions.RANDOM_RANGE_INT;

        params.limitHigh = 256;
        testSpeedInt(iterations, params, testSortResults, sorters, null);

        params.limitHigh = 15;
        testSpeedInt(iterations, params, testSortResults, sorters, null);

        params.limitHigh = 4096;
        testSpeedInt(iterations, params, testSortResults, sorters, null);


        iterations = 4000;
        System.out.println("----------------------");

        for (int limitH : limitHigh) {
            params.limitHigh = limitH;

            testSortResults = new TestSortResults(sorters.length);
            params.size = 16;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 32;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 64;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 128;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 256;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 512;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1024;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 2048;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 4096;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 8192;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 16384;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 32768;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 65536;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }



}
