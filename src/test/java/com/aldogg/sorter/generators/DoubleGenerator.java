package com.aldogg.sorter.generators;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DoubleGenerator {
    private static final ConcurrentHashMap<String, Function<GeneratorParams, double[]>> functions = new ConcurrentHashMap<>();

    static {
        functions.put(String.valueOf(GeneratorFunctions.RANDOM_INTEGER_RANGE), DoubleGenerator::random_int_range);
        functions.put(String.valueOf(GeneratorFunctions.RANDOM_REAL_RANGE), DoubleGenerator::random_real_range);
    }

    public static Function<GeneratorParams, double[]> getGFunction(GeneratorFunctions functionName) {
        return functions.get(String.valueOf(functionName));
    }

    public DoubleGenerator() {

    }

    static double[] random_int_range(GeneratorParams params) {
        int size = params.size;
        double[] v = new double[size];
        long range = params.limitHigh - params.limitLow;
        for (int i = 0; i < size; ++i) {
            double randomNumber = params.random.nextDouble() * range + params.limitLow;
            v[i] = randomNumber;
        }
        return v;
    }

    static double[] random_real_range(GeneratorParams params) {
        int size = params.size;
        double[] v = new double[size];
        int range = (int) (params.limitHigh - (long) params.limitLow);
        for (int i = 0; i < size; ++i) {
            v[i] = params.random.nextDouble() * range + params.limitLow;
        }
        return v;
    }

}
