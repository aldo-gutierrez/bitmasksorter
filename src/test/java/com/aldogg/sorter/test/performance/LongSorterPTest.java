package com.aldogg.sorter.test.performance;

import com.aldogg.sorter.Sorter;
import com.aldogg.sorter.generators.GeneratorFunctions;
import com.aldogg.sorter.generators.GeneratorParams;
import com.aldogg.sorter.generators.LongGenerator;
import com.aldogg.sorter.long_.SorterLong;
import com.aldogg.sorter.long_.object.EntityLong1;
import com.aldogg.sorter.long_.object.JavaSorterObjectLong;
import com.aldogg.sorter.long_.object.LongMapper;
import com.aldogg.sorter.long_.object.SorterObjectLong;
import com.aldogg.sorter.long_.object.st.RadixBitSorterObjectLong;
import com.aldogg.sorter.long_.st.JavaSorterLong;
import com.aldogg.sorter.long_.st.RadixBitBaseSorterLong;
import com.aldogg.sorter.long_.st.RadixBitSorterLong;
import com.aldogg.sorter.long_.st.RadixByteSorterLong;
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

public class LongSorterPTest extends IntBasicTest {


    @Test
    public void speedTestPositiveLongST() throws IOException {
        SorterLong[] sorters = new SorterLong[]{new JavaSorterLong(), new RadixBitBaseSorterLong(), new RadixBitSorterLong(), new RadixByteSorterLong()};
        BufferedWriter writer = getWriter("test-results/speed_positiveLong_st_" + branch + ".csv");
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
        testSpeedLong(HEAT_ITERATIONS, params, testAlgorithms, null);
        System.out.println("----------------------");

        params.random = new Random(SEED);
        long[] limitHigh = new long[]{1000000000, 10000000000000L};

        for (long limitH : limitHigh) {
            params.limitHigh = limitH;

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000;
            testSpeedLong(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 100000;
            testSpeedLong(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 1000000;
            testSpeedLong(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000000;
            testSpeedLong(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 40000000;
            testSpeedLong(ITERATIONS, params, testAlgorithms, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    @Test
    public void speedTestSignedLongST() throws IOException {
        SorterLong[] sorters = new SorterLong[]{new JavaSorterLong(), new RadixBitBaseSorterLong(), new RadixBitSorterLong(), new RadixByteSorterLong()};
        BufferedWriter writer = getWriter("test-results/speed_signedLong_st_" + branch + ".csv");
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
        testSpeedLong(HEAT_ITERATIONS, params, testAlgorithms, null);
        System.out.println("----------------------");

        params.random = new Random(SEED);
        long[] limitHigh = new long[]{10, 100000, 1000000000, 10000000000000L};

        for (long limitH : limitHigh) {
            params.limitLow = (int) -limitH;
            params.limitHigh = limitH;

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000;
            testSpeedLong(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 100000;
            testSpeedLong(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 1000000;
            testSpeedLong(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000000;
            testSpeedLong(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 40000000;
            testSpeedLong(ITERATIONS, params, testAlgorithms, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    @Test
    public void speedTestObjectPositiveIntKey() throws IOException {
        BufferedWriter writer = getWriter("test-results\\speed_objectPositiveLong_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");


        SorterObjectLong[] sorters = new SorterObjectLong[]{new JavaSorterObjectLong(), new RadixBitSorterObjectLong()};
        TestAlgorithms testAlgorithms;

        LongMapper<EntityLong1> mapper = o -> o.getId();

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

    @Test
    public void speedTestUnsigned() throws IOException {
        BufferedWriter writer = getWriter("test-results/speed_unsignedLong_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");
        SorterLong[] sorters = new SorterLong[]{new RadixByteSorterLong(), new RadixBitBaseSorterLong(), new RadixBitSorterLong()};

        for (Sorter sorter : sorters) {
            //TODO FIX
            //sorter.setUnsigned(true);
        }

        TestAlgorithms testAlgorithms;

        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = -1000;
        params.limitHigh = 1000L;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;

        //heatup
        testAlgorithms = new TestAlgorithms(sorters);
        testSpeedLong(HEAT_ITERATIONS, params, testAlgorithms, null);

        params.random = new Random(SEED);
        System.out.println("----------------------");
        {
            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000;
            testSpeedLong(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 100000;
            testSpeedLong(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 1000000;
            testSpeedLong(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 10000000;
            testSpeedLong(ITERATIONS, params, testAlgorithms, writer);

            testAlgorithms = new TestAlgorithms(sorters);
            params.size = 40000000;
            testSpeedLong(ITERATIONS, params, testAlgorithms, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    private void testSpeedObject(LongMapper mapper, int iterations, GeneratorParams params, TestAlgorithms testAlgorithms, Writer writer) throws IOException {
        Function<GeneratorParams, long[]> function = LongGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            long[] listInt = function.apply(params);
            EntityLong1[] list = new EntityLong1[params.size];
            for (int i = 0; i < params.size; i++) {
                long randomNumber = listInt[i];
                list[i] = new EntityLong1(randomNumber, randomNumber + "");
            }
            testObjectLongSort(list, mapper, testAlgorithms);
        }
        testAlgorithms.printTestSpeed(params, writer);
    }

    private void testObjectLongSort(Object[] list, LongMapper mapper, TestAlgorithms<SorterObjectLong> testAlgorithms) {
        Object[] baseListSorted = null;
        SorterObjectLong[] sorters = testAlgorithms.getAlgorithms();
        for (int i = 0; i < sorters.length; i++) {
            SorterObjectLong sorter = sorters[i];
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

    public void testSort(long[] list, TestAlgorithms<SorterLong> testAlgorithms) {
        long[] baseListSorted = null;
        SorterLong[] sorters = testAlgorithms.getAlgorithms();
        for (int i = 0; i < sorters.length; i++) {
            SorterLong sorter = sorters[i];
            long[] listAux = Arrays.copyOf(list, list.length);
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

    public void testSpeedLong(int iterations, GeneratorParams params, TestAlgorithms testAlgorithms, Writer writer) throws IOException {
        Function<GeneratorParams, long[]> function = LongGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            long[] list = function.apply(params);
            testSort(list, testAlgorithms);
        }
        testAlgorithms.printTestSpeed(params, writer);
    }


}
