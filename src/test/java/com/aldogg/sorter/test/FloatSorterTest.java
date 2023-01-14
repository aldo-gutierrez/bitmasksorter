package com.aldogg.sorter.test;

import com.aldogg.sorter.floatType.FloatSorter;
import com.aldogg.sorter.floatType.st.JavaSorterFloat;
import com.aldogg.sorter.floatType.st.RadixBitBaseSorterFloat;
import com.aldogg.sorter.floatType.st.RadixBitSorterFloat;
import com.aldogg.sorter.generators.GeneratorFunctions;
import com.aldogg.sorter.generators.GeneratorParams;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;

public class FloatSorterTest extends BasicTest {


    @Test
    public void speedTestPositiveFloatST() throws IOException {
        FloatSorter[] sorters = new FloatSorter[]{new JavaSorterFloat(), new RadixBitBaseSorterFloat(), new RadixBitSorterFloat()};
        BufferedWriter writer = getWriter("test-results/speed_positiveFloat_st_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeed(sorters, HEAT_ITERATIONS, params, testSortResults, null);
        System.out.println("----------------------");

        params.random = new Random(seed);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
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
    public void speedTestSignedFloatST() throws IOException {
        FloatSorter[] sorters = new FloatSorter[]{new JavaSorterFloat(), new RadixBitBaseSorterFloat(), new RadixBitSorterFloat()};
        BufferedWriter writer = getWriter("test-results/speed_signedFloat_st_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 80000;
        params.limitLow = -80000;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeed(sorters, HEAT_ITERATIONS, params, testSortResults, null);
        System.out.println("----------------------");

        params.random = new Random(seed);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            params.limitHigh = limitH;
            params.limitLow = (int) - params.limitHigh;

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
    public void speedTestRealFloatST() throws IOException {
        FloatSorter[] sorters = new FloatSorter[]{new JavaSorterFloat(), new RadixBitBaseSorterFloat(), new RadixBitSorterFloat()};
        BufferedWriter writer = getWriter("test-results/speed_realFloat_st_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 80000;
        params.limitLow = -80000;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_REAL_RANGE;
        testSpeed(sorters, HEAT_ITERATIONS, params, testSortResults, null);
        System.out.println("----------------------");

        params.random = new Random(seed);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            params.limitHigh = limitH;
            params.limitLow = (int) - params.limitHigh;

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

}
