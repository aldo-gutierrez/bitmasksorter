package com.aldogg.sorter.generators;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * <a href="https://github.com/Morwenn/vergesort/blob/master/bench/bench.cpp">...</a>
 */
public class IntGenerator {
    private static final ConcurrentHashMap<String, Function<GeneratorParams, int[]>> functions = new ConcurrentHashMap<>();

    static {
        functions.put(String.valueOf(GeneratorFunctions.SHUFFLED), IntGenerator::shuffled_int);
        functions.put(String.valueOf(GeneratorFunctions.SHUFFLED_16_VALUES), IntGenerator::shuffled_16_values_int);
        functions.put(String.valueOf(GeneratorFunctions.ALL_EQUAL), IntGenerator::all_equal_int);
        functions.put(String.valueOf(GeneratorFunctions.ASCENDING), IntGenerator::ascending_int);
        functions.put(String.valueOf(GeneratorFunctions.DESCENDING), IntGenerator::descending_int);
        functions.put(String.valueOf(GeneratorFunctions.PIPE_ORGAN), IntGenerator::pipe_organ_int);
        functions.put(String.valueOf(GeneratorFunctions.PUSH_FRONT), IntGenerator::push_front_int);
        functions.put(String.valueOf(GeneratorFunctions.PUSH_MIDDLE), IntGenerator::push_middle_int);
        functions.put(String.valueOf(GeneratorFunctions.ASCENDING_SAWTOOTH), IntGenerator::ascending_sawtooth_int);
        functions.put(String.valueOf(GeneratorFunctions.DESCENDING_SAWTOOTH), IntGenerator::descending_sawtooth_int);
        functions.put(String.valueOf(GeneratorFunctions.ALTERNATING), IntGenerator::alternating_int);
        functions.put(String.valueOf(GeneratorFunctions.ALTERNATING_16_VALUES), IntGenerator::alternating_16_values_int);
        functions.put(String.valueOf(GeneratorFunctions.RANDOM_INTEGER_RANGE), IntGenerator::random_int_range);
        functions.put(String.valueOf(GeneratorFunctions.HARCODED), IntGenerator::hardcoded);
    }

    public static Function<GeneratorParams, int[]> getGFunction(GeneratorFunctions functionName) {
        return functions.get(String.valueOf(functionName));
    }

    public IntGenerator() {

    }


    static int[] shuffled_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        for (int i = 0; i < size; ++i) {
            v[i] = i;
        }
        shuffleArray(v, params.random);
        return v;
    }

    static int[] shuffled_16_values_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        for (int i = 0; i < size; ++i) {
            v[i] = (i % 16);
        }
        shuffleArray(v, params.random);
        return v;
    }

    static int[] all_equal_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        for (int i = 0; i < size; ++i) {
            v[i] = 0;
        }
        return v;
    }

    static int[] ascending_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        for (int i = 0; i < size; ++i) {
            v[i] = i;
        }
        return v;
    }

    static int[] descending_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        for (int i = size - 1, count = 0; i >= 0; --i) {
            v[i] = count++;
        }
        return v;
    }

    static int[] pipe_organ_int(GeneratorParams params) {
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

    static int[] push_front_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        int i = 1;
        for (; i < size; ++i) {
            v[i] = i;
        }
        v[size - 1] = 0;
        return v;
    }

    static int[] push_middle_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        int i = 0;
        for (; i < size; ++i) {
            if (i != size / 2) {
                v[i] = i;
            }
        }
        v[size - 1] = size / 2;
        return v;
    }

    static int[] ascending_sawtooth_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        int limit = (int) (size / log2(size) * 1.1);
        for (int i = 0; i < size; ++i) {
            v[i] = (i % limit);
        }
        return v;
    }

    static int[] descending_sawtooth_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        int limit = (int) (size / log2(size) * 1.1);
        for (int i = size - 1; i >= 0; --i) {
            v[i] = (i % limit);
        }
        return v;
    }

    static int[] alternating_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        for (int i = 0; i < size; ++i) {
            v[i] = i;
        }
        for (int i = 0; i < size; i += 2) v[i] *= -1;
        return v;
    }

    static int[] alternating_16_values_int(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        for (int i = 0; i < size; ++i) {
            v[i] = (i % 16);
        }
        for (int i = 0; i < size; i += 2) v[i] *= -1;
        return v;
    }

    static int[] random_int_range(GeneratorParams params) {
        int size = params.size;
        int[] v = new int[size];
        if (params.limitHigh > Integer.MAX_VALUE) {
            int range = (int) (params.limitHigh - (long) params.limitLow);
            for (int i = 0; i < size; ++i) {
                long randomLong = params.random.nextInt(range) + (long) params.limitLow;
                v[i] = ((Long) randomLong).intValue();
            }
        } else {
            int range = (int) (params.limitHigh - params.limitLow);
            for (int i = 0; i < size; ++i) {
                int randomInt = params.random.nextInt(range) + (int) params.limitLow;
                v[i] = randomInt;
            }
        }
        return v;
    }

    static int[] hardcoded(GeneratorParams params) {
        return new int[]{2, -2, -10, 9, -9, 6, 9, 3, -4, -3, 1, 3};
    }


    private static void shuffleArray(int[] array, Random random) {
        int index, temp;
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    static private double log2(int N) {

        // calculate log2 N indirectly
        // using log() method
        return (Math.log(N) / Math.log(2));

    }
}
