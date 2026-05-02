package com.aldogg.sorter.double_.st;

import java.util.Arrays;

public class TestSpecialValues {

    public static void main(String[] args) {
        double[] a = { +0.0f, -0.0f, +0.0f, -0.0f, +0.0f, Double.MAX_VALUE, Double.MIN_VALUE, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN};
        System.out.println("Before: " + Arrays.toString(a));

        Arrays.sort(a);          // sorts by numeric value (uses Float.compare)
        System.out.println("After  : " + Arrays.toString(a));

        // Show the raw bit patterns after sorting
        long[] bits = Arrays.stream(a)
                .mapToLong(Double::doubleToRawLongBits)
                .toArray();
        System.out.println("Longs   : " + Arrays.toString(bits));
        Object[] hex = Arrays.stream(bits).mapToObj(Long::toHexString).toArray();
        System.out.println("Hex   : " + Arrays.toString(hex));

    }
}
