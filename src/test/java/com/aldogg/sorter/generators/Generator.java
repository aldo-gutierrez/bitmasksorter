package com.aldogg.sorter.generators;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * https://github.com/Morwenn/vergesort/blob/master/bench/bench.cpp
 */
public class Generator {
    public static final String SHUFFLED_INT = "shuffled_int";
    public static final String SHUFFLED_16_VALUES_INT = "shuffled_16_values_int";
    public static final String ALL_EQUAL_INT = "all_equal_int";
    public static final String ASCENDING_INT = "ascending_int";
    public static final String DESCENDING_INT = "descending_int";
    public static final String PIPE_ORGAN_INT = "pipe_organ_int";
    public static final String PUSH_FRONT_INT = "push_front_int";
    public static final String PUSH_MIDDLE_INT = "push_middle_int";
    public static final String ASCENDING_SAWTOOTH_INT = "ascending_sawtooth_int";
    public static final String DESCENDING_SAWTOOTH_INT = "descending_sawtooth_int";
    public static final String ALTERNATING_INT = "alternating_int";
    public static final String ALTERNATING_16_VALUES_INT = "alternating_16_values_int";
    public static final String ORIGINAL_INT = "original_int";
    public static final String ORIGINAL_UNSIGNED_INT = "original_unsigned_int";
    ConcurrentHashMap<String, Function<GeneratorParams, int[]>> functions = new ConcurrentHashMap<>();


    public Generator() {
        functions.put(SHUFFLED_INT, this::shuffled_int);
        functions.put(SHUFFLED_16_VALUES_INT, this::shuffled_16_values_int);
        functions.put(ALL_EQUAL_INT, this::all_equal_int);
        functions.put(ASCENDING_INT, this::ascending_int);
        functions.put(DESCENDING_INT, this::descending_int);
        functions.put(PIPE_ORGAN_INT, this::pipe_organ_int);
        functions.put(PUSH_FRONT_INT, this::push_front_int);
        functions.put(PUSH_MIDDLE_INT, this::push_middle_int);
        functions.put(ASCENDING_SAWTOOTH_INT, this::ascending_sawtooth_int);
        functions.put(DESCENDING_SAWTOOTH_INT, this::descending_sawtooth_int);
        functions.put(ALTERNATING_INT, this::alternating_int);
        functions.put(ALTERNATING_16_VALUES_INT, this::alternating_16_values_int);
        functions.put(ORIGINAL_INT, this::original_int);
        functions.put(ORIGINAL_UNSIGNED_INT, this::original_unsigned_int);
    }

    public Function<GeneratorParams, int[]> getGeneratorFunction(String functionName) {
        return functions.get(functionName);
    }



    int[] shuffled_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        for (int i = 0; i < size; ++i) {
            v[i] = i;
        }
        shuffleArray(v, params.random);
        return v;
    }

    int[] shuffled_16_values_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        for (int i = 0; i < size; ++i) {
            v[i] = (i % 16);
        }
        shuffleArray(v, params.random);
        return v;
    }

    int[] all_equal_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        for (int i = 0; i < size; ++i) {
            v[i] = 0;
        }
        return v;
    }

    int[] ascending_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        for (int i = 0; i < size; ++i) {
            v[i] = i;
        }
        return v;
    }

    int[] descending_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        for (int i = size - 1; i >= 0; --i) {
            v[i] = i;
        }
        return v;
    }

    int[] pipe_organ_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        for (int i = 0; i < size / 2; ++i) {
            v[i] = i;
        }
        for (int i = size / 2; i < size; ++i) {
            v[i] = size - i;
        }
        return v;
    }

    int[] push_front_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        int i = 1;
        for (; i < size; ++i) {
            v[i] = i;
        }
        v[i] = 0;
        return v;
    }

    int[] push_middle_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        int i = 0;
        for (; i < size; ++i) {
            if (i != size / 2) {
                v[i] = i;
            }
        }
        v[i] = size / 2;
        return v;
    }

    int[] ascending_sawtooth_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        int limit = (int) (size / log2(size) * 1.1);
        for (int i = 0; i < size; ++i) {
            v[i] = (i % limit);
        }
        return v;
    }

    int[] descending_sawtooth_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        int limit = (int) (size / log2(size) * 1.1);
        for (int i = size - 1; i >= 0; --i) {
            v[i] = (i % limit);
        }
        return v;
    }

    int[] alternating_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        for (int i = 0; i < size; ++i) {
            v[i] = i;
        }
        for (int i = 0; i < size; i += 2) v[i] *= -1;
        return v;
    }

    int[] alternating_16_values_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        for (int i = 0; i < size; ++i) {
            v[i] = (i % 16);
        }
        for (int i = 0; i < size; i += 2) v[i] *= -1;
        return v;
    }

    int[] original_int(GeneratorParams params) {
        int size = params.size;
        int f = (int) (params.limitHigh - params.limitLow);
        int[] v = new int[size];
        for (int i = 0; i < size; ++i) {
            int randomInt = params.random.nextInt(f) + params.limitLow;
            v[i] = randomInt;
        }
        return v;
    }

    int[] original_unsigned_int(GeneratorParams params) {
        int limitLow = Integer.MAX_VALUE - 1000;
        int size = params.size;
        int[] v = new int[size];
        for (int i = 0; i < size; ++i) {
            long randomInt = (long) params.random.nextInt(3000)  + (long) limitLow;
            v[i] =  ((Long) randomInt).intValue();
        }
        return v;
    }


    private void shuffleArray(int[] array, Random random) {
        int index, temp;
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
    private double log2(int N)
    {

        // calculate log2 N indirectly
        // using log() method
        return (Math.log(N) / Math.log(2));

    }
}
