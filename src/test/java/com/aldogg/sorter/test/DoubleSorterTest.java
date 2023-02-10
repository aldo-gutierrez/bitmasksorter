package com.aldogg.sorter.test;

import com.aldogg.sorter.doubleType.DoubleSorter;
import com.aldogg.sorter.doubleType.collection.DoubleComparator;
import com.aldogg.sorter.doubleType.collection.ObjectDoubleSorter;
import com.aldogg.sorter.doubleType.collection.st.RadixBitSorterObjectDouble;
import com.aldogg.sorter.doubleType.st.*;
import com.aldogg.sorter.generators.DoubleGenerator;
import com.aldogg.sorter.generators.GeneratorFunctions;
import com.aldogg.sorter.generators.GeneratorParams;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoubleSorterTest extends BasicTest {


    @Test
    public void speedTestPositiveDoubleST() throws IOException {
        DoubleSorter[] sorters = new DoubleSorter[]{new JavaSorterDouble(), new RadixBitBaseSorterDouble(), new RadixBitSorterDouble()};
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
        DoubleSorter[] sorters = new DoubleSorter[]{new JavaSorterDouble(), new RadixBitSorterDouble(), new RadixBitBaseSorterDouble()};
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
        DoubleSorter[] sorters = new DoubleSorter[]{new JavaSorterDouble(), new RadixBitSorterDouble(), new RadixBitBaseSorterDouble()};
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


        ObjectDoubleSorter[] sorters = new ObjectDoubleSorter[]{new JavaSorterObjectDouble(), new RadixBitSorterObjectDouble()};
        TestAlgorithms testAlgorithms;

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
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;

        //heat up
        testAlgorithms = new TestAlgorithms(sorters);
        testSpeedObject(comparator, HEAT_ITERATIONS, params, testAlgorithms, null);
        System.out.println("----------------------");

        params.random = new Random(SEED);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            testAlgorithms = new TestAlgorithms(sorters);
            params.limitHigh = limitH;
            params.size = 10000;
            testSpeedObject(comparator, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 100000;
            testSpeedObject(comparator, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 1000000;
            testSpeedObject(comparator, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000000;
            testSpeedObject(comparator, ITERATIONS, params, testAlgorithms, writer);

//            testSortResults = new TestSortResults(Arrays.asList(sorters));
//            params.size = 40000000;
//            testSpeedObject(sorters, comparator,ITERATIONS, params, testSortResults, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    private void testSpeedObject(DoubleComparator comparator, int iterations, GeneratorParams params, TestAlgorithms testAlgorithms, Writer writer) throws IOException {
        Function<GeneratorParams, double[]> function = DoubleGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            double[] listInt = function.apply(params);
            EntityDouble1[] list = new EntityDouble1[params.size];
            for (int i = 0; i < params.size; i++) {
                double randomNumber = listInt[i];
                list[i] = new EntityDouble1(randomNumber, randomNumber + "");
            }
            testObjectDoubleSort(list, comparator, testAlgorithms);
        }
        testAlgorithms.printTestSpeed(params, writer);
    }

    private void testObjectDoubleSort(Object[] list, DoubleComparator comparator, TestAlgorithms<ObjectDoubleSorter> testAlgorithms) {
        Object[] baseListSorted = null;
        ObjectDoubleSorter[] sorters = testAlgorithms.getAlgorithms();
        for (int i = 0; i < sorters.length; i++) {
            ObjectDoubleSorter sorter = sorters[i];
            Object[] listAux = Arrays.copyOf(list, list.length);
            try {
                long start = System.nanoTime();
                sorter.sort(listAux, comparator);
                long elapsed = System.nanoTime() - start;
                if (i == 0) {
                    baseListSorted = listAux;
                } else {
                    if (validateResult) {
                        for (int j=0; j<listAux.length; j++) {
                            assertEquals(comparator.value(baseListSorted[j]), comparator.value(listAux[j]));
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

}
