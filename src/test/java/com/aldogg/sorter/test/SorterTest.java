package com.aldogg.sorter.test;

import com.aldogg.sorter.*;
import com.aldogg.sorter.collection.*;
import com.aldogg.sorter.generators.Generator;
import com.aldogg.sorter.generators.GeneratorParams;
import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.Sorter;
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

public class SorterTest {

    public void testIntSort(int[] list, TestSortResults testSortResults, IntSorter[] sorters) {
        int[] listAux2 = Arrays.copyOf(list, list.length);
        long startJava = System.nanoTime();
        Arrays.sort(listAux2);
        long elapsedJava = System.nanoTime() - startJava;

        for (int i = 0; i < sorters.length; i++) {
            IntSorter sorter = sorters[i];
            if (sorter instanceof JavaSorterInt) {
                testSortResults.set(i, elapsedJava);
            } else {
                long start = System.nanoTime();
                int[] listAux = Arrays.copyOf(list, list.length);
                sorter.sort(listAux);
                long elapsed = System.nanoTime() - start;
                try {
                    assertArrayEquals(listAux2, listAux);
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

    private void testUnsignedIntSort(int[] list, TestSortResults testSortResults, IntSorter[] sorters) {
        int[] listAux2 = Arrays.copyOf(list, list.length);
        long startBase = System.nanoTime();
        IntSorter base = new RadixByteSorterInt();
        base.setUnsigned(true);
        base.sort(listAux2);
        long elapsedBase = System.nanoTime() - startBase;

        for (int i = 0; i < sorters.length; i++) {
            IntSorter sorter = sorters[i];
            if (sorter instanceof JavaSorterInt) {
                testSortResults.set(i, elapsedBase);
            } else {
                long start = System.nanoTime();
                int[] listAux = Arrays.copyOf(list, list.length);
                sorter.sort(listAux);
                long elapsed = System.nanoTime() - start;
                try {
                    assertArrayEquals(listAux2, listAux);
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



    private void testObjectSort(Object[] list, IntComparator comparator, TestSortResults testSortResults, ObjectSorter[] sorters) {
        Object[] listAux2 = Arrays.copyOf(list, list.length);
        long startJava = System.nanoTime();
        Arrays.sort(listAux2, comparator);
        long elapsedJava = System.nanoTime() - startJava;

        for (int i = 0; i < sorters.length; i++) {
            ObjectSorter sorter = sorters[i];
            if (sorter instanceof JavaSorterInt) {
                testSortResults.set(i, elapsedJava);
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
    public void speedTestInt() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("speed.csv"));
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");


        IntSorter[] sorters = new IntSorter[] {new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt(), new JavaParallelSorterInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};
        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random();
        params.size=80000;
        params.limitLow = 0;
        params.limitHigh = 80000;
        testSpeedInt(10, params, testSortResults, sorters, null);

        int iterations = 20;
        int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            testSortResults = new TestSortResults(sorters.length);
            params.limitHigh = limitH;

            params.size = 10000;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1000000;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 40000000;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


    @Test
    public void speedTestObject() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("speed_object.csv"));
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
        params.random = new Random();
        params.size = 80000;
        params.limitLow = 0;
        params.limitHigh = 80000;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        testSpeedObject(10, params, testSortResults, sorters, comparator, null);

        int iterations = 20;
        int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            testSortResults = new TestSortResults(sorters.length);
            params.limitHigh = limitH;
            params.size = 10000;
            testSpeedObject(iterations, params, testSortResults, sorters, comparator, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeedObject(iterations, params, testSortResults, sorters, comparator, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1000000;
            testSpeedObject(iterations, params, testSortResults, sorters, comparator, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeedObject(iterations, params, testSortResults, sorters, comparator, writer);

            //testSortResults = new TestSortResults(sorters.length);
            //testSpeedObject(iterations, 40000000, 0, limitH, testSortResults, sorters, comparator, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


    @Test
    public void speedTestNegative() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("speed_negative.csv"));
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");
        IntSorter[] sorters = new IntSorter[] {new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt(), new JavaParallelSorterInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};

        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random();
        params.size = 80000;
        params.limitLow = -80000;
        params.limitHigh = 80000;

        testSpeedInt(1000, params, testSortResults, sorters, null);

        int iterations = 20;
        int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            testSortResults = new TestSortResults(sorters.length);
            params.limitLow = -limitH;
            params.limitHigh = limitH;

            params.size = 10000;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 40000000;
            testSpeedInt(iterations, params, testSortResults, sorters, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


    @Test
    public void speedTestUnsigned() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("speed_unsigned.csv"));
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");
        IntSorter[] sorters = new IntSorter[] {new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};

        for (IntSorter sorter: sorters) {
            sorter.setUnsigned(true);
        }

        TestSortResults testSortResults;

        GeneratorParams params = new GeneratorParams();
        params.random = new Random();
        params.size = 80000;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        testSpeedUnsignedInt(1000, params, testSortResults, sorters, null);

        int iterations = 20;
        int[] limitHigh = new int[] {214748364};
        for (int limitH : limitHigh) {
            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000;
            testSpeedUnsignedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeedUnsignedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1000000;
            testSpeedUnsignedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeedUnsignedInt(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 40000000;
            testSpeedUnsignedInt(iterations, params, testSortResults, sorters, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    public void testSpeedInt(int iterations, GeneratorParams params, TestSortResults testSortResults, IntSorter[] sorters, Writer writer) throws IOException {
        Generator generator = new Generator();
        Function<GeneratorParams, int[]> generatorFunction = generator.getGeneratorFunction(Generator.ORIGINAL_INT);
        for (int iter = 0; iter < iterations; iter++) {
            int[] list = generatorFunction.apply(params);
            testIntSort(list, testSortResults, sorters);
        }
        printTestSpeed(params, testSortResults, sorters, writer);
    }

    public void testSpeedIncDec(int iterations, GeneratorParams params, TestSortResults testSortResults, IntSorter[] sorters, Writer writer) throws IOException {
        Generator generator = new Generator();
        Function<GeneratorParams, int[]> allEqualFunction = generator.getGeneratorFunction(Generator.ALL_EQUAL_INT);
        Function<GeneratorParams, int[]> ascendingFunction = generator.getGeneratorFunction(Generator.ASCENDING_INT);
        Function<GeneratorParams, int[]> descendingFunction = generator.getGeneratorFunction(Generator.DESCENDING_INT);
        Random random = new Random();
        for (int iter = 0; iter < iterations; iter++) {
            int type = random.nextInt();
            Function<GeneratorParams, int[]> function = type == 0 ? allEqualFunction : (type == 1 ? ascendingFunction : descendingFunction);
            int[] list = function.apply(params);
            testIntSort(list, testSortResults, sorters);
        }
        printTestSpeed(params, testSortResults, sorters, writer);
    }

    @Test
    public void speedTestIncDec() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("speed_sorted.csv"));
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");


        IntSorter[] sorters = new IntSorter[] {new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt(), new JavaParallelSorterInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};
        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        GeneratorParams params = new GeneratorParams();
        params.random = new Random();
        params.size = 10000;

        testSpeedIncDec(20, params, testSortResults, sorters, writer);

        int iterations = 20;
        int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000;
            testSpeedIncDec(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 100000;
            testSpeedIncDec(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 1000000;
            testSpeedIncDec(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 10000000;
            testSpeedIncDec(iterations, params, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            params.size = 40000000;
            testSpeedIncDec(iterations, params, testSortResults, sorters, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


    private void testSpeedUnsignedInt(int iterations, GeneratorParams params, TestSortResults testSortResults, IntSorter[] sorters, Writer writer) throws IOException {
        Generator generator = new Generator();
        Function<GeneratorParams, int[]> generatorFunction = generator.getGeneratorFunction(Generator.ORIGINAL_UNSIGNED_INT);
        for (int iter = 0; iter < iterations; iter++) {
            int[] list = generatorFunction.apply(params);
            testUnsignedIntSort(list, testSortResults, sorters);
        }
        params.limitLow = Integer.MAX_VALUE - 1000;
        params.limitHigh = ((long) Integer.MAX_VALUE) + 2000L;
        printTestSpeed(params, testSortResults, sorters, writer);
    }

    private void testSpeedObject(int iterations, GeneratorParams params, TestSortResults testSortResults, ObjectSorter[] sorters, IntComparator comparator, Writer writer) throws IOException {
        Generator generator = new Generator();
        Function<GeneratorParams, int[]> generatorFunction = generator.getGeneratorFunction(Generator.ORIGINAL_INT);
        for (int iter = 0; iter < iterations; iter++) {
            int[] listInt = generatorFunction.apply(params);
            Entity1[] list = new Entity1[params.size];
            for (int i = 0; i < params.size; i++) {
                int randomInt = listInt[i];
                list[i] = new Entity1(randomInt, randomInt+"");
            }
            testObjectSort(list, comparator, testSortResults, sorters);
        }
        printTestSpeed(params, testSortResults, sorters, writer);
    }

    private void printTestSpeed(GeneratorParams params, TestSortResults testSortResults, Sorter[] sorters, Writer writer) throws IOException {
        int size = params.size;
        int limitLow = params.limitLow;
        long limitHigh = params.limitHigh;
        if (writer != null) {
           for (int i = 0; i < sorters.length; i++) {
               Sorter sorter = sorters[i];
               writer.write(size + ",\"" + limitLow + ":" + limitHigh + "\",\""+sorter.name()+"\"," + testSortResults.getAVG(i)/1000000 + "\n");
               writer.flush();
           }
           System.out.printf("%,13d %,13d ", size,  limitHigh);
           for (int i = 0; i < sorters.length; i++) {
               Sorter sorter = sorters[i];
               System.out.printf("%21s %,13d ", sorter.name(), testSortResults.getAVG(i));
           }
           String sorterWinner = "";
           long sorterWinnerTime = 0;
           String sorter2ndWinner = "";
           long sorter2ndWinnerTime = 0;
           for (int i = 0; i < sorters.length; i++) {
               Sorter sorter = sorters[i];
               if (i==0) {
                   sorterWinner = sorter.name();
                   sorterWinnerTime = testSortResults.getAVG(i);
               } if (i==1) {
                   if (testSortResults.getAVG(i) < sorterWinnerTime) {
                       sorter2ndWinner = sorterWinner;
                       sorter2ndWinnerTime = sorterWinnerTime;
                       sorterWinner = sorter.name();
                       sorterWinnerTime = testSortResults.getAVG(i);
                   } else {
                       sorter2ndWinner = sorter.name();
                       sorter2ndWinnerTime = testSortResults.getAVG(i);
                   }
               } else {
                   if (testSortResults.getAVG(i) < sorterWinnerTime) {
                       sorter2ndWinner = sorterWinner;
                       sorter2ndWinnerTime = sorterWinnerTime;
                       sorterWinner = sorter.name();
                       sorterWinnerTime = testSortResults.getAVG(i);
                   } else if (testSortResults.getAVG(i) < sorter2ndWinnerTime) {
                       sorter2ndWinner = sorter.name();
                       sorter2ndWinnerTime = testSortResults.getAVG(i);
                   }
               }
           }
           System.out.printf("%21s %,13d ", sorterWinner, sorterWinnerTime);
           System.out.printf("%21s %,13d ", sorter2ndWinner, sorter2ndWinnerTime);
           System.out.println();
       }
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

