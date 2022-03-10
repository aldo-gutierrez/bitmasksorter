package com.aldogg;

import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static com.aldogg.BitSorterUtils.getMaskAsList;
import static com.aldogg.BitSorterUtils.getMaskBit;
import static com.aldogg.RadixBitSorterUInt.radixSort;
import static org.junit.jupiter.api.Assertions.*;

public class SorterTest {

    @Test
    public void basicTests() {
        IntSorter[] sorters = new IntSorter[] {new MixedBitSorterMTUInt(), new QuickBitSorterUInt(), new QuickBitSorterMTUInt(), new RadixBitSorterUInt()};
        TestSortResults sorter = new TestSortResults(sorters);
        testSort(new int[] {}, sorter);
        testSort(new int[] {1}, sorter);
        testSort(new int[] {2, 1}, sorter);
        testSort(new int[] {1, 2}, sorter);
        testSort(new int[] {1, 1}, sorter);
        testSort(new int[] {53,11,13}, sorter);
        testSort(new int[] {70,11,13,53}, sorter);
        testSort(new int[] {54,46,95,96,59,58,29,18,6,12,56,76,55,16,85,88,87,54,21,90,27,79,29,23,41,74}, sorter);
        testSort(new int[] {
                70,11,13,53,54,46,95,96,59,58,29,18,6,12,56,76,55,16,85,88,
                87,54,21,90,27,79,29,23,41,74,55,8,87,87,17,73,9,47,21,22,
                77,53,67,24,11,24,47,38,26,42,14,91,36,19,12,35,79,91,71,81,
                70,51,94,43,33,7,47,32,6,66,76,81,89,18,10,83,19,67,87,86,45,
                31,70,13,16,40,31,55,81,75,71,16,31,27,17,5,36,29,63,60},sorter);
        //test bit mask 110110000 and 111110000
        testSort(new int[] {432,496,432,496,432,496,432,496,432,496,432,496,432,496,432,496,432,496,432,432,496,496,496,496,496,432}, sorter);
    }

    private void testSort(int[] list, TestSortResults testSortResults) {
        int[] listAux2 = Arrays.copyOf(list, list.length);
        long startJava = System.nanoTime();
        Arrays.sort(listAux2);
        long elapsedJava = System.nanoTime() - startJava;

        for (int i = 0; i < testSortResults.getSorters().size(); i++) {
            IntSorter sorter = testSortResults.getSorters().get(i);
            if (sorter instanceof JavaSorter) {
                testSortResults.set(i, true, elapsedJava);
            } else {
                long start = System.nanoTime();
                int[] listAux = Arrays.copyOf(list, list.length);
                sorter.sort(listAux);
                long elapsed = System.nanoTime() - start;
                try {
                    assertArrayEquals(listAux2, listAux);
                    testSortResults.set(i, true, elapsed);
                } catch (Throwable ex) {
                    testSortResults.set(i, false, 0);
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
    public void speedTest() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("speed.csv"));
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");


        IntSorter[] sorters = new IntSorter[] {new JavaSorter(), new QuickBitSorterUInt(), new RadixBitSorterUInt(), new JavaParallelSorter(), new QuickBitSorterMTUInt(), new MixedBitSorterMTUInt(), new RadixBitSorterMTUInt()};
        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters);
        testSpeed(1000, 80000, 0, 80000, testSortResults, null);

        int iterations = 20;
        int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 10000, 0, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 100000, 0, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 1000000, 0, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 10000000, 0, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 40000000, 0, limitH, testSortResults, writer);

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
        },  new IntSorter() {
            @Override
            public void sort(int[] list) {
                int[] maskParts = getMaskBit(list, 0, list.length);
                int mask = maskParts[0] & maskParts[1];
                int[] listK = getMaskAsList(mask);
                int[] aux = new int[list.length];
                for (int i = listK.length - 1; i >= 0; i--) {
                    int sortMask = BitSorterUtils.getMaskBit(listK[i]);
                    IntSorterUtils.partitionStable(list, 0, list.length, sortMask, aux);
                }
            }
            @Override
            public String name() {
                return "StableBit";
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
        }};
        TestSortResults testSortResults;

        int iterations = 20;
            int[] limitHigh = new int[] {2, 4, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192,16384,32768,65536};

