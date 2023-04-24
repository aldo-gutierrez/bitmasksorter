package com.aldogg.sorter.test.performance.generators;

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
import com.aldogg.sorter.int_.st.QuickBitSorterInt;
import com.aldogg.sorter.int_.st.RadixBitSorterInt;
import com.aldogg.sorter.int_.st.RadixByteSorterInt;
import com.aldogg.sorter.test.BaseTest;
import com.aldogg.sorter.test.TestAlgorithms;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.aldogg.sorter.int_.IntSorterUtils.ShortSorter.*;
import static com.aldogg.sorter.int_.st.RadixBitSorterInt.radixSort;

public class GeneratorPTest extends BaseTest {
    @Test
    public void smallListAlgorithmSpeedTest() throws IOException {
        BufferedWriter writer = getWriter("small.csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");
        BufferedWriter writer2 = getWriter("smallJava.txt");

        IntSorter[] sorters = new IntSorter[]{new IntSorter() {
            @Override
            public void sort(int[] array, int start, int endP1) {
                MaskInfoInt maskInfo = MaskInfoInt.calculateMask(array, start, endP1);
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
                MaskInfoInt maskInfo = MaskInfoInt.calculateMask(array, start, endP1);
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
                MaskInfoInt maskInfo = MaskInfoInt.calculateMask(array, start, endP1);
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

    @Test
    public void speedTestPositiveIntSTBase2() throws IOException {
        IntSorter[] sorters = new IntSorter[]{new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt()};

        BufferedWriter writer = getWriter("test-results/speed_positiveInt_st_base2.csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");
        BufferedWriter writer2 = getWriter("test-results/speed_positiveInt_st_base2.txt");

        TestAlgorithms<IntSorter> testAlgorithms;

        //heat up
        testAlgorithms = new TestAlgorithms<>(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeedInt(HEAT_ITERATIONS, params, testAlgorithms);
        System.out.println("----------------------");

        List<Integer> limitHigh = new ArrayList<>();
        for (int i = 1; i < 29; i++) {
            limitHigh.add(1 << i);
        }

        writer2.write("{\n");
        for (Integer limitH : limitHigh) {
            params.limitHigh = limitH - 1;
            writer2.write("{");
            for (Integer size : limitHigh) {
                testAlgorithms = new TestAlgorithms<>(sorters);
                params.size = size;
                testSpeedInt(ITERATIONS, params, testAlgorithms);
                testAlgorithms.printTestSpeed(params, writer);
                writer2.write(testAlgorithms.getWinners().get(0)[0] + ".class, ");
            }
            writer2.write("}, \n");
            System.out.println("----------------------");
        }
        writer2.write("\n}");
        System.out.println();
        writer.close();
        writer2.close();
    }

    @Test
    public void speedTestPositiveIntMTBase2() throws IOException {
        IntSorter[] sorters = new IntSorter[]{new JavaSorterMTInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};
        BufferedWriter writer = getWriter("test-results/speed_positiveInt_mt_base2.csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestAlgorithms<IntSorter> testAlgorithms;

        //heat up
        testAlgorithms = new TestAlgorithms<>(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeedInt(HEAT_ITERATIONS, params, testAlgorithms);
        testAlgorithms.printTestSpeed(params, null);
        System.out.println("----------------------");

        List<Integer> limitHigh = new ArrayList<>();
        for (int i = 1; i < 29; i++) {
            limitHigh.add(1 << i);
        }

        for (Integer limitH : limitHigh) {
            params.limitHigh = limitH - 1;

            for (Integer size : limitHigh) {
                testAlgorithms = new TestAlgorithms<>(sorters);
                params.size = size;
                testSpeedInt(ITERATIONS, params, testAlgorithms);
                testAlgorithms.printTestSpeed(params, null);
            }

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

}
