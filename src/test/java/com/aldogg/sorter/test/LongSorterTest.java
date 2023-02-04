package com.aldogg.sorter.test;

import com.aldogg.sorter.Sorter;
import com.aldogg.sorter.generators.*;
import com.aldogg.sorter.longType.LongSorter;
import com.aldogg.sorter.longType.collection.EntityLong1;
import com.aldogg.sorter.longType.collection.JavaSorterObjectLong;
import com.aldogg.sorter.longType.collection.LongComparator;
import com.aldogg.sorter.longType.collection.ObjectLongSorter;
import com.aldogg.sorter.longType.collection.st.RadixBitSorterObjectLong;
import com.aldogg.sorter.longType.st.JavaSorterLong;
import com.aldogg.sorter.longType.st.RadixBitBaseSorterLong;
import com.aldogg.sorter.longType.st.RadixBitSorterLong;
import com.aldogg.sorter.longType.st.RadixByteSorterLong;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LongSorterTest extends BasicTest {


    @Test
    public void speedTestPositiveLongST() throws IOException {
        LongSorter[] sorters = new LongSorter[]{new JavaSorterLong(), new RadixBitBaseSorterLong(), new RadixBitSorterLong(), new RadixByteSorterLong()};
        BufferedWriter writer = getWriter("test-results/speed_positiveLong_st_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestAlgorithms testAlgorithms;

        //heatup
        testAlgorithms = new TestAlgorithms(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeed(sorters, HEAT_ITERATIONS, params, testAlgorithms, null);
        System.out.println("----------------------");

        params.random = new Random(seed);
        long[] limitHigh = new long[]{1000000000, 10000000000000L};

        for (long limitH : limitHigh) {
            params.limitHigh = limitH;

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000;
            testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 100000;
            testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 1000000;
            testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000000;
            testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 40000000;
            testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    @Test
    public void speedTestSignedLongST() throws IOException {
        LongSorter[] sorters = new LongSorter[]{new JavaSorterLong(), new RadixBitBaseSorterLong(), new RadixBitSorterLong(), new RadixByteSorterLong()};
        BufferedWriter writer = getWriter("test-results/speed_signedLong_st_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestAlgorithms testAlgorithms;

        //heatup
        testAlgorithms = new TestAlgorithms(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 80000;
        params.limitLow = -80000;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeed(sorters, HEAT_ITERATIONS, params, testAlgorithms, null);
        System.out.println("----------------------");

        params.random = new Random(seed);
        long[] limitHigh = new long[]{10, 100000, 1000000000, 10000000000000L};

        for (long limitH : limitHigh) {
            params.limitLow = (int) -limitH;
            params.limitHigh = limitH;

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000;
            testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 100000;
            testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 1000000;
            testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000000;
            testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 40000000;
            testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    @Test
    public void speedTestObjectPositiveIntKey() throws IOException {
        BufferedWriter writer = getWriter("test-results\\speed_objectPositiveLong_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");


        ObjectLongSorter[] sorters = new ObjectLongSorter[]{new JavaSorterObjectLong(), new RadixBitSorterObjectLong()};
        TestAlgorithms testAlgorithms;

        LongComparator<EntityLong1> comparator = new LongComparator<EntityLong1>() {
            @Override
            public long value(EntityLong1 o) {
                return o.getId();
            }

            @Override
            public int compare(EntityLong1 entity1, EntityLong1 t1) {
                return Double.compare(entity1.getId(), t1.getId());
            }
        };

        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;

        //heat up
        testAlgorithms = new TestAlgorithms(sorters);
        testSpeedObject(sorters, comparator, HEAT_ITERATIONS, params, testAlgorithms, null);
        System.out.println("----------------------");

        params.random = new Random(seed);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            testAlgorithms = new TestAlgorithms(sorters);
            params.limitHigh = limitH;
            params.size = 10000;
            testSpeedObject(sorters, comparator, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 100000;
            testSpeedObject(sorters, comparator, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 1000000;
            testSpeedObject(sorters, comparator, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000000;
            testSpeedObject(sorters, comparator, ITERATIONS, params, testAlgorithms, writer);

//            testSortResults = new TestSortResults(Arrays.asList(sorters));
//            params.size = 40000000;
//            testSpeedObject(sorters, comparator,ITERATIONS, params, testSortResults, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    @Test
    public void speedTestUnsigned() throws IOException {
        BufferedWriter writer = getWriter("test-results/speed_unsignedLong_"+branch+".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");
        LongSorter[] sorters = new LongSorter[]{new RadixByteSorterLong(), new RadixBitBaseSorterLong(), new RadixBitSorterLong()};

        for (Sorter sorter : sorters) {
            sorter.setUnsigned(true);
        }

        TestAlgorithms testAlgorithms;

        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 80000;
        params.limitLow = -1000;
        params.limitHigh = 1000L;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;

        //heatup
        testAlgorithms = new TestAlgorithms(sorters);
        testSpeed(sorters, HEAT_ITERATIONS, params, testAlgorithms, null);

        params.random = new Random(seed);
        System.out.println("----------------------");
        {
            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000;
            testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 100000;
            testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 1000000;
            testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000000;
            testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 40000000;
            testSpeed(sorters, ITERATIONS, params, testAlgorithms, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    private void testSpeedObject(ObjectLongSorter[] sorters, LongComparator comparator, int iterations, GeneratorParams params, TestAlgorithms testAlgorithms, Writer writer) throws IOException {
        Function<GeneratorParams, long[]> function = LongGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            long[] listInt = function.apply(params);
            EntityLong1[] list = new EntityLong1[params.size];
            for (int i = 0; i < params.size; i++) {
                long randomNumber = listInt[i];
                list[i] = new EntityLong1(randomNumber, randomNumber + "");
            }
            testObjectSort(sorters, comparator, list, testAlgorithms);
        }
        testAlgorithms.printTestSpeed(params, writer);
    }

    private void testObjectSort(ObjectLongSorter[] sorters, LongComparator comparator, Object[] list, TestAlgorithms testAlgorithms) {
        Object[] listAux2 = Arrays.copyOf(list, list.length);
        long startReference = System.nanoTime();
        Arrays.sort(listAux2, comparator);
        long elapsedReference = System.nanoTime() - startReference;

        for (int i = 0; i < sorters.length; i++) {
            ObjectLongSorter sorter = sorters[i];
            if (sorter instanceof JavaSorterObjectLong) {
                testAlgorithms.set(sorter.getName(), elapsedReference);
            } else {
                long start = System.nanoTime();
                Object[] listAux = Arrays.copyOf(list, list.length);
                sorter.sort(listAux, comparator);
                long elapsed = System.nanoTime() - start;
                try {
                    for (int j = 0; j < listAux.length; j++) {
                        assertEquals(comparator.value(listAux[j]), comparator.value(listAux2[j]));
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
                        String ok = Arrays.toString(listAux2);
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


}
