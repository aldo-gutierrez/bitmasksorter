package com.aldogg.sorter.test;

import com.aldogg.sorter.intType.collection.EntityInt1;
import com.aldogg.sorter.intType.collection.mt.JavaSorterMTObjectInt;
import com.aldogg.sorter.intType.collection.st.JavaSorterObjectInt;
import com.aldogg.sorter.intType.collection.st.RadixBitSorterObjectInt;
import com.aldogg.sorter.generators.GeneratorFunctions;
import com.aldogg.sorter.generators.IntGenerator;
import com.aldogg.sorter.generators.GeneratorParams;
import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.collection.IntComparator;
import com.aldogg.sorter.intType.collection.ObjectIntSorter;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class IntSorterTest extends BaseTest {

    @Test
    public void speedTestPositiveIntST() throws IOException {
        IntSorter[] sorters = new IntSorter[] {new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt()};
        BufferedWriter writer = getWriter("test-results/speed_positiveInt_st_"+branch+".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");

        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size=80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeed(sorters, HEAT_ITERATIONS, params, testSortResults, null);
        System.out.println("----------------------");

        params.random = new Random(seed);
        int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

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
    public void speedTestSignedIntSt() throws IOException {
        IntSorter[] sorters = new IntSorter[]{new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt()};

        BufferedWriter writer = getWriter("test-results/speed_signedInt_st_"+branch+".csv");
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

        params.random = new Random(seed);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        System.out.println("----------------------");

        for (int limitH : limitHigh) {
            params.limitLow = -limitH;
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
    public void speedTestPositiveIntMT() throws IOException {
        IntSorter[] sorters = new IntSorter[] {new JavaSorterMTInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};
        BufferedWriter writer = getWriter("test-results/speed_positiveInt_mt_"+branch+".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");

        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size=80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeed(sorters, HEAT_ITERATIONS, params, testSortResults, null);
        System.out.println("----------------------");

        params.random = new Random(seed);
        int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

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
    public void speedTestObjectPositiveIntKey() throws IOException {
        BufferedWriter writer = getWriter("test-results\\speed_objectPositiveInt_"+branch+".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");


        ObjectIntSorter[] sorters = new ObjectIntSorter[] {new JavaSorterObjectInt(), new JavaSorterMTObjectInt(), new RadixBitSorterObjectInt()};
        TestSortResults testSortResults;

        IntComparator<EntityInt1> comparator = new IntComparator<EntityInt1>() {
            @Override
            public int value(EntityInt1 o) {
                return o.getId();
            }

            @Override
            public int compare(EntityInt1 entity1, EntityInt1 t1) {
                return Integer.compare(entity1.getId(), t1.getId());
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
        int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

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
//            testSpeedObject(ITERATIONS, params, testSortResults, sorters, comparator, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }



    @Test
    public void speedTestSignedIntMt() throws IOException {
        IntSorter[] sorters = new IntSorter[]{new JavaSorterMTInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};

        BufferedWriter writer = getWriter("test-results/speed_signedInt_mt_"+branch+".csv");
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

        params.random = new Random(seed);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        System.out.println("----------------------");

        for (int limitH : limitHigh) {
            params.limitLow = -limitH;
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
        BufferedWriter writer = getWriter("test-results/speed_unsignedInt_"+branch+".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");
        IntSorter[] sorters = new IntSorter[]{new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};

        for (IntSorter sorter : sorters) {
            sorter.setUnsigned(true);
        }

        TestSortResults testSortResults;

        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 80000;
        params.limitLow = Integer.MAX_VALUE - 1000;
        params.limitHigh = ((long) Integer.MAX_VALUE) + 2000L;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        testSpeedUnsignedInt(HEAT_ITERATIONS, params, testSortResults, sorters, null);

        params.random = new Random(seed);
        System.out.println("----------------------");
        {
            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000;
            testSpeedUnsignedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeedUnsignedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1000000;
            testSpeedUnsignedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeedUnsignedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 40000000;
            testSpeedUnsignedInt(ITERATIONS, params, testSortResults, sorters, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    @Test
    public void speedTestIncDec() throws IOException {
        IntSorter[] sorters = new IntSorter[]{new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt(), new JavaSorterMTInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};
        BufferedWriter writer = getWriter("speed_sorted_"+branch+".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");


        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 10000;

        testSpeedIncDec(HEAT_ITERATIONS, params, testSortResults, sorters, writer);

        params.random = new Random(seed);
        System.out.println("----------------------");
        {
            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000;
            testSpeedIncDec(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeedIncDec(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1000000;
            testSpeedIncDec(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeedIncDec(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 40000000;
            testSpeedIncDec(ITERATIONS, params, testSortResults, sorters, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


    public void testSpeedIncDec(int iterations, GeneratorParams params, TestSortResults testSortResults, IntSorter[] sorters, Writer writer) throws IOException {
        Random random = new Random(seed);
        for (int iter = 0; iter < iterations; iter++) {
            int type = random.nextInt(3);
            GeneratorFunctions functionEnum = type == 0 ? GeneratorFunctions.ALL_EQUAL : (type == 1 ? GeneratorFunctions.ASCENDING : GeneratorFunctions.DESCENDING);
            Function<GeneratorParams, int[]> gFunction = IntGenerator.getGFunction(functionEnum);
            params.function = functionEnum;
            int[] list = gFunction.apply(params);
            testSort(list, sorters, testSortResults);
        }
        printTestSpeed(params, testSortResults, sorters, writer);
    }


    private void testSpeedUnsignedInt(int iterations, GeneratorParams params, TestSortResults testSortResults, IntSorter[] sorters, Writer writer) throws IOException {
        IntSorter base = new RadixByteSorterInt();
        base.setUnsigned(true);
        Function<GeneratorParams, int[]> function = IntGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            int[] list = function.apply(params);
            testSort(list, sorters, testSortResults, base);
        }
        printTestSpeed(params, testSortResults, sorters, writer);
    }

    private void testSpeedObject(ObjectIntSorter[] sorters, IntComparator comparator, int iterations, GeneratorParams params, TestSortResults testSortResults, Writer writer) throws IOException {
        Function<GeneratorParams, int[]> function = IntGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            int[] listInt = function.apply(params);
            EntityInt1[] list = new EntityInt1[params.size];
            for (int i = 0; i < params.size; i++) {
                int randomInt = listInt[i];
                list[i] = new EntityInt1(randomInt, randomInt+"");
            }
            testObjectSort(sorters, comparator, list, testSortResults);
        }
        printTestSpeed(params, testSortResults, sorters, writer);
    }

    private void testObjectSort(ObjectIntSorter[] sorters, IntComparator comparator, Object[] list, TestSortResults testSortResults) {
        Object[] listAux2 = Arrays.copyOf(list, list.length);
        long startReference = System.nanoTime();
        Arrays.sort(listAux2, comparator);
        long elapsedReference = System.nanoTime() - startReference;

        for (int i = 0; i < sorters.length; i++) {
            ObjectIntSorter sorter = sorters[i];
            if (sorter instanceof JavaSorterInt) {
                testSortResults.set(i, elapsedReference);
            } else {
                long start = System.nanoTime();
                Object[] listAux = Arrays.copyOf(list, list.length);
                sorter.sort(listAux, comparator);
                long elapsed = System.nanoTime() - start;
                try {
                    for (int j=0; j<listAux.length; j++) {
                        assertEquals(comparator.value(listAux[j]), comparator.value(listAux2[j]));
                    }
                    //for stable sort
                    //assertArrayEquals(listAux2, listAux);
                    testSortResults.set(i, elapsed);
                } catch (Throwable ex) {
                    testSortResults.set(i, 0);
                    if (list.length <= 10000) {
                        System.err.println("Sorter "+ sorter.name());
                        String orig = Arrays.toString(list);
                        System.err.println("List orig: " + orig);
                        String failed = Arrays.toString(listAux);
                        System.err.println("List fail: " + failed);
                        String ok = Arrays.toString(listAux2);
                        System.err.println("List ok: " + ok);
                    } else {
                        System.err.println("Sorter "+ sorter.name());
                        System.err.println("List order is not OK ");
                    }
                    ex.printStackTrace();
                }
            }
        }
    }


}

