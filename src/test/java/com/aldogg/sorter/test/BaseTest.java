package com.aldogg.sorter.test;

import com.aldogg.sorter.generators.Generator;
import com.aldogg.sorter.generators.GeneratorParams;
import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.Sorter;
import com.aldogg.sorter.intType.st.JavaSorterInt;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class BaseTest {
    boolean validateResult = false;

    public void testIntSort(int[] list, TestSortResults testSortResults, IntSorter[] sorters) {
        IntSorter base = new JavaSorterInt();
        testIntSort(list, testSortResults, sorters, base);
    }

    public void testIntSort(int[] list, TestSortResults testSortResults, IntSorter[] sorters, IntSorter baseSorter) {
        int[] baseListSorted = null;
        if (validateResult) {
            baseListSorted = Arrays.copyOf(list, list.length);
        }
        long startBase = System.nanoTime();
        if (validateResult) {
            baseSorter.sort(baseListSorted);
        }
        long elapsedBase = System.nanoTime() - startBase;
        performSort(list, testSortResults, sorters, baseListSorted, elapsedBase, baseSorter.getClass());
    }

    private void performSort(int[] list, TestSortResults testSortResults, IntSorter[] sorters, int[] baseListSorted, long elapsedBase, Class baseClass) {
        for (int i = 0; i < sorters.length; i++) {
            IntSorter sorter = sorters[i];
            if (validateResult && baseClass.isInstance(sorter)) {
                testSortResults.set(i, elapsedBase);
            } else {
                long start = System.nanoTime();
                int[] listAux = Arrays.copyOf(list, list.length);
                sorter.sort(listAux);
                long elapsed = System.nanoTime() - start;
                try {
                    if (validateResult) {
                        assertArrayEquals(baseListSorted, listAux);
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
                        String ok = Arrays.toString(baseListSorted);
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

    public void testSpeedInt(int iterations, GeneratorParams params, TestSortResults testSortResults, IntSorter[] sorters, Writer writer) throws IOException {
        Function<GeneratorParams, int[]> function = Generator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            int[] list = function.apply(params);
            testIntSort(list, testSortResults, sorters);
        }
        printTestSpeed(params, testSortResults, sorters, writer);
    }

    protected void printTestSpeed(GeneratorParams params, TestSortResults testSortResults, Sorter[] sorters, Writer writer) throws IOException {
        int size = params.size;
        int limitLow = params.limitLow;
        long limitHigh = params.limitHigh;
        for (int i = 0; i < sorters.length; i++) {
            Sorter sorter = sorters[i];
            if (writer != null)
                writer.write(size + ",\"" + limitLow + ":" + limitHigh + "\",\"" + sorter.name() + "\"," + testSortResults.getAVG(i) / 1000000 + "\n");
            if (writer != null) writer.flush();
        }
        System.out.printf("%,13d %18s %25s", size, limitLow + ":" + limitHigh, params.function.toString());
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
            if (i == 0) {
                sorterWinner = sorter.name();
                sorterWinnerTime = testSortResults.getAVG(i);
            }
            if (i == 1) {
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
