package com.aldogg.sorter.test;

import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.generators.GeneratorFunctions;
import com.aldogg.sorter.generators.GeneratorParams;
import com.aldogg.sorter.int_.IntCountSort;
import com.aldogg.sorter.int_.IntSorter;
import com.aldogg.sorter.int_.IntSorterUtils;
import com.aldogg.sorter.int_.mt.JavaSorterMTInt;
import com.aldogg.sorter.int_.mt.MixedBitSorterMTInt;
import com.aldogg.sorter.int_.mt.QuickBitSorterMTInt;
import com.aldogg.sorter.int_.mt.RadixBitSorterMTInt;
import com.aldogg.sorter.int_.st.JavaSorterInt;
import com.aldogg.sorter.int_.st.QuickBitSorterInt;
import com.aldogg.sorter.int_.st.RadixBitSorterInt;
import com.aldogg.sorter.int_.st.RadixByteSorterInt;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;

import static com.aldogg.sorter.int_.IntSorterUtils.ShortSorter.*;
import static com.aldogg.sorter.int_.st.RadixBitSorterInt.radixSort;

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
        BufferedWriter writer2 = getWriter("smallJava.txt");

        IntSorter[] sorters = new IntSorter[]{new IntSorter() {
            @Override
            public void sort(int[] array, int start, int endP1) {
                MaskInfoInt maskInfo = MaskInfoInt.getMaskBit(array, start, endP1);
                int mask = maskInfo.getMask();
                int[] kList = MaskInfoInt.getMaskAsArray(mask);
                int length = endP1 - start;
                int[] aux = new int[length];
                radixSort(array, start, endP1, kList, 0, kList.length - 1, aux, 0);
            }

            @Override
            public String getName() {
                return StableByte.name();
            }

        }, new IntSorter() {
            @Override
            public void sort(int[] array, int start, int endP1) {
                MaskInfoInt maskInfo = MaskInfoInt.getMaskBit(array, start, endP1);
                int mask = maskInfo.getMask();
                int[] kList = MaskInfoInt.getMaskAsArray(mask);
                int[] aux = new int[endP1 - start];
                for (int i = kList.length - 1; i >= 0; i--) {
                    int sortMask = 1 << kList[i];
                    IntSorterUtils.partitionStable(array, start, endP1, sortMask, aux);
                }
            }

            @Override
            public String getName() {
                return StableBit.name();
            }

        }, new IntSorter() {
            @Override
            public void sort(int[] array, int start, int endP1) {
                MaskInfoInt maskInfo = MaskInfoInt.getMaskBit(array, start, endP1);
                int mask = maskInfo.getMask();
                int[] kList = MaskInfoInt.getMaskAsArray(mask);
                IntCountSort.countSort(array, start, endP1, kList, 0);
            }

            @Override
            public String getName() {
                return CountSort.name();
            }

        }};
//        }, new JavaSorterInt()};
        TestAlgorithms<IntSorter> testAlgorithms;

        int[] twoPowersHeat = {16, 256, 4096, 65536};
        int[] twoPowers = {2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536};

        GeneratorParams params = new GeneratorParams();
        params.random = new Random(123456789);
        params.size = 16;
        params.limitLow = 0;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;

        int iterations = HEAT_ITERATIONS*200;
        for (int limitH : twoPowersHeat) {
            params.limitHigh = limitH - 1;
            for (int size : twoPowersHeat) {
                testAlgorithms = new TestAlgorithms<>(sorters);
                params.size = size;
                testSpeedInt(iterations, params, testAlgorithms);
                testAlgorithms.printTestSpeed(params, null);
            }
        }

        System.out.println("----------------------");

        iterations = ITERATIONS*200;
        writer2.write("{\n");
        for (int limitH : twoPowers) {
            params.limitHigh = limitH - 1;
            writer2.write("{");
            for (int size : twoPowers) {
                testAlgorithms = new TestAlgorithms<>(sorters);
                params.size = size;
                testSpeedInt(iterations, params, testAlgorithms);
                testAlgorithms.printTestSpeed(params, writer);
                writer2.write(testAlgorithms.getWinners().get(0)[0] + ", ");
            }
            writer2.write("}, \n");
        }
        writer2.write("\n}");
        System.out.println();
        System.out.println("----------------------");
        writer.close();
        writer2.close();
    }


}
