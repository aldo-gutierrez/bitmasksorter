package com.aldogg.sorter.generators;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class FloatGenerator {
    private static final ConcurrentHashMap<String, Function<GeneratorParams, float[]>> functions = new ConcurrentHashMap<>();

    static {
        functions.put(String.valueOf(GeneratorFunctions.RANDOM_INTEGER_RANGE), FloatGenerator::random_int_range);
        functions.put(String.valueOf(GeneratorFunctions.RANDOM_REAL_RANGE), FloatGenerator::random_real_range);
    }

    public static Function<GeneratorParams, float[]> getGFunction(GeneratorFunctions functionName) {
        return functions.get(String.valueOf(functionName));
    }

    public FloatGenerator() {

    }

    static float[] random_int_range(GeneratorParams params) {
        int size = params.size;
        float[] v = new float[size];
        if (params.limitHigh > Integer.MAX_VALUE) {
            int range = (int) (params.limitHigh - (long) params.limitLow);
            for (int i = 0; i < size; ++i) {
                long randomLong = params.random.nextInt(range) + (long) params.limitLow;
                v[i] = ((Long) randomLong).intValue();
            }
        } else {
            int range = (int) (params.limitHigh - params.limitLow);
            for (int i = 0; i < size; ++i) {
                int randomInt = params.random.nextInt(range) + params.limitLow;
                v[i] = randomInt;
            }
        }
        return v;
    }

    static float[] random_real_range(GeneratorParams params) {
        int size = params.size;
        float[] v = new float[size];
        int range = (int) (params.limitHigh - (long) params.limitLow);
        for (int i = 0; i < size; ++i) {
            v[i] = params.random.nextFloat() * range + params.limitLow;
        }
        return v;
    }

}
