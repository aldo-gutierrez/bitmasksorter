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
        long[] limitHigh = new long[]{1000000000, 10000000000000L};

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
    public void speedTestSignedLongST() throws IOException {
        LongSorter[] sorters = new LongSorter[]{new JavaSorterLong(), new RadixBitBaseSorterLong(), new RadixBitSorterLong(), new RadixByteSorterLong()};
        BufferedWriter writer = getWriter("test-results/speed_signedLong_st_" + branch + ".csv");
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
    public void speedTestObjectPositiveIntKey() throws IOException {
        BufferedWriter writer = getWriter("test-results\\speed_objectPositiveLong_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");


        ObjectLongSorter[] sorters = new ObjectLongSorter[]{new JavaSorterObjectLong(), new RadixBitSorterObjectLong()};
        TestSortResults testSortResults;

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

    @Test
    public void speedTestUnsigned() throws IOException {
        BufferedWriter writer = getWriter("test-results/speed_unsignedLong_"+branch+".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");
        LongSorter[] sorters = new LongSorter[]{new RadixBitBaseSorterLong(), new RadixBitSorterLong(), new RadixByteSorterLong()};

        for (Sorter sorter : sorters) {
            sorter.setUnsigned(true);
        }

        TestSortResults testSortResults;

        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 80000;
        params.limitLow = -1000;
        params.limitHigh = 1000L;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        testSpeedUnsignedInt(sorters, HEAT_ITERATIONS, params, testSortResults, null);

        params.random = new Random(seed);
        System.out.println("----------------------");
        {
            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000;
            testSpeedUnsignedInt(sorters, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeedUnsignedInt(sorters, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1000000;
            testSpeedUnsignedInt(sorters, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeedUnsignedInt(sorters, ITERATIONS, params, testSortResults, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 40000000;
            testSpeedUnsignedInt(sorters, ITERATIONS, params, testSortResults, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    private void testSpeedUnsignedInt(LongSorter[] sorters, int iterations, GeneratorParams params, TestSortResults testSortResults, Writer writer) throws IOException {
        LongSorter base = new RadixByteSorterLong();
        base.setUnsigned(true);
        Function<GeneratorParams, long[]> function = LongGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            long[] list = function.apply(params);
            testSort(list, sorters, testSortResults, base);
        }
        printTestSpeed(sorters, params, testSortResults, writer);
    }

    private void testSpeedObject(ObjectLongSorter[] sorters, LongComparator comparator, int iterations, GeneratorParams params, TestSortResults testSortResults, Writer writer) throws IOException {
        Function<GeneratorParams, long[]> function = LongGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            long[] listInt = function.apply(params);
            EntityLong1[] list = new EntityLong1[params.size];
            for (int i = 0; i < params.size; i++) {
                long randomNumber = listInt[i];
                list[i] = new EntityLong1(randomNumber, randomNumber + "");
            }
            testObjectSort(sorters, comparator, list, testSortResults);
        }
        printTestSpeed(sorters, params, testSortResults, writer);
    }

    private void testObjectSort(ObjectLongSorter[] sorters, LongComparator comparator, Object[] list, TestSortResults testSortResults) {
        Object[] listAux2 = Arrays.copyOf(list, list.length);
        long startReference = System.nanoTime();
        Arrays.sort(listAux2, comparator);
        long elapsedReference = System.nanoTime() - startReference;

        for (int i = 0; i < sorters.length; i++) {
            ObjectLongSorter sorter = sorters[i];
            if (sorter instanceof JavaSorterObjectLong) {
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
