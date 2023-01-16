package com.aldogg.sorter.test;

import com.aldogg.sorter.Sorter;
import com.aldogg.sorter.byteType.ByteSorter;
import com.aldogg.sorter.byteType.st.JavaSorterByte;
import com.aldogg.sorter.byteType.st.RadixByteSorterByte;
import com.aldogg.sorter.generators.GeneratorFunctions;
import com.aldogg.sorter.generators.GeneratorParams;
import com.aldogg.sorter.generators.IntGenerator;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Random;
import java.util.function.Function;

public class ByteSorterTest extends BasicTest {

    @Test
    public void speedTestPositiveByteST() throws IOException {
        ByteSorter[] sorters = new ByteSorter[]{new JavaSorterByte(), new RadixByteSorterByte()};
        BufferedWriter writer = getWriter("test-results/speed_positiveByte_st_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 80000;
        params.limitLow = 0;
        params.limitHigh = 127;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeed(sorters, HEAT_ITERATIONS, params, testSortResults, null);
        System.out.println("----------------------");

        params.random = new Random(seed);
        long[] limitHigh = new long[]{15, 31, 63, 127};

        for (long limitH : limitHigh) {
            params.limitHigh = limitH;

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000;
            testSpeed(sorters, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeed(sorters, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1000000;
            testSpeed(sorters, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeed(sorters, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 40000000;
            testSpeed(sorters, ITERATIONS, params, testSortResults, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    @Test
    public void speedTestSignedByteST() throws IOException {
        ByteSorter[] sorters = new ByteSorter[]{new JavaSorterByte(), new RadixByteSorterByte()};
        BufferedWriter writer = getWriter("test-results/speed_signedByte_st_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 80000;
        params.limitLow = -128;
        params.limitHigh = 127;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeed(sorters, HEAT_ITERATIONS, params, testSortResults, null);
        System.out.println("----------------------");

        params.random = new Random(seed);
        long[] limitHigh = new long[]{15, 31, 63, 127};

        for (long limitH : limitHigh) {
            params.limitLow = (int) -limitH;
            params.limitHigh = limitH;

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000;
            testSpeed(sorters, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeed(sorters, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1000000;
            testSpeed(sorters, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeed(sorters, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 40000000;
            testSpeed(sorters, ITERATIONS, params, testSortResults, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    @Test
    public void speedTestUnsigned() throws IOException {
        BufferedWriter writer = getWriter("test-results/speed_unsignedByte_"+branch+".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");
        ByteSorter[] sorters = new ByteSorter[]{new RadixByteSorterByte()};

        for (Sorter sorter : sorters) {
            sorter.setUnsigned(true);
        }

        TestSortResults testSortResults;

        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 80000;
        params.limitLow = -100;
        params.limitHigh = 100;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        testSpeedUnsigned(sorters, HEAT_ITERATIONS, params, testSortResults, null);

        params.random = new Random(seed);
        System.out.println("----------------------");
        {
            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000;
            testSpeedUnsigned(sorters, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeedUnsigned(sorters, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1000000;
            testSpeedUnsigned(sorters, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeedUnsigned(sorters, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 40000000;
            testSpeedUnsigned(sorters, ITERATIONS, params, testSortResults, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    private void testSpeedUnsigned(ByteSorter[] sorters, int iterations, GeneratorParams params, TestSortResults testSortResults, Writer writer) throws IOException {
        ByteSorter base = new RadixByteSorterByte();
        base.setUnsigned(true);
        Function<GeneratorParams, int[]> function = IntGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            int[] listInts = function.apply(params);
            byte[] list = new byte[listInts.length];
            for (int i=0; i<listInts.length; i++) {
                list[i] = (byte) listInts[i];
            }

            testSort(list, sorters, testSortResults, base);
        }
        printTestSpeed(sorters, params, testSortResults, writer);
    }
}
