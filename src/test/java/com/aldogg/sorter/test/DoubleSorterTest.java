package com.aldogg.sorter.test;

import com.aldogg.sorter.doubleType.DoubleSorter;
import com.aldogg.sorter.doubleType.collection.DoubleComparator;
import com.aldogg.sorter.doubleType.collection.ObjectDoubleSorter;
import com.aldogg.sorter.doubleType.collection.st.RadixBitSorterObjectDouble;
import com.aldogg.sorter.doubleType.st.*;
import com.aldogg.sorter.generators.DoubleGenerator;
import com.aldogg.sorter.generators.GeneratorFunctions;
import com.aldogg.sorter.generators.GeneratorParams;
import com.aldogg.sorter.intType.st.JavaSorterInt;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoubleSorterTest extends BasicTest {


    @Test
    public void speedTestPositiveDoubleST() throws IOException {
        DoubleSorter[] sorters = new DoubleSorter[]{new JavaSorterDouble(), new RadixBitBaseSorterDouble(), new RadixBitSorterDouble()};
        BufferedWriter writer = new BufferedWriter(new FileWriter("test-results/speed_positiveDouble_st_" + branch + ".csv", UTF_8));
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
        long[] limitHigh = new long[]{10, 100000, 1000000000, 10000000000000L};

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
    public void speedTestSignedDoubleST() throws IOException {
        DoubleSorter[] sorters = new DoubleSorter[]{new JavaSorterDouble(), new RadixBitSorterDouble(), new RadixBitBaseSorterDouble()};
        BufferedWriter writer = new BufferedWriter(new FileWriter("test-results/speed_signedDouble_st_" + branch + ".csv", UTF_8));
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
        long[] limitHigh = new long[]{10, 100000, 1000000000, 10000000000000L};

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
    public void speedTestRealDoubleST() throws IOException {
        DoubleSorter[] sorters = new DoubleSorter[]{new JavaSorterDouble(), new RadixBitSorterDouble(), new RadixBitBaseSorterDouble()};
        BufferedWriter writer = new BufferedWriter(new FileWriter("test-results/speed_realDouble_st_" + branch + ".csv", UTF_8));
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
        long[] limitHigh = new long[]{10, 100000, 1000000000, 10000000000000L};

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
    public void speedTestObjectPositiveIntKey() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("test-results\\speed_objectPositiveDouble_" + branch + ".csv", UTF_8));
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");


        ObjectDoubleSorter[] sorters = new ObjectDoubleSorter[]{new JavaSorterObjectDouble(), new RadixBitSorterObjectDouble()};
        TestSortResults testSortResults;

        DoubleComparator<EntityDouble1> comparator = new DoubleComparator<EntityDouble1>() {
            @Override
            public double value(EntityDouble1 o) {
                return o.getId();
            }

            @Override
            public int compare(EntityDouble1 entity1, EntityDouble1 t1) {
                return Double.compare(entity1.getId(), t1.getId());
            }
        };

        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        testSpeedObject(sorters, comparator, HEAT_ITERATIONS, params, testSortResults, null);
        System.out.println("----------------------");

        params.random = new Random(seed);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            testSortResults = new TestSortResults(sorters.length);
            params.limitHigh = limitH;
            params.size = 10000;
            testSpeedObject(sorters, comparator, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeedObject(sorters, comparator, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1000000;
            testSpeedObject(sorters, comparator, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeedObject(sorters, comparator, ITERATIONS, params, testSortResults, writer);

//            testSortResults = new TestSortResults(sorters.length);
//            params.size = 40000000;
//            testSpeedObject(sorters, comparator,ITERATIONS, params, testSortResults, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    private void testSpeedObject(ObjectDoubleSorter[] sorters, DoubleComparator comparator, int iterations, GeneratorParams params, TestSortResults testSortResults, Writer writer) throws IOException {
        Function<GeneratorParams, double[]> function = DoubleGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            double[] listInt = function.apply(params);
            EntityDouble1[] list = new EntityDouble1[params.size];
            for (int i = 0; i < params.size; i++) {
                double randomNumber = listInt[i];
                list[i] = new EntityDouble1(randomNumber, randomNumber + "");
            }
            testObjectSort(sorters, comparator, list, testSortResults);
        }
        printTestSpeed(params, testSortResults, sorters, writer);
    }

    private void testObjectSort(ObjectDoubleSorter[] sorters, DoubleComparator comparator, Object[] list, TestSortResults testSortResults) {
        Object[] listAux2 = Arrays.copyOf(list, list.length);
        long startReference = System.nanoTime();
        Arrays.sort(listAux2, comparator);
        long elapsedReference = System.nanoTime() - startReference;

        for (int i = 0; i < sorters.length; i++) {
            ObjectDoubleSorter sorter = sorters[i];
            if (sorter instanceof JavaSorterInt) {
                testSortResults.set(i, elapsedReference);
            } else {
                long start = System.nanoTime();
                Object[] listAux = Arrays.copyOf(list, list.length);
                sorter.sort(listAux, comparator);
                long elapsed = System.nanoTime() - start;
                try {
                    for (int j = 0; j < listAux.length; j++) {
                        assertEquals(comparator.value(listAux[j]), comparator.value(listAux2[j]));
                    }
                    //for stable sort
                    //assertArrayEquals(listAux2, listAux);
                    testSortResults.set(i, elapsed);
                } catch (Throwable ex) {
                    testSortResults.set(i, 0);
                    if (list.length <= 10000) {
                        System.err.println("Sorter " + sorter.name());
                        String orig = Arrays.toString(list);
                        System.err.println("List orig: " + orig);
                        String failed = Arrays.toString(listAux);
                        System.err.println("List fail: " + failed);
                        String ok = Arrays.toString(listAux2);
                        System.err.println("List ok: " + ok);
                    } else {
                        System.err.println("Sorter " + sorter.name());
                        System.err.println("List order is not OK ");
                    }
                    ex.printStackTrace();
                }
            }
        }
    }

}
