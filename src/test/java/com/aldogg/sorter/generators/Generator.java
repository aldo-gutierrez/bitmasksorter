package com.aldogg.sorter.generators;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * https://github.com/Morwenn/vergesort/blob/master/bench/bench.cpp
 */
public class Generator {
    public enum GeneratorFunctions {
        SHUFFLED_INT,
        SHUFFLED_16_VALUES_INT,
        ALL_EQUAL_INT,
        ASCENDING_INT,
        DESCENDING_INT,
        PIPE_ORGAN_INT,
        PUSH_FRONT_INT,
        PUSH_MIDDLE_INT,
        ASCENDING_SAWTOOTH_INT,
        DESCENDING_SAWTOOTH_INT,
        ALTERNATING_INT,
        ALTERNATING_16_VALUES_INT,
        RANDOM_RANGE_INT,
    }
    private static final ConcurrentHashMap<String, Function<GeneratorParams, int[]>> functions = new ConcurrentHashMap<>();

    static {
        functions.put(String.valueOf(GeneratorFunctions.SHUFFLED_INT), Generator::shuffled_int);
        functions.put(String.valueOf(GeneratorFunctions.SHUFFLED_16_VALUES_INT), Generator::shuffled_16_values_int);
        functions.put(String.valueOf(GeneratorFunctions.ALL_EQUAL_INT), Generator::all_equal_int);
        functions.put(String.valueOf(GeneratorFunctions.ASCENDING_INT), Generator::ascending_int);
        functions.put(String.valueOf(GeneratorFunctions.DESCENDING_INT), Generator::descending_int);
        functions.put(String.valueOf(GeneratorFunctions.PIPE_ORGAN_INT), Generator::pipe_organ_int);
        functions.put(String.valueOf(GeneratorFunctions.PUSH_FRONT_INT), Generator::push_front_int);
        functions.put(String.valueOf(GeneratorFunctions.PUSH_MIDDLE_INT), Generator::push_middle_int);
        functions.put(String.valueOf(GeneratorFunctions.ASCENDING_SAWTOOTH_INT), Generator::ascending_sawtooth_int);
        functions.put(String.valueOf(GeneratorFunctions.DESCENDING_SAWTOOTH_INT), Generator::descending_sawtooth_int);
        functions.put(String.valueOf(GeneratorFunctions.ALTERNATING_INT), Generator::alternating_int);
        functions.put(String.valueOf(GeneratorFunctions.ALTERNATING_16_VALUES_INT), Generator::alternating_16_values_int);
        functions.put(String.valueOf(GeneratorFunctions.RANDOM_RANGE_INT), Generator::random_int_range);
    }

    public static Function<GeneratorParams, int[]> getGFunction(GeneratorFunctions functionName) {
        return functions.get(String.valueOf(functionName));
    }

    public Generator(){

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
        for (int i = size - 1; i >= 0; --i) {
            v[i] = i;
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
        v[size-1] = 0;
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
        v[size-1] = size / 2;
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
                v[i] =  ((Long) randomLong).intValue();
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


    private static void shuffleArray(int[] array, Random random) {
        int index, temp;
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
    static private double log2(int N)
    {

        // calculate log2 N indirectly
        // using log() method
        return (Math.log(N) / Math.log(2));

    }
}
