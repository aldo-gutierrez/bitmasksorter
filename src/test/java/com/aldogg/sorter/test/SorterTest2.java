package com.aldogg.sorter.test;

import com.aldogg.sorter.generators.GeneratorFunctions;
import com.aldogg.sorter.generators.GeneratorParams;
import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.mt.JavaSorterMTInt;
import com.aldogg.sorter.intType.mt.MixedBitSorterMTInt;
import com.aldogg.sorter.intType.mt.QuickBitSorterMTInt;
import com.aldogg.sorter.intType.mt.RadixBitSorterMTInt;
import com.aldogg.sorter.intType.st.JavaSorterInt;
import com.aldogg.sorter.intType.st.QuickBitSorterInt;
import com.aldogg.sorter.intType.st.RadixBitSorterInt;
import com.aldogg.sorter.intType.st.RadixByteSorterInt;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SorterTest2 extends IntSorterTest {
    @Test
    public void testFunctionsSingleThread() throws IOException {
        IntSorter[] sorters = new IntSorter[] {new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt()};

        GeneratorParams params = new GeneratorParams();
        params.random = new Random();
        params.size= 4096;
        params.limitLow = 0;
        params.limitHigh = params.size;

        BufferedWriter writer = getWriter("speed2.csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");

        GeneratorFunctions[] values = GeneratorFunctions.values();
        //Generator.GeneratorFunctions[] values = {Generator.GeneratorFunctions.ALL_EQUAL_INT};
        for (GeneratorFunctions a : values) {
            params.function =a;
            TestAlgorithms testAlgorithms = new TestAlgorithms(sorters);
            testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);

        }
        //testSpeedInt(10, params, testSortResults, sorters, null);

    }

    @Test
    public void speedTestPositiveIntSTBase2() throws IOException {
        IntSorter[] sorters = new IntSorter[] {new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt()};

        BufferedWriter writer = getWriter("test-results/speed_positiveInt_st_base2.csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");

        TestAlgorithms testAlgorithms;

        //heat up
        testAlgorithms = new TestAlgorithms(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size=80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeed(sorters, HEAT_ITERATIONS, params, testAlgorithms, null);
        System.out.println("----------------------");

        List<Integer> limitHigh = new ArrayList();
        for (int i=1; i<29; i++) {
            limitHigh.add(1 << i);
        }

        for (Integer limitH : limitHigh) {
            params.limitHigh = limitH -1 ;

            for (Integer size : limitHigh) {
                testAlgorithms = new TestAlgorithms(sorters);
                params.size = size;
                testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);
            }

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    @Test
    public void speedTestPositiveIntMTBase2() throws IOException {

        IntSorter[] sorters = new IntSorter[] {new JavaSorterMTInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};
        BufferedWriter writer = getWriter("test-results/speed_positiveInt_mt_base2.csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");

        TestAlgorithms testAlgorithms;

        //heat up
        testAlgorithms = new TestAlgorithms(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size=80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeed(sorters, HEAT_ITERATIONS, params, testAlgorithms, null);
        System.out.println("----------------------");

        List<Integer> limitHigh = new ArrayList();
        for (int i=1; i<29; i++) {
            limitHigh.add(1 << i);
        }

        for (Integer limitH : limitHigh) {
            //params.limitHigh = limitH-1;
            params.limitHigh = limitH -1 ;

            for (Integer size : limitHigh) {
                testAlgorithms = new TestAlgorithms(sorters);
                params.size = size;
                testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);
            }

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

}
