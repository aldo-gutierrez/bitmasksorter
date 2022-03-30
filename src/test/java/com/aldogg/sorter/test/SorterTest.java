package com.aldogg.sorter.test;

import com.aldogg.sorter.*;
import com.aldogg.sorter.collection.*;
import com.aldogg.sorter.intType.CountSort;
import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;
import com.aldogg.sorter.intType.Sorter;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.aldogg.sorter.BitSorterUtils.getMaskAsList;
import static com.aldogg.sorter.BitSorterUtils.getMaskBit;
import static com.aldogg.sorter.RadixBitSorterInt.radixSort;
import static org.junit.jupiter.api.Assertions.*;

public class SorterTest {

    @Test
    public void basicTests() {
        IntSorter[] sorters = new IntSorter[] {new MixedBitSorterMTInt(), new QuickBitSorterInt(), new QuickBitSorterMTInt(), new RadixBitSorterInt(), new RadixBitSorterMTInt(), new RadixByteSorterInt()};
        TestSortResults sorterTests = new TestSortResults(sorters.length);
        testIntSort(new int[] {}, sorterTests, sorters);
        testIntSort(new int[] {1}, sorterTests, sorters);
        testIntSort(new int[] {2, 1}, sorterTests, sorters);
        testIntSort(new int[] {1, 2}, sorterTests, sorters);
        testIntSort(new int[] {1, 1}, sorterTests, sorters);
        testIntSort(new int[] {53,11,13}, sorterTests, sorters);
        testIntSort(new int[] {70,11,13,53}, sorterTests, sorters);
        testIntSort(new int[] {54,46,95,96,59,58,29,18,6,12,56,76,55,16,85,88,87,54,21,90,27,79,29,23,41,74}, sorterTests, sorters);
        testIntSort(new int[] {
                70,11,13,53,54,46,95,96,59,58,29,18,6,12,56,76,55,16,85,88,
                87,54,21,90,27,79,29,23,41,74,55,8,87,87,17,73,9,47,21,22,
                77,53,67,24,11,24,47,38,26,42,14,91,36,19,12,35,79,91,71,81,
                70,51,94,43,33,7,47,32,6,66,76,81,89,18,10,83,19,67,87,86,45,
                31,70,13,16,40,31,55,81,75,71,16,31,27,17,5,36,29,63,60},sorterTests, sorters);
        //test bit mask 110110000 and 111110000
        testIntSort(new int[] {432,496,432,496,432,496,432,496,432,496,432,496,432,496,432,496,432,496,432,432,496,496,496,496,496,432}, sorterTests, sorters);
    }

    private void testIntSort(int[] list, TestSortResults testSortResults, IntSorter[] sorters) {
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
                    ex.printStackTrace();
                    String orig = Arrays.toString(list);
                    System.err.println("List orig: " + orig);
                    String failed = Arrays.toString(listAux);
                    System.err.println("List fail: " + failed);
                    String ok = Arrays.toString(listAux2);
                    System.err.println("List ok: " + ok);
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
        testSpeedInt(10, 80000, 0, 80000, testSortResults, sorters, null);

        int iterations = 50;
        int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 10000, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 100000, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 1000000, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 10000000, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 40000000, 0, limitH, testSortResults, sorters, writer);

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

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        testSpeedObject(10, 80000, 0, 80000, testSortResults, sorters, comparator, null);

        int iterations = 20;
        int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            testSortResults = new TestSortResults(sorters.length);
            testSpeedObject(iterations, 10000, 0, limitH, testSortResults, sorters, comparator, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedObject(iterations, 100000, 0, limitH, testSortResults, sorters, comparator, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedObject(iterations, 1000000, 0, limitH, testSortResults, sorters, comparator, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedObject(iterations, 10000000, 0, limitH, testSortResults, sorters, comparator, writer);

            //testSortResults = new TestSortResults(sorters.length);
            //testSpeedObject(iterations, 40000000, 0, limitH, testSortResults, sorters, comparator, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


    @Test
    public void smallListAlgorithmSpeedTest() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("small.csv"));
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");


        IntSorter[] sorters = new IntSorter[] {new IntSorter() {
            @Override
            public void sort(int[] list) {
                final int start = 0;
                final int end = list.length;
                int[] maskParts = getMaskBit(list, start, end);
                int mask = maskParts[0] & maskParts[1];
                int[] kList = getMaskAsList(mask);
                int length = end - start;
                int[] aux = new int[length];
                radixSort(list, start, end, aux, kList, kList.length - 1, 0);
            }
            @Override
            public String name() {
                return "StableByte";
            }

            @Override
            public void setUnsigned(boolean unsigned) {
                
            }
        },  new IntSorter() {
            @Override
            public void sort(int[] list) {
                int[] maskParts = getMaskBit(list, 0, list.length);
                int mask = maskParts[0] & maskParts[1];
                int[] listK = getMaskAsList(mask);
                int[] aux = new int[list.length];
                for (int i = listK.length - 1; i >= 0; i--) {
                    int sortMask = getMaskBit(listK[i]);
                    IntSorterUtils.partitionStable(list, 0, list.length, sortMask, aux);
                }
            }
            @Override
            public String name() {
                return "StableBit";
            }

            @Override
            public void setUnsigned(boolean unsigned) {

            }
        }, new IntSorter() {
            @Override
            public void sort(int[] list) {
                int[] maskParts = getMaskBit(list, 0, list.length);
                int mask = maskParts[0] & maskParts[1];
                int[] listK = getMaskAsList(mask);
                CountSort.countSort(list, 0, list.length, listK, 0);
            }

            @Override
            public String name() {
                return "CountSort";
            }

            @Override
            public void setUnsigned(boolean unsigned) {

            }
        }};
        TestSortResults testSortResults;

        int iterations = 20;
            int[] limitHigh = new int[] {2, 4, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192,16384,32768,65536};

        for (int limitH : limitHigh) {
            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 16, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 32, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 64, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 128, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 256, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 512, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 1024, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 2048, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 4096, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 8192, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 16384, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 32768, 0, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 65536, 0, limitH, testSortResults, sorters, writer);

            //System.out.println("----------------------");
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
        testSpeedInt(1000, 80000, 0, 80000, testSortResults, sorters, null);

        int iterations = 50;
        int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 10000, -limitH, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 100000, -limitH, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 1000000, -limitH, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 10000000, -limitH, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 40000000, -limitH, limitH, testSortResults, sorters, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


    @Test
    public void speedTestUnsigned() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("speed_negative.csv"));
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");
        IntSorter[] sorters = new IntSorter[] {new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new JavaParallelSorterInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};

        for (IntSorter sorter: sorters) {
            sorter.setUnsigned(true);
        }

        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters.length);
        testSpeedInt(1000, 80000, 0, 80000, testSortResults, sorters, null);