        for (int limitH : limitHigh) {
            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 16, 0, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 32, 0, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 64, 0, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 128, 0, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 256, 0, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 512, 0, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 1024, 0, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 2048, 0, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 4096, 0, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 8192, 0, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 16384, 0, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 32768, 0, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 65536, 0, limitH, testSortResults, writer);

            //System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }


    @Test
    public void speedTestNegative() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("speed_negative.csv"));
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");


        IntSorter[] sorters = new IntSorter[] {new JavaSorter(),  new RadixBitSorterInt(), new JavaParallelSorter(),  new QuickBitSorterInt()};
        TestSortResults testSortResults;

        //heatup
        testSortResults = new TestSortResults(sorters);
        testSpeed(1000, 80000, 0, 80000, testSortResults, null);

        int iterations = 200;
        int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

        for (int limitH : limitHigh) {
            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 10000, -limitH, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 100000, -limitH, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 1000000, -limitH, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 10000000, -limitH, limitH, testSortResults, writer);

            testSortResults = new TestSortResults(sorters);
            testSpeed(iterations, 40000000, -limitH, limitH, testSortResults, writer);

            System.out.println("----------------------");
        }
        System.out.println();
        writer.close();
    }

    

    private void testSpeed(int iterations, int size, int limitLow, int limitHigh, TestSortResults testSortResults, Writer writer) throws IOException {
        Random random = new Random();
        int f = limitHigh - limitLow;
        for (int iter = 0; iter < iterations; iter++) {
            int[] list = new int[size];
            for (int i = 0; i < size; i++) {
                int randomInt = random.nextInt(f) + limitLow;
                list[i] = randomInt;
            }
            testSort(list, testSortResults);
        }
         if (writer != null) {
            //System.out.println("N: %12i, K: %12i ",  size, limitLow, limitHigh+ ");
            for (int i = 0; i < testSortResults.getSorters().size(); i++) {
                IntSorter sorter = testSortResults.getSorters().get(i);
                writer.write(size + ",\"" + limitLow + ":" + limitHigh + "\",\""+sorter.name()+"\"," + testSortResults.getAVG(i)/1000000 + "\n");
                writer.flush();
            }
            System.out.printf("%12d, %12d, ", size,  limitHigh);
            for (int i = 0; i < testSortResults.getSorters().size(); i++) {
                IntSorter sorter = testSortResults.getSorters().get(i);
                System.out.printf("%20s, %12d, ", sorter.name(), testSortResults.getAVG(i));
            }
            String sorterWinner = "";
            long sorterWinnerTime = 0;
            for (int i = 0; i < testSortResults.getSorters().size(); i++) {
                IntSorter sorter = testSortResults.getSorters().get(i);
                if (i==0) {
                    sorterWinner = sorter.name();
                    sorterWinnerTime = testSortResults.getAVG(i);
                } else {
                    if (testSortResults.getAVG(i) < sorterWinnerTime) {
                        sorterWinner = sorter.name();
                    }
                }
            }
            System.out.print(sorterWinner  + ",\t");
            System.out.println();
        }
    }

    @Test
    public void testNegativeNumbers() {
        IntSorter[] sorters = new IntSorter[] {new JavaSorter(), new QuickBitSorterInt(), new RadixBitSorterInt()};
        TestSortResults sorter = new TestSortResults(sorters);
        testSort(new int[] {}, sorter);
        testSort(new int[] {1}, sorter);
        testSort(new int[] {2, 1}, sorter);
        testSort(new int[] {1, 2}, sorter);
        testSort(new int[] {1, 1}, sorter);
        testSort(new int[] {53,11,13}, sorter);
        testSort(new int[] {70,11,13,53}, sorter);
        testSort(new int[] {-70,-11,-13,-53}, sorter);
        testSort(new int[] {-54,-46,-95,-96,-59,-58,-29,18,6,12,56,76,55,16,85,88,87,54,21,90,27,79,29,23,41,74}, sorter);

    }

    @Test
    public void testBooleans() {
        IntSorter[] sorters = new IntSorter[]{new JavaSorter(), new QuickBitSorterUInt()};
        TestSortResults sorter = new TestSortResults(sorters);
        testSort(new int[]{33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0}, sorter);
    }

    public static  void main (String[] args) {
        //Collections.sort();
    }
}

