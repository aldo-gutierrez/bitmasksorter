package com.aldogg.sorter.test.performance;

import com.aldogg.sorter.double_.SorterDouble;
import com.aldogg.sorter.double_.object.DoubleMapper;
import com.aldogg.sorter.double_.object.SorterObjectDouble;
import com.aldogg.sorter.double_.object.st.RadixBitSorterObjectDouble;
import com.aldogg.sorter.double_.st.*;
import com.aldogg.sorter.generators.DoubleGenerator;
import com.aldogg.sorter.generators.GeneratorFunctions;
import com.aldogg.sorter.generators.GeneratorParams;
import com.aldogg.sorter.generic.RadixBitSorterGenericInt;
import com.aldogg.sorter.test.unit.IntBasicTest;
import com.aldogg.sorter.test.TestAlgorithms;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoubleSorterPTest extends IntBasicTest {


    @Test
    public void speedTestPositiveDoubleST() throws IOException {
        SorterDouble[] sorters = new SorterDouble[]{new JavaSorterDouble(), new RadixBitBaseSorterDouble(), new RadixBitSorterDouble()};
        BufferedWriter writer = getWriter("test-results/speed_positiveDouble_st_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestAlgorithms testAlgorithms;

        //heat up
        testAlgorithms = new TestAlgorithms(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeedDouble(HEAT_ITERATIONS, params, testAlgorithms, null);
        System.out.println("----------------------");

        params.random = new Random(SEED);
        long[] limitHigh = new long[]{10, 100000, 1000000000, 10000000000000L};

        for (long limitH : limitHigh) {
            params.limitHigh = limitH;

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000;
            testSpeedDouble(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 100000;
            testSpeedDouble(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 1000000;
            testSpeedDouble(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000000;
            testSpeedDouble(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 40000000;
            testSpeedDouble(ITERATIONS, params, testAlgorithms, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    @Test
    public void speedTestSignedDoubleST() throws IOException {
        SorterDouble[] sorters = new SorterDouble[]{new JavaSorterDouble(), new RadixBitSorterDouble(), new RadixBitBaseSorterDouble()};
        BufferedWriter writer = getWriter("test-results/speed_signedDouble_st_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestAlgorithms testAlgorithms;

        //heat up
        testAlgorithms = new TestAlgorithms(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = -80000;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeedDouble(HEAT_ITERATIONS, params, testAlgorithms, null);
        System.out.println("----------------------");

        params.random = new Random(SEED);
        long[] limitHigh = new long[]{10, 100000, 1000000000, 10000000000000L};

        for (long limitH : limitHigh) {
            params.limitLow = (int) -limitH;
            params.limitHigh = limitH;

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000;
            testSpeedDouble(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 100000;
            testSpeedDouble(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 1000000;
            testSpeedDouble(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000000;
            testSpeedDouble(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 40000000;
            testSpeedDouble(ITERATIONS, params, testAlgorithms, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    @Test
    public void speedTestRealDoubleST() throws IOException {
        SorterDouble[] sorters = new SorterDouble[]{new JavaSorterDouble(), new RadixBitSorterDouble(), new RadixBitBaseSorterDouble()};
        BufferedWriter writer = getWriter("test-results/speed_realDouble_st_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestAlgorithms testAlgorithms;

        //heat up
        testAlgorithms = new TestAlgorithms(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = -80000;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_REAL_RANGE;
        testSpeedDouble(HEAT_ITERATIONS, params, testAlgorithms, null);
        System.out.println("----------------------");

        params.random = new Random(SEED);
        long[] limitHigh = new long[]{10, 100000, 1000000000, 10000000000000L};

        for (long limitH : limitHigh) {
            params.limitLow = (int) -limitH;
            params.limitHigh = limitH;

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000;
            testSpeedDouble(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 100000;
            testSpeedDouble(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 1000000;
            testSpeedDouble(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000000;
            testSpeedDouble(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 40000000;
            testSpeedDouble(ITERATIONS, params, testAlgorithms, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    @Test
    public void speedTestObjectPositiveIntKey() throws IOException {
        BufferedWriter writer = getWriter("test-results\\speed_objectPositiveDouble_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");


        SorterObjectDouble[] sorters = new SorterObjectDouble[]{new JavaSorterObjectDouble(), new RadixBitSorterObjectDouble()};
        TestAlgorithms testAlgorithms;

        DoubleMapper<EntityDouble1> mapper = o -> o.getId();

        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;

        //heat up
        testAlgorithms = new TestAlgorithms(sorters);
        testSpeedObject(mapper, HEAT_ITERATIONS, params, testAlgorithms, null);
        System.out.println("----------------------");

        params.random = new Random(SEED);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            testAlgorithms = new TestAlgorithms(sorters);
            params.limitHigh = limitH;
            params.size = 10000;
            testSpeedObject(mapper, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 100000;
            testSpeedObject(mapper, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 1000000;
            testSpeedObject(mapper, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000000;
            testSpeedObject(mapper, ITERATIONS, params, testAlgorithms, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    private void testSpeedObject(DoubleMapper mapper, int iterations, GeneratorParams params, TestAlgorithms testAlgorithms, Writer writer) throws IOException {
        Function<GeneratorParams, double[]> function = DoubleGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            double[] listInt = function.apply(params);
            EntityDouble1[] list = new EntityDouble1[params.size];
            for (int i = 0; i < params.size; i++) {
                double randomNumber = listInt[i];
                list[i] = new EntityDouble1(randomNumber, randomNumber + "");
            }
            testObjectDoubleSort(list, mapper, testAlgorithms);
        }
        testAlgorithms.printTestSpeed(params, writer);
    }

    private void testObjectDoubleSort(Object[] list, DoubleMapper mapper, TestAlgorithms<SorterObjectDouble> testAlgorithms) {
        Object[] baseListSorted = null;
        SorterObjectDouble[] sorters = testAlgorithms.getAlgorithms();
        for (int i = 0; i < sorters.length; i++) {
            SorterObjectDouble sorter = sorters[i];
            Object[] listAux = Arrays.copyOf(list, list.length);
            try {
                long start = System.nanoTime();
                sorter.sort(listAux, mapper);
                long elapsed = System.nanoTime() - start;
                if (i == 0) {
                    baseListSorted = listAux;
                } else {
                    if (validateResult) {
                        for (int j = 0; j < listAux.length; j++) {
                            assertEquals(mapper.value(baseListSorted[j]), mapper.value(listAux[j]));
                        }
//                        assertArrayEquals(baseListSorted, listAux);
                    }
                }
                testAlgorithms.set(sorter.getName(), elapsed);
            } catch (Throwable ex) {
                testAlgorithms.set(sorter.getName(), 0);
                if (list.length <= 10000) {
                    System.err.println("Sorter " + sorter.getName());
                    String orig = Arrays.toString(list);
                    System.err.println("List orig: " + orig);
                    String failed = Arrays.toString(listAux);
                    System.err.println("List fail: " + failed);
                    String ok = Arrays.toString(baseListSorted);
                    System.err.println("List ok: " + ok);
                } else {
                    System.err.println("Sorter " + sorter.getName());
                    System.err.println("List order is not OK ");
                }
                ex.printStackTrace();
            }
        }
    }

    public void testSort(double[] list, TestAlgorithms<SorterDouble> testAlgorithms) {
        double[] baseListSorted = null;
        SorterDouble[] sorters = testAlgorithms.getAlgorithms();
        for (int i = 0; i < sorters.length; i++) {
            SorterDouble sorter = sorters[i];
            double[] listAux = Arrays.copyOf(list, list.length);
            try {
                long start = System.nanoTime();
                sorter.sort(listAux);
                long elapsed = System.nanoTime() - start;
                if (i == 0) {
                    baseListSorted = listAux;
                } else {
                    if (validateResult) {
                        assertArrayEquals(baseListSorted, listAux);
                    }
                }
                testAlgorithms.set(sorter.getName(), elapsed);
            } catch (Throwable ex) {
                testAlgorithms.set(sorter.getName(), 0);
                if (list.length <= 10000) {
                    System.err.println("Sorter " + sorter.getName());
                    String orig = Arrays.toString(list);
                    System.err.println("List orig: " + orig);
                    String failed = Arrays.toString(listAux);
                    System.err.println("List fail: " + failed);
                    String ok = Arrays.toString(baseListSorted);
                    System.err.println("List ok: " + ok);
                } else {
                    System.err.println("Sorter " + sorter.getName());
                    System.err.println("List order is not OK ");
                }
                ex.printStackTrace();
            }
        }
    }


    public void testSpeedDouble(int iterations, GeneratorParams params, TestAlgorithms testAlgorithms, Writer writer) throws IOException {
        Function<GeneratorParams, double[]> function = DoubleGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            double[] list = function.apply(params);
            testSort(list, testAlgorithms);
        }
        testAlgorithms.printTestSpeed(params, writer);
    }

}
