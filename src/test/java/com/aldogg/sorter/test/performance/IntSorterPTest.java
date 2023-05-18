package com.aldogg.sorter.test.performance;

import com.aldogg.sorter.generators.GeneratorFunctions;
import com.aldogg.sorter.generators.GeneratorParams;
import com.aldogg.sorter.generators.IntGenerator;
import com.aldogg.sorter.generic.RadixBitSorterGenericInt;
import com.aldogg.sorter.generic.SorterGenericInt;
import com.aldogg.sorter.int_.SorterInt;
import com.aldogg.sorter.int_.object.EntityInt1;
import com.aldogg.sorter.int_.object.IntMapper;
import com.aldogg.sorter.int_.object.mt.JavaSorterMTObjectInt;
import com.aldogg.sorter.int_.object.mt.RadixBitSorterMTObjectInt;
import com.aldogg.sorter.int_.object.st.JavaSorterObjectInt;
import com.aldogg.sorter.int_.object.st.RadixBitSorterObjectInt;
import com.aldogg.sorter.int_.mt.JavaSorterMTInt;
import com.aldogg.sorter.int_.mt.MixedBitSorterMTInt;
import com.aldogg.sorter.int_.mt.QuickBitSorterMTInt;
import com.aldogg.sorter.int_.mt.RadixBitSorterMTInt;
import com.aldogg.sorter.int_.st.JavaSorterInt;
import com.aldogg.sorter.int_.st.QuickBitSorterInt;
import com.aldogg.sorter.int_.st.RadixBitSorterInt;
import com.aldogg.sorter.int_.st.RadixByteSorterInt;
import com.aldogg.sorter.test.BaseTest;
import com.aldogg.sorter.test.TestAlgorithms;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntSorterPTest extends BaseTest {

    @Test
    public void speedTestPositiveIntST() throws IOException {
        SorterInt[] sorters = new SorterInt[]{new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt()};
        BufferedWriter writer = getWriter("test-results/speed_positiveInt_st_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestAlgorithms<SorterInt> testAlgorithms;

        //heatup
        testAlgorithms = new TestAlgorithms<>(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeedInt(HEAT_ITERATIONS, params, testAlgorithms);
        testAlgorithms.printTestSpeed(params, null);
        System.out.println("----------------------");

        params.random = new Random(SEED);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            params.limitHigh = limitH;
            for (int size : new int[]{10000, 100000, 1000000, 10000000, 40000000}) {
                testAlgorithms = new TestAlgorithms<>(sorters);
                params.size = size;
                testSpeedInt(ITERATIONS, params, testAlgorithms);
                testAlgorithms.printTestSpeed(params, writer);
            }
            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    @Test
    public void speedTestSignedIntSt() throws IOException {
        SorterInt[] sorters = new SorterInt[]{new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt()};

        BufferedWriter writer = getWriter("test-results/speed_signedInt_st_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestAlgorithms<SorterInt> testAlgorithms;

        //heatup
        testAlgorithms = new TestAlgorithms<>(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = -80000;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;

        testSpeedInt(HEAT_ITERATIONS, params, testAlgorithms);
        testAlgorithms.printTestSpeed(params, null);

        params.random = new Random(SEED);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        System.out.println("----------------------");

        for (int limitH : limitHigh) {
            params.limitLow = -limitH;
            params.limitHigh = limitH;

            for (int size : new int[]{10000, 100000, 1000000, 10000000, 40000000}) {
                testAlgorithms = new TestAlgorithms<>(sorters);
                params.size = size;
                testSpeedInt(ITERATIONS, params, testAlgorithms);
                testAlgorithms.printTestSpeed(params, writer);
            }
            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


    @Test
    public void speedTestPositiveIntMT() throws IOException {
        SorterInt[] sorters = new SorterInt[]{new JavaSorterMTInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};
        BufferedWriter writer = getWriter("test-results/speed_positiveInt_mt_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestAlgorithms<SorterInt> testAlgorithms;

        //heatup
        testAlgorithms = new TestAlgorithms<>(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;
        testSpeedInt(HEAT_ITERATIONS, params, testAlgorithms);
        testAlgorithms.printTestSpeed(params, null);

        System.out.println("----------------------");

        params.random = new Random(SEED);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            params.limitHigh = limitH;

            for (int size : new int[]{10000, 100000, 1000000, 10000000, 40000000}) {
                testAlgorithms = new TestAlgorithms<>(sorters);
                params.size = size;
                testSpeedInt(ITERATIONS, params, testAlgorithms);
                testAlgorithms.printTestSpeed(params, writer);
            }
            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


    @Test
    public void speedTestObjectPositiveIntKey() throws IOException {
        BufferedWriter writer = getWriter("test-results\\speed_objectPositiveInt_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");


        SorterGenericInt[] sorters = new SorterGenericInt[]{new JavaSorterObjectInt(), new JavaSorterMTObjectInt(), new RadixBitSorterObjectInt(), new RadixBitSorterMTObjectInt(), new RadixBitSorterGenericInt()};
        TestAlgorithms<SorterGenericInt> testAlgorithms;

        IntMapper<EntityInt1> mapper = o -> o.getId();

        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;

        //heatup
        testAlgorithms = new TestAlgorithms<>(sorters);
        testSpeedObjectInt(mapper, HEAT_ITERATIONS, params, testAlgorithms);
        testAlgorithms.printTestSpeed(params, null);
        System.out.println("----------------------");

        params.random = new Random(SEED);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            params.limitHigh = limitH;
            for (int size : new int[]{10000, 100000, 1000000, 10000000}) {
                testAlgorithms = new TestAlgorithms<>(sorters);
                params.size = size;
                testSpeedObjectInt(mapper, ITERATIONS, params, testAlgorithms);
                testAlgorithms.printTestSpeed(params, writer);
            }
            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


    @Test
    public void speedTestSignedIntMt() throws IOException {
        SorterInt[] sorters = new SorterInt[]{new JavaSorterMTInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};

        BufferedWriter writer = getWriter("test-results/speed_signedInt_mt_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestAlgorithms<SorterInt> testAlgorithms;

        //heatup
        testAlgorithms = new TestAlgorithms<>(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = -80000;
        params.limitHigh = 80000;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;

        testSpeedInt(HEAT_ITERATIONS, params, testAlgorithms);
        testAlgorithms.printTestSpeed(params, null);

        params.random = new Random(SEED);
        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        System.out.println("----------------------");

        for (int limitH : limitHigh) {
            params.limitLow = -limitH;
            params.limitHigh = limitH;

            for (int size : new int[]{10000, 100000, 1000000, 10000000, 40000000}) {
                testAlgorithms = new TestAlgorithms<>(sorters);
                params.size = size;
                testSpeedInt(ITERATIONS, params, testAlgorithms);
                testAlgorithms.printTestSpeed(params, writer);
            }
            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


    @Test
    public void speedTestUnsigned() throws IOException {
        BufferedWriter writer = getWriter("test-results/speed_unsignedInt_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");
        SorterInt[] sorters = new SorterInt[]{new RadixByteSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};

        for (SorterInt sorter : sorters) {
            sorter.setUnsigned(true);
        }

        TestAlgorithms<SorterInt> testAlgorithms;

        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 80000;
        params.limitLow = -1000;
        params.limitHigh = 1000L;
        params.function = GeneratorFunctions.RANDOM_INTEGER_RANGE;

        //heatup
        testAlgorithms = new TestAlgorithms<>(sorters);
        testSpeedInt(HEAT_ITERATIONS, params, testAlgorithms);
        testAlgorithms.printTestSpeed(params, null);

        params.random = new Random(SEED);
        System.out.println("----------------------");
        {
            for (int size : new int[]{10000, 100000, 1000000, 10000000, 40000000}) {
                testAlgorithms = new TestAlgorithms<>(sorters);
                params.size = size;
                testSpeedInt(ITERATIONS, params, testAlgorithms);
                testAlgorithms.printTestSpeed(params, writer);
            }
            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    @Test
    public void speedTestIncDec() throws IOException {
        SorterInt[] sorters = new SorterInt[]{new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt(), new JavaSorterMTInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};
        BufferedWriter writer = getWriter("speed_sorted_" + branch + ".csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");


        TestAlgorithms testAlgorithms;

        //heatup
        testAlgorithms = new TestAlgorithms(sorters);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(SEED);
        params.size = 10000;

        testSpeedIncDec(HEAT_ITERATIONS, params, testAlgorithms);
        testAlgorithms.printTestSpeed(params, null);

        params.random = new Random(SEED);
        System.out.println("----------------------");
        {
            for (int size : new int[]{10000, 100000, 1000000, 10000000, 40000000}) {
                testAlgorithms = new TestAlgorithms<>(sorters);
                params.size = size;
                testSpeedIncDec(ITERATIONS, params, testAlgorithms);
                testAlgorithms.printTestSpeed(params, writer);
            }
            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


    public void testSpeedIncDec(int iterations, GeneratorParams params, TestAlgorithms testAlgorithms) throws IOException {
        Random random = new Random(SEED);
        for (int iter = 0; iter < iterations; iter++) {
            int type = random.nextInt(3);
            GeneratorFunctions functionEnum = type == 0 ? GeneratorFunctions.ALL_EQUAL : (type == 1 ? GeneratorFunctions.ASCENDING : GeneratorFunctions.DESCENDING);
            Function<GeneratorParams, int[]> gFunction = IntGenerator.getGFunction(functionEnum);
            params.function = functionEnum;
            int[] list = gFunction.apply(params);
            testSort(list, testAlgorithms);
        }
    }


    private void testSpeedObjectInt(IntMapper mapper, int iterations, GeneratorParams params, TestAlgorithms testAlgorithms) {
        Function<GeneratorParams, int[]> function = IntGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            int[] listInt = function.apply(params);
            EntityInt1[] list = new EntityInt1[params.size];
            for (int i = 0; i < params.size; i++) {
                int randomInt = listInt[i];
                list[i] = new EntityInt1(randomInt, randomInt + "");
            }
            testObjectIntSort(list, mapper, testAlgorithms);
        }
    }

    public void testObjectIntSort(Object[] list, IntMapper mapper, TestAlgorithms<SorterGenericInt> testAlgorithms) {
        Object[] baseListSorted = null;
        SorterGenericInt[] sorters = testAlgorithms.getAlgorithms();
        for (int i = 0; i < sorters.length; i++) {
            SorterGenericInt sorter = sorters[i];
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

}

