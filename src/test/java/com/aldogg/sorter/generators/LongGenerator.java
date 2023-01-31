package com.aldogg.sorter.generators;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class LongGenerator {

    private static final ConcurrentHashMap<String, Function<GeneratorParams, long[]>> functions = new ConcurrentHashMap<>();

    static {
        functions.put(String.valueOf(GeneratorFunctions.SHUFFLED), LongGenerator::shuffled_int);
        functions.put(String.valueOf(GeneratorFunctions.SHUFFLED_16_VALUES), LongGenerator::shuffled_16_values_int);
        functions.put(String.valueOf(GeneratorFunctions.ALL_EQUAL), LongGenerator::all_equal_int);
        functions.put(String.valueOf(GeneratorFunctions.ASCENDING), LongGenerator::ascending_int);
        functions.put(String.valueOf(GeneratorFunctions.DESCENDING), LongGenerator::descending_int);
        functions.put(String.valueOf(GeneratorFunctions.PIPE_ORGAN), LongGenerator::pipe_organ_int);
        functions.put(String.valueOf(GeneratorFunctions.PUSH_FRONT), LongGenerator::push_front_int);
        functions.put(String.valueOf(GeneratorFunctions.PUSH_MIDDLE), LongGenerator::push_middle_int);
        functions.put(String.valueOf(GeneratorFunctions.ASCENDING_SAWTOOTH), LongGenerator::ascending_sawtooth_int);
        functions.put(String.valueOf(GeneratorFunctions.DESCENDING_SAWTOOTH), LongGenerator::descending_sawtooth_int);
        functions.put(String.valueOf(GeneratorFunctions.ALTERNATING), LongGenerator::alternating_int);
        functions.put(String.valueOf(GeneratorFunctions.ALTERNATING_16_VALUES), LongGenerator::alternating_16_values_int);
        functions.put(String.valueOf(GeneratorFunctions.RANDOM_INTEGER_RANGE), LongGenerator::random_int_range);
    }

    public static Function<GeneratorParams, long[]> getGFunction(GeneratorFunctions functionName) {
        return functions.get(String.valueOf(functionName));
    }

    public LongGenerator(){

    }



    static long[] shuffled_int(GeneratorParams params) {
        int size = params.size;
        long[] v = new long[size];
        for (int i = 0; i < size; ++i) {
            v[i] = i;
        }
        shuffleArray(v, params.random);
        return v;
    }

    static long[] shuffled_16_values_int(GeneratorParams params) {
        int size = params.size;
        long[] v = new long[size];
        for (int i = 0; i < size; ++i) {
            v[i] = (i % 16);
        }
        shuffleArray(v, params.random);
        return v;
    }

    static long[] all_equal_int(GeneratorParams params) {
        int size = params.size;
        long[] v = new long[size];
        for (int i = 0; i < size; ++i) {
            v[i] = 0;
        }
        return v;
    }

    static long[] ascending_int(GeneratorParams params) {
        int size = params.size;
        long[] v = new long[size];
        for (int i = 0; i < size; ++i) {
            v[i] = i;
        }
        return v;
    }

    static long[] descending_int(GeneratorParams params) {
        int size = params.size;
        long[] v = new long[size];
        for (int i = size - 1, count = 0; i >= 0; --i) {
            v[i] = count++;
        }
        return v;
    }

    static long[] pipe_organ_int(GeneratorParams params) {
        int size = params.size;
        long[] v = new long[size];
        for (int i = 0; i < size / 2; ++i) {
            v[i] = i;
        }
        for (int i = size / 2; i < size; ++i) {
            v[i] = size - i;
        }
        return v;
    }

    static long[] push_front_int(GeneratorParams params) {
        int size = params.size;
        long[] v = new long[size];
        int i = 1;
        for (; i < size; ++i) {
            v[i] = i;
        }
        v[size-1] = 0;
        return v;
    }

    static long[] push_middle_int(GeneratorParams params) {
        int size = params.size;
        long[] v = new long[size];
        int i = 0;
        for (; i < size; ++i) {
            if (i != size / 2) {
                v[i] = i;
            }
        }
        v[size-1] = size / 2;
        return v;
    }

    static long[] ascending_sawtooth_int(GeneratorParams params) {
        int size = params.size;
        long[] v = new long[size];
        int limit = (int) (size / log2(size) * 1.1);
        for (int i = 0; i < size; ++i) {
            v[i] = (i % limit);
        }
        return v;
    }

    static long[] descending_sawtooth_int(GeneratorParams params) {
        int size = params.size;
        long[] v = new long[size];
        int limit = (int) (size / log2(size) * 1.1);
        for (int i = size - 1; i >= 0; --i) {
            v[i] = (i % limit);
        }
        return v;
    }

    static long[] alternating_int(GeneratorParams params) {
        int size = params.size;
        long[] v = new long[size];
        for (int i = 0; i < size; ++i) {
            v[i] = i;
        }
        for (int i = 0; i < size; i += 2) v[i] *= -1;
        return v;
    }

    static long[] alternating_16_values_int(GeneratorParams params) {
        int size = params.size;
        long[] v = new long[size];
        for (int i = 0; i < size; ++i) {
            v[i] = (i % 16);
        }
        for (int i = 0; i < size; i += 2) v[i] *= -1;
        return v;
    }

    static long[] random_int_range(GeneratorParams params) {
        int size = params.size;
        long[] v = new long[size];
        long range = (params.limitHigh - params.limitLow);
        for (int i = 0; i < size; ++i) {
            long randomNumber = (long) (params.random.nextDouble() * range + params.limitLow);
            v[i] = randomNumber;
        }
        return v;
    }


    private static void shuffleArray(long[] array, Random random) {
        int index;
        long temp;
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