        int iterations = 200;
        int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 10000, -limitH, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 100000, -limitH, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 1000000, -limitH, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 10000000, -limitH, limitH, testSortResults, sorters, writer);

            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(iterations, 40000000, -limitH, limitH, testSortResults, sorters, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    private void testSpeedInt(int iterations, int size, int limitLow, int limitHigh, TestSortResults testSortResults, IntSorter[] sorters, Writer writer) throws IOException {
        Random random = new Random();
        int f = limitHigh - limitLow;
        for (int iter = 0; iter < iterations; iter++) {
            int[] list = new int[size];
            for (int i = 0; i < size; i++) {
                int randomInt = random.nextInt(f) + limitLow;
                list[i] = randomInt;
            }
            testIntSort(list, testSortResults, sorters);
        }
        printTestSpeed(size, limitLow, limitHigh, testSortResults, sorters, writer);
    }

    private void testSpeedObject(int iterations, int size, int limitLow, int limitHigh, TestSortResults testSortResults, ObjectSorter[] sorters, IntComparator comparator, Writer writer) throws IOException {
        Random random = new Random();
        int f = limitHigh - limitLow;
        for (int iter = 0; iter < iterations; iter++) {
            Entity1[] list = new Entity1[size];
            for (int i = 0; i < size; i++) {
                int randomInt = random.nextInt(f) + limitLow;
                list[i] = new Entity1(randomInt, randomInt+"");
            }
            testObjectSort(list, comparator, testSortResults, sorters);
        }
        printTestSpeed(size, limitLow, limitHigh, testSortResults, sorters, writer);
    }

    private void printTestSpeed(int size, int limitLow, int limitHigh, TestSortResults testSortResults, Sorter[] sorters, Writer writer) throws IOException {
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
           for (int i = 0; i < sorters.length; i++) {
               Sorter sorter = sorters[i];
               if (i==0) {
                   sorterWinner = sorter.name();
                   sorterWinnerTime = testSortResults.getAVG(i);
               } else {
                   if (testSortResults.getAVG(i) < sorterWinnerTime) {
                       sorterWinner = sorter.name();
                       sorterWinnerTime = testSortResults.getAVG(i);
                   }
               }
           }
            System.out.printf("%21s %,13d ", sorterWinner, sorterWinnerTime);
           System.out.println();
       }
    }

    @Test
    public void testNegativeNumbers() {
        IntSorter[] sorters = new IntSorter[] {new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt(), new JavaParallelSorterInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};
        TestSortResults testSorter = new TestSortResults(sorters.length);
        testIntSort(new int[] {}, testSorter, sorters);
        testIntSort(new int[] {1}, testSorter, sorters);
        testIntSort(new int[] {2, 1}, testSorter, sorters);
        testIntSort(new int[] {1, 2}, testSorter, sorters);
        testIntSort(new int[] {1, 1}, testSorter, sorters);
        testIntSort(new int[] {53,11,13}, testSorter, sorters);
        testIntSort(new int[] {70,11,13,53}, testSorter, sorters);
        testIntSort(new int[] {-70,-11,-13,-53}, testSorter, sorters);
        testIntSort(new int[] {-54,-46,-95,-96,-59,-58,-29,18,6,12,56,76,55,16,85,88,87,54,21,90,27,79,29,23,41,74}, testSorter, sorters);

    }

    @Test
    public void testBooleans() {
        IntSorter[] sorters = new IntSorter[]{new JavaSorterInt(), new QuickBitSorterInt()};
        TestSortResults sorter = new TestSortResults(sorters.length);
        testIntSort(new int[]{33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0}, sorter, sorters);
    }

    static volatile int  sum = 0;

    public static  void main (String[] args) {
        long average_t = 0;
        long average_st = 0;
        long average_wt = 0;
        long average_nt = 0;
        int numberOfLoops = 50;
        int numberOfThreads = 8;
        int waitTime = 5;

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
            List<Thread> threads = new ArrayList<>();
            for (int j = 0; j < numberOfThreads; j++) {
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
                threads.add(t);
                t.start();
            }
            average_st += System.nanoTime() - startThread;

            long waitingThread = System.nanoTime();
            for (int j = 0; j < numberOfThreads; j++) {
                Thread t = threads.get(j);
                try {
                    t.join();
                } catch (InterruptedException e) {
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

