package com.aldogg.sorter.test;

import com.aldogg.sorter.collection.*;
import com.aldogg.sorter.collection.mt.JavaParallelSorterObjectInt;
import com.aldogg.sorter.collection.st.JavaSorterObject;
import com.aldogg.sorter.collection.st.RadixBitSorterObjectInt;
import com.aldogg.sorter.generators.Generator;
import com.aldogg.sorter.generators.GeneratorParams;
import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.mt.JavaParallelSorterInt;
import com.aldogg.sorter.intType.mt.MixedBitSorterMTInt;
import com.aldogg.sorter.intType.mt.QuickBitSorterMTInt;
import com.aldogg.sorter.intType.mt.RadixBitSorterMTInt;
import com.aldogg.sorter.intType.st.JavaSorterInt;
import com.aldogg.sorter.intType.st.QuickBitSorterInt;
import com.aldogg.sorter.intType.st.RadixBitSorterInt;
import com.aldogg.sorter.intType.st.RadixByteSorterInt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

import static com.aldogg.sorter.BitSorterUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class SorterTest extends BaseTest{

    public static final long seed = 1234567890;
    public static final int ITERATIONS = 20;
    public static final int HEAT_ITERATIONS = 10;

    @BeforeEach
    public void beforeEach() {
        System.out.println("Java: "+System.getProperty("java.version"));
    }





    private void testObjectSort(Object[] list, IntComparator comparator, TestSortResults testSortResults, ObjectSorter[] sorters) {
        Object[] listAux2 = Arrays.copyOf(list, list.length);
        long startReference = System.nanoTime();
        Arrays.sort(listAux2, comparator);
        long elapsedReference = System.nanoTime() - startReference;

        for (int i = 0; i < sorters.length; i++) {
            ObjectSorter sorter = sorters[i];
            if (sorter instanceof JavaSorterInt) {
                testSortResults.set(i, elapsedReference);
            } else {
                long start = System.nanoTime();
                Object[] listAux = Arrays.copyOf(list, list.length);
                sorter.sort(listAux, comparator);
                long elapsed = System.nanoTime() - start;
                try {
                    for (int j=0; j<listAux.length; j++) {
                        assertEquals(comparator.intValue(listAux[j]), comparator.intValue(listAux2[j]));
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



    @Test
    public void speedTestPositiveIntST() throws IOException {
//        IntSorter[] sorters = new IntSorter[] {new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt(), new JavaParallelSorterInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};
//        BufferedWriter writer = new BufferedWriter(new FileWriter("test-results/old/speed.csv"));

        IntSorter[] sorters = new IntSorter[] {new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt()};
        BufferedWriter writer = new BufferedWriter(new FileWriter("test-results/speed_positiveInt_st.csv"));
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");

        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size=80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = Generator.GeneratorFunctions.RANDOM_RANGE_INT;
        testSpeedInt(HEAT_ITERATIONS, params, testSortResults, sorters, null);
        System.out.println("----------------------");

        int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            params.limitHigh = limitH;

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1000000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 40000000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    @Test
    public void speedTestPositiveIntMT() throws IOException {
        IntSorter[] sorters = new IntSorter[] {new JavaParallelSorterInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};
        BufferedWriter writer = new BufferedWriter(new FileWriter("test-results/speed_positiveInt_mt.csv"));
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");

        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size=80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = Generator.GeneratorFunctions.RANDOM_RANGE_INT;
        testSpeedInt(HEAT_ITERATIONS, params, testSortResults, sorters, null);
        System.out.println("----------------------");

        int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            params.limitHigh = limitH;

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1000000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 40000000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


    @Test
    public void speedTestObjectPositiveIntKey() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("test-results\\old\\speed_object.csv"));
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");


        ObjectSorter[] sorters = new ObjectSorter[] {new JavaSorterObject(), new JavaParallelSorterObjectInt(), new RadixBitSorterObjectInt()};
        TestSortResults testSortResults;

        IntComparator<Entity1> comparator = new IntComparator<Entity1>() {
            @Override
            public int intValue(Entity1 o) {
                return o.getId();
            }

            @Override
            public int compare(Entity1 entity1, Entity1 t1) {
                return Integer.compare(entity1.getId(), t1.getId());
            }
        };

        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        params.function = Generator.GeneratorFunctions.RANDOM_RANGE_INT;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        testSpeedObject(HEAT_ITERATIONS, params, testSortResults, sorters, comparator, null);
        System.out.println("----------------------");

        int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            testSortResults = new TestSortResults(sorters.length);
            params.limitHigh = limitH;
            params.size = 10000;
            testSpeedObject(ITERATIONS, params, testSortResults, sorters, comparator, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeedObject(ITERATIONS, params, testSortResults, sorters, comparator, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1000000;
            testSpeedObject(ITERATIONS, params, testSortResults, sorters, comparator, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeedObject(ITERATIONS, params, testSortResults, sorters, comparator, writer);

//            testSortResults = new TestSortResults(sorters.length);
//            params.size = 40000000;
//            testSpeedObject(ITERATIONS, params, testSortResults, sorters, comparator, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


    @Test
    public void speedTestSignedIntSt() throws IOException {
        IntSorter[] sorters = new IntSorter[]{new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt()};

        BufferedWriter writer = new BufferedWriter(new FileWriter("test-results/speed_signedInt_st.csv"));
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 80000;
        params.limitLow = -80000;
        params.limitHigh = 80000;
        params.function = Generator.GeneratorFunctions.RANDOM_RANGE_INT;

        testSpeedInt(HEAT_ITERATIONS, params, testSortResults, sorters, null);

        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        System.out.println("----------------------");

        for (int limitH : limitHigh) {
            params.limitLow = -limitH;
            params.limitHigh = limitH;

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1000000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 40000000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    @Test
    public void speedTestSignedIntMt() throws IOException {
        IntSorter[] sorters = new IntSorter[]{new JavaParallelSorterInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};

        BufferedWriter writer = new BufferedWriter(new FileWriter("test-results/speed_signedInt_mt.csv"));
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 80000;
        params.limitLow = -80000;
        params.limitHigh = 80000;
        params.function = Generator.GeneratorFunctions.RANDOM_RANGE_INT;

        testSpeedInt(HEAT_ITERATIONS, params, testSortResults, sorters, null);

        int[] limitHigh = new int[]{10, 1000, 100000, 10000000, 1000000000};

        System.out.println("----------------------");

        for (int limitH : limitHigh) {
            params.limitLow = -limitH;
            params.limitHigh = limitH;

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1000000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 40000000;
            testSpeedInt(ITERATIONS, params, testSortResults, sorters, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


    @Test
    public void speedTestUnsigned() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("speed_unsigned.csv"));
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
        params.function = Generator.GeneratorFunctions.RANDOM_RANGE_INT;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        testSpeedUnsignedInt(HEAT_ITERATIONS, params, testSortResults, sorters, null);

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
        IntSorter[] sorters = new IntSorter[]{new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt(), new JavaParallelSorterInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};
        BufferedWriter writer = new BufferedWriter(new FileWriter("speed_sorted.csv"));
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");


        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random(seed);
        params.size = 10000;

        testSpeedIncDec(HEAT_ITERATIONS, params, testSortResults, sorters, writer);

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
            int type = random.nextInt();
            Generator.GeneratorFunctions functionEnum = type == 0 ? Generator.GeneratorFunctions.ALL_EQUAL_INT : (type == 1 ? Generator.GeneratorFunctions.ASCENDING_INT : Generator.GeneratorFunctions.DESCENDING_INT);
            Function<GeneratorParams, int[]> gFunction = Generator.getGFunction(functionEnum);
            params.function = functionEnum;
            int[] list = gFunction.apply(params);
            testIntSort(list, testSortResults, sorters);
        }
        printTestSpeed(params, testSortResults, sorters, writer);
    }


    private void testSpeedUnsignedInt(int iterations, GeneratorParams params, TestSortResults testSortResults, IntSorter[] sorters, Writer writer) throws IOException {
        IntSorter base = new RadixByteSorterInt();
        base.setUnsigned(true);
        Function<GeneratorParams, int[]> function = Generator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            int[] list = function.apply(params);
            testIntSort(list, testSortResults, sorters, base);
        }
        printTestSpeed(params, testSortResults, sorters, writer);
    }

    private void testSpeedObject(int iterations, GeneratorParams params, TestSortResults testSortResults, ObjectSorter[] sorters, IntComparator comparator, Writer writer) throws IOException {
        Function<GeneratorParams, int[]> function = Generator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            int[] listInt = function.apply(params);
            Entity1[] list = new Entity1[params.size];
            for (int i = 0; i < params.size; i++) {
                int randomInt = listInt[i];
                list[i] = new Entity1(randomInt, randomInt+"");
            }
            testObjectSort(list, comparator, testSortResults, sorters);
        }
        printTestSpeed(params, testSortResults, sorters, writer);
    }




    static volatile int  sum = 0;

    public static  void main (String[] args) {
        listIsOrderedSigned(new int[]{1,1,1}, 0, 3);

        long average_t = 0;
        long average_st = 0;
        long average_wt = 0;
        long average_nt = 0;
        int numberOfLoops = 50;
        int numberOfThreads = 4;
        int waitTime = 5;

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                Thread t = new Thread(() -> {
                    int a = new Random().nextInt();
                    int b = new Random().nextInt();
                    int c = a + b;
                    sum += c;
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (int i = 0; i < numberOfLoops; i++) {

            long startThread = System.nanoTime();
            List<Future> threads = new ArrayList<>();
            for (int j = 0; j < numberOfThreads; j++) {
                Future f = executor.submit(() -> {
                    int a = new Random().nextInt();
                    int b = new Random().nextInt();
                    int c = a + b;
                    sum += c;
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                threads.add(f);
                //t.start();
            }
            average_st += System.nanoTime() - startThread;

            long waitingThread = System.nanoTime();
            for (int j = 0; j < numberOfThreads; j++) {
                Future  t = threads.get(j);
                try {
                    t.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            average_wt += System.nanoTime() - waitingThread;

            long elapsedThread = System.nanoTime() - startThread;
            average_t+=elapsedThread;


            long startNoThread = System.nanoTime();
            for (int j = 0; j < numberOfThreads; j++) {
                int a = new Random().nextInt();
                int b = new Random().nextInt();
                int c = a + b;
                sum += c;
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            long elapsedNoThread = System.nanoTime() - startNoThread;
            average_nt+=elapsedNoThread;

        }
        long startThread = System.nanoTime();
        executor.shutdownNow();
        long t = System.nanoTime() - startThread;
        System.out.println(""+t);

        System.out.println("");
        System.out.printf("%24s %,13d \n", "Average Thread",  average_t/ numberOfLoops);
        System.out.printf("%24s %,13d \n", "Average Starting Thread",  average_st/ numberOfLoops);
        System.out.printf("%24s %,13d \n", "Average Waiting Thread",  average_wt/ numberOfLoops);
        System.out.println("");
        System.out.printf("%24s %,13d \n", "Average No Thread",  average_nt/ numberOfLoops);
        System.out.println("");
        System.out.printf("%24s %,13d \n", "Difference Average",  (average_t - average_nt)/ numberOfLoops);

    }
}

