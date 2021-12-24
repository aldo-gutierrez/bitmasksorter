package com.aldogg;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BitUIntSorterTest {

    @Test
    public void basicTests() {
        IntSorter[] sorters = new IntSorter[] {new JavaIntSorter(), new BitSorterUIntOptimized2(), new BitSorterUIntOptimized(), new BitSorterUIntMT(), new RadixBitUIntSorter()};
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
        long startJava = System.currentTimeMillis();
        Arrays.sort(listAux2);
        long elapsedJava = System.currentTimeMillis() - startJava;

        for (int i = 0; i < testSortResults.getSorters().size(); i++) {
            IntSorter sorter = testSortResults.getSorters().get(i);
            if (sorter instanceof JavaIntSorter) {
                testSortResults.set(i, true, elapsedJava);
            } else {
                long start = System.currentTimeMillis();
                int[] listAux = Arrays.copyOf(list, list.length);
                sorter.sort(listAux);
                long elapsed = System.currentTimeMillis() - start;
                try {
                    assertArrayEquals(listAux2, listAux);
                    testSortResults.set(i, true, elapsed);
                } catch (Throwable ex) {
                    testSortResults.set(i, false, 0);
                    ex.printStackTrace();
                    String orig = Arrays.toString(list);
                    //System.err.println("List orig: " + orig);
                }
            }
        }
    }


    @Test
    public void speedTest() {
        IntSorter[] sorters = new IntSorter[] {new JavaIntSorter(), new BitSorterUIntOptimized2(), new BitSorterUIntOptimized(), new BitSorterUIntMT(), new RadixBitUIntSorter(), new RadixBitUIntSorter2(), new JavaParallelSorter()};
        TestSortResults testSortResults = new TestSortResults(sorters);

        // write your code here
        int iterations = 10;

        testSpeed(iterations, 600000, 0, 10000000, testSortResults);
        for (int i = 0; i< testSortResults.getSorters().size(); i++) {
            IntSorter sorter = testSortResults.getSorters().get(i);
            System.out.println("Elapsed "+sorter.name() + " AVG: " + testSortResults.getAVG(i));
        }
        System.out.println();

        // write your code here
        testSpeed(iterations, 10000000, 0, 10000000, testSortResults);
        for (int i = 0; i< testSortResults.getSorters().size(); i++) {
            IntSorter sorter = testSortResults.getSorters().get(i);
            System.out.println("Elapsed "+sorter.name() + " AVG: " + testSortResults.getAVG(i));
        }
        System.out.println();

        testSortResults = new TestSortResults(sorters);
        testSpeed(iterations, 2333333, 0, 9999, testSortResults);
        for (int i = 0; i< testSortResults.getSorters().size(); i++) {
            IntSorter sorter = testSortResults.getSorters().get(i);
            System.out.println("Elapsed "+sorter.name() + " AVG: " + testSortResults.getAVG(i));
        }
        System.out.println();

        testSortResults = new TestSortResults(sorters);
        testSpeed(iterations, 1000000, 0, 400000, testSortResults);
        for (int i = 0; i< testSortResults.getSorters().size(); i++) {
            IntSorter sorter = testSortResults.getSorters().get(i);
            System.out.println("Elapsed "+sorter.name() + " AVG: " + testSortResults.getAVG(i));
        }
        System.out.println();

        testSortResults = new TestSortResults(sorters);
        testSpeed(iterations, 1000000, 0, 2, testSortResults);
        for (int i = 0; i< testSortResults.getSorters().size(); i++) {
            IntSorter sorter = testSortResults.getSorters().get(i);
            System.out.println("Elapsed "+sorter.name() + " AVG: " + testSortResults.getAVG(i));
        }
        System.out.println();

    }

    private void testSpeed(int iterations, int size, int limitLow, int limitHigh, TestSortResults testSortResults) {
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
    }

    @Test
    public void testNegativeNumbers() {
        IntSorter[] sorters = new IntSorter[] {new JavaIntSorter(), new BitSorterIntOptimized()};
        TestSortResults sorter = new TestSortResults(sorters);
        //testSort(new int[] {}, sorter);
        //testSort(new int[] {1}, sorter);
        //testSort(new int[] {2, 1}, sorter);
        //testSort(new int[] {1, 2}, sorter);
        //testSort(new int[] {1, 1}, sorter);
        //testSort(new int[] {53,11,13}, sorter);
        //testSort(new int[] {70,11,13,53}, sorter);
        testSort(new int[] {-70,-11,-13,-53}, sorter);
        testSort(new int[] {-54,-46,-95,-96,-59,-58,-29,18,6,12,56,76,55,16,85,88,87,54,21,90,27,79,29,23,41,74}, sorter);

    }

}

