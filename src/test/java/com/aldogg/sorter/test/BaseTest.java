package com.aldogg.sorter.test;

import com.aldogg.sorter.doubleType.DoubleSorter;
import com.aldogg.sorter.floatType.FloatSorter;
import com.aldogg.sorter.generators.*;
import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.Sorter;
import com.aldogg.sorter.longType.LongSorter;
import org.junit.jupiter.api.BeforeEach;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class BaseTest {
    final boolean validateResult = true;

    public static final long seed = 1234567890;
    public static final int ITERATIONS = 20;
    public static final int HEAT_ITERATIONS = 10;

    public static final String branch = "main";

    @BeforeEach
    public void beforeEach() {
        System.out.println("Java: " + System.getProperty("java.version"));
    }

    protected BufferedWriter getWriter(String filename) {
        try {
            return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), UTF_8));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void testSort(int[] list, IntSorter[] sorters, TestSortResults testSortResults) {
        int[] baseListSorted = null;
        for (int i = 0; i < sorters.length; i++) {
            IntSorter sorter = sorters[i];
            int[] listAux = Arrays.copyOf(list, list.length);
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

    public void testSpeed(IntSorter[] sorters, int iterations, GeneratorParams params, TestSortResults testSortResults, Writer writer) throws IOException {
        Function<GeneratorParams, int[]> function = IntGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            int[] list = function.apply(params);
            testSort(list, sorters, testSortResults);
        }
        printTestSpeed(sorters, params, testSortResults, writer);
    }

    protected void printTestSpeed(Sorter[] sorters, GeneratorParams params, TestSortResults testSortResults, Writer writer) throws IOException {
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
        Map winners = getWinners(testSortResults, sorters);
        String sorterWinner = (String) winners.get("sorterWinner");
        long sorterWinnerTime = (Long) winners.get("sorterWinnerTime");
        String sorter2ndWinner = (String) winners.get("sorter2ndWinner");
        long sorter2ndWinnerTime = (Long) winners.get("sorter2ndWinnerTime");
        System.out.printf("%21s %,13d ", sorterWinner, sorterWinnerTime);
        System.out.printf("%21s %,13d ", sorter2ndWinner, sorter2ndWinnerTime);
        System.out.println();
    }

    protected Map<String, Object> getWinners(TestSortResults testSortResults, Sorter[] sorters) {
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
        Map result = new HashMap();
        result.put("sorterWinner", sorterWinner);
        result.put("sorterWinnerTime", sorterWinnerTime);
        result.put("sorter2ndWinner", sorter2ndWinner);
        result.put("sorter2ndWinnerTime", sorter2ndWinnerTime);
        return result;
    }

    public void testSort(long[] list, LongSorter[] sorters, TestSortResults testSortResults) {
        long[] baseListSorted = null;
        for (int i = 0; i < sorters.length; i++) {
            LongSorter sorter = sorters[i];
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

    public void testSpeed(LongSorter[] sorters, int iterations, GeneratorParams params, TestSortResults testSortResults, Writer writer) throws IOException {
        Function<GeneratorParams, long[]> function = LongGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            long[] list = function.apply(params);
            testSort(list, sorters, testSortResults);
        }
        printTestSpeed(sorters, params, testSortResults, writer);
    }

    public void testSort(float[] list, FloatSorter[] sorters, TestSortResults testSortResults) {
        float[] baseListSorted = null;
        for (int i = 0; i < sorters.length; i++) {
            FloatSorter sorter = sorters[i];
            float[] listAux = Arrays.copyOf(list, list.length);
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

    public void testSpeed(FloatSorter[] sorters, int iterations, GeneratorParams params, TestSortResults testSortResults, Writer writer) throws IOException {
        Function<GeneratorParams, float[]> function = FloatGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            float[] list = function.apply(params);
            testSort(list, sorters, testSortResults);
        }
        printTestSpeed(sorters, params, testSortResults, writer);
    }

    public void testSort(double[] list, DoubleSorter[] sorters, TestSortResults testSortResults) {
        double[] baseListSorted = null;
        for (int i = 0; i < sorters.length; i++) {
            DoubleSorter sorter = sorters[i];
            double[] listAux = Arrays.copyOf(list, list.length);
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


    public void testSpeed(DoubleSorter[] sorters, int iterations, GeneratorParams params, TestSortResults testSortResults, Writer writer) throws IOException {
        Function<GeneratorParams, double[]> function = DoubleGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            double[] list = function.apply(params);
            testSort(list, sorters, testSortResults);
        }
        printTestSpeed(sorters, params, testSortResults, writer);
    }

}
