package com.aldogg.sorter.test;

import com.aldogg.sorter.FieldOptions;
import com.aldogg.sorter.generators.*;
import com.aldogg.sorter.int_.SorterInt;
import org.junit.jupiter.api.BeforeEach;

import java.io.*;
import java.util.Arrays;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class BaseTest {
    protected final boolean validateResult = true;

    public static final long SEED = 1234567890;
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

    public void testSort(int[] list, TestAlgorithms<SorterInt> testAlgorithms, FieldOptions options) {
        int[] baseListSorted = null;
        SorterInt[] sorters = testAlgorithms.getAlgorithms();
        for (int i = 0; i < sorters.length; i++) {
            SorterInt sorter = sorters[i];
            int[] listAux = Arrays.copyOf(list, list.length);
            try {
                long start = System.nanoTime();
                if (options == null) {
                    sorter.sort(listAux, 0, listAux.length);
                } else {
                    sorter.sort(listAux, 0, listAux.length, options);
                }
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
                if (list.length <= 1024) {
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

    public void testSpeedInt(int iterations, GeneratorParams params, TestAlgorithms testAlgorithms, FieldOptions options) {
        Function<GeneratorParams, int[]> function = IntGenerator.getGFunction(params.function);
        for (int iter = 0; iter < iterations; iter++) {
            int[] list = function.apply(params);
            testSort(list, testAlgorithms, options);
        }
    }


}
