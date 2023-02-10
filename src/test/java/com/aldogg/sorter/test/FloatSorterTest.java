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

        TestAlgorithms testAlgorithms;

        //heatup
        testAlgorithms = new TestAlgorithms(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeedFloat(HEAT_ITERATIONS, params, testAlgorithms, null);
        System.out.println("----------------------");

        params.random = new Random(SEED);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            params.limitHigh = limitH;

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000;
            testSpeedFloat(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 100000;
            testSpeedFloat(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 1000000;
            testSpeedFloat(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000000;
            testSpeedFloat(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 40000000;
            testSpeedFloat(ITERATIONS, params, testAlgorithms, writer);

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

        TestAlgorithms testAlgorithms;

        //heatup
        testAlgorithms = new TestAlgorithms(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = -80000;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeedFloat(HEAT_ITERATIONS, params, testAlgorithms, null);
        System.out.println("----------------------");

        params.random = new Random(SEED);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            params.limitHigh = limitH;
            params.limitLow = (int) - params.limitHigh;

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000;
            testSpeedFloat(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 100000;
            testSpeedFloat(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 1000000;
            testSpeedFloat(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000000;
            testSpeedFloat(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 40000000;
            testSpeedFloat(ITERATIONS, params, testAlgorithms, writer);

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

        TestAlgorithms testAlgorithms;

        //heatup
        testAlgorithms = new TestAlgorithms(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = -80000;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_REAL_RANGE;
        testSpeedFloat(HEAT_ITERATIONS, params, testAlgorithms, null);
        System.out.println("----------------------");

        params.random = new Random(SEED);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            params.limitHigh = limitH;
            params.limitLow = (int) - params.limitHigh;

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000;
            testSpeedFloat(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 100000;
            testSpeedFloat(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 1000000;
            testSpeedFloat(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000000;
            testSpeedFloat(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 40000000;
            testSpeedFloat(ITERATIONS, params, testAlgorithms, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

}
