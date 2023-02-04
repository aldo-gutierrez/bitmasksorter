package com.aldogg.sorter.test;

import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.generators.GeneratorFunctions;
import com.aldogg.sorter.generators.GeneratorParams;
import com.aldogg.sorter.intType.IntCountSort;
import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;
import com.aldogg.sorter.intType.mt.JavaSorterMTInt;
import com.aldogg.sorter.intType.mt.MixedBitSorterMTInt;
import com.aldogg.sorter.intType.mt.QuickBitSorterMTInt;
import com.aldogg.sorter.intType.mt.RadixBitSorterMTInt;
import com.aldogg.sorter.intType.st.JavaSorterInt;
import com.aldogg.sorter.intType.st.QuickBitSorterInt;
import com.aldogg.sorter.intType.st.RadixBitSorterInt;
import com.aldogg.sorter.intType.st.RadixByteSorterInt;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Random;

import static com.aldogg.sorter.intType.st.RadixBitSorterInt.radixSort;

public class BasicTest extends BaseTest {
    @Test
    public void basicTests() {
        IntSorter[] sorters = new IntSorter[]{new JavaSorterInt(), new MixedBitSorterMTInt(), new QuickBitSorterInt(), new QuickBitSorterMTInt(), new RadixBitSorterInt(), new RadixBitSorterMTInt(), new RadixByteSorterInt()};
        TestAlgorithms sorterTests = new TestAlgorithms(sorters);
        testSort(new int[]{}, sorterTests);
        testSort(new int[]{1}, sorterTests);
        testSort(new int[]{2, 1}, sorterTests);
        testSort(new int[]{1, 2}, sorterTests);
        testSort(new int[]{1, 1}, sorterTests);
        testSort(new int[]{53, 11, 13}, sorterTests);
        testSort(new int[]{70, 11, 13, 53}, sorterTests);
        testSort(new int[]{54, 46, 95, 96, 59, 58, 29, 18, 6, 12, 56, 76, 55, 16, 85, 88, 87, 54, 21, 90, 27, 79, 29, 23, 41, 74}, sorterTests);
        testSort(new int[]{
                70, 11, 13, 53, 54, 46, 95, 96, 59, 58, 29, 18, 6, 12, 56, 76, 55, 16, 85, 88,
                87, 54, 21, 90, 27, 79, 29, 23, 41, 74, 55, 8, 87, 87, 17, 73, 9, 47, 21, 22,
                77, 53, 67, 24, 11, 24, 47, 38, 26, 42, 14, 91, 36, 19, 12, 35, 79, 91, 71, 81,
                70, 51, 94, 43, 33, 7, 47, 32, 6, 66, 76, 81, 89, 18, 10, 83, 19, 67, 87, 86, 45,
                31, 70, 13, 16, 40, 31, 55, 81, 75, 71, 16, 31, 27, 17, 5, 36, 29, 63, 60}, sorterTests);
        //test bit mask 110110000 and 111110000
        testSort(new int[]{432, 496, 432, 496, 432, 496, 432, 496, 432, 496, 432, 496, 432, 496, 432, 496, 432, 496, 432, 432, 496, 496, 496, 496, 496, 432}, sorterTests);
    }

    @Test
    public void testNegativeNumbers() {
        IntSorter[] sorters = new IntSorter[]{new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt(), new JavaSorterMTInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};
        TestAlgorithms testSorter = new TestAlgorithms(sorters);
        testSort(new int[]{}, testSorter);
        testSort(new int[]{1}, testSorter);
        testSort(new int[]{2, 1}, testSorter);
        testSort(new int[]{1, 2}, testSorter);
        testSort(new int[]{1, 1}, testSorter);
        testSort(new int[]{53, 11, 13}, testSorter);
        testSort(new int[]{70, 11, 13, 53}, testSorter);
        testSort(new int[]{-70, -11, -13, -53}, testSorter);
        testSort(new int[]{-54, -46, -95, -96, -59, -58, -29, 18, 6, 12, 56, 76, 55, 16, 85, 88, 87, 54, 21, 90, 27, 79, 29, 23, 41, 74}, testSorter);

    }

    @Test
    public void testBooleans() {
        IntSorter[] sorters = new IntSorter[]{new JavaSorterInt(), new QuickBitSorterInt()};
        TestAlgorithms sorter = new TestAlgorithms(sorters);
        testSort(new int[]{33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0}, sorter);
    }

    @Test
    public void smallListAlgorithmSpeedTest() throws IOException {
        BufferedWriter writer = getWriter("small.csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");


        IntSorter[] sorters = new IntSorter[]{new IntSorter() {
            @Override
            public void sort(int[] array, int start, int end) {
                MaskInfoInt maskInfo = MaskInfoInt.getMaskBit(array, start, end);
                int mask = maskInfo.getMask();
                int[] kList = MaskInfoInt.getMaskAsArray(mask);
                int length = end - start;
                int[] aux = new int[length];
                radixSort(array, start, end, kList, 0, kList.length - 1, aux);
            }

            @Override
            public String getName() {
                return "StableByte";
            }

            @Override
            public void setUnsigned(boolean unsigned) {

            }
        }, new IntSorter() {
            @Override
            public void sort(int[] array, int start, int end) {
                MaskInfoInt maskInfo = MaskInfoInt.getMaskBit(array, start, end);
                int mask = maskInfo.getMask();
                int[] kList = MaskInfoInt.getMaskAsArray(mask);
                int[] aux = new int[end - start];
                for (int i = kList.length - 1; i >= 0; i--) {
                    int sortMask = 1 << kList[i];
                    IntSorterUtils.partitionStable(array, start, end, sortMask, aux);
                }
            }

            @Override
            public String getName() {
                return "StableBit";
            }

            @Override
            public void setUnsigned(boolean unsigned) {

            }
        }, new IntSorter() {
            @Override
            public void sort(int[] array, int start, int end) {
                MaskInfoInt maskInfo = MaskInfoInt.getMaskBit(array, start, end);
                int mask = maskInfo.getMask();
                int[] kList = MaskInfoInt.getMaskAsArray(mask);
                IntCountSort.countSort(array, start, end, kList, 0);
            }

            @Override
            public String getName() {
                return "CountSort";
            }

            @Override
            public void setUnsigned(boolean unsigned) {

            }
        }};
        TestAlgorithms testAlgorithms;

        int iterations = HEAT_ITERATIONS;
        int[] limitHigh = new int[]{1, 3, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535};
        testAlgorithms = new TestAlgorithms(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(123456789);
        params.size = 16;
        params.limitLow = 0;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;

        params.limitHigh = 256;
        testSpeedInt(iterations, params, testAlgorithms, null);

        params.limitHigh = 15;
        testSpeedInt(iterations, params, testAlgorithms, null);

        params.limitHigh = 4096;
        testSpeedInt(iterations, params, testAlgorithms, null);


        iterations = ITERATIONS;
        System.out.println("----------------------");

        for (int limitH : limitHigh) {
            params.limitHigh = limitH;

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 16;
            testSpeedInt(iterations, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 32;
            testSpeedInt(iterations, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 64;
            testSpeedInt(iterations, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 128;
            testSpeedInt(iterations, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 256;
            testSpeedInt(iterations, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 512;
            testSpeedInt(iterations, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 1024;
            testSpeedInt(iterations, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 2048;
            testSpeedInt(iterations, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 4096;
            testSpeedInt(iterations, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 8192;
            testSpeedInt(iterations, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 16384;
            testSpeedInt(iterations, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 32768;
            testSpeedInt(iterations, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 65536;
            testSpeedInt(iterations, params, testAlgorithms, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


}
