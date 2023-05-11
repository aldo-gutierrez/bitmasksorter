package com.aldogg.sorter.test.performance;

import com.aldogg.parallel.ArrayParallelRunner;
import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.MaskInfoInt;
import com.aldogg.sorter.Section;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static com.aldogg.sorter.MaskInfoInt.calculateMaskInParallel;
import static com.aldogg.sorter.int_.IntSorterUtils.*;

public class GetBitMaskTestPTest {

    @Test
    public void testGetMaskBitParallel() {
        Random random = new Random();
        int arraySize = 6000000;
        int iterations = 100;
        long total = 0;
        MaskInfoInt c = null;
        for (int iteration = 0; iteration < iterations; iteration++) {
            int[] a = new int[arraySize];
            for (int i = 0; i < arraySize; i++) {
                a[i] = random.nextInt();
            }
            long start = System.nanoTime();
            c = MaskInfoInt.calculateMask(a, 0, a.length);
            long elapsed = System.nanoTime() - start;
            total += elapsed;
        }
        System.out.println(c);
        System.out.println("total = " + total);
        System.out.println("iterations = " + iterations);
        double t1 = (double) total / iterations;
        System.out.println("total/iterations = " + t1);
        total = 0;
        for (int iteration = 0; iteration < iterations; iteration++) {
            int[] a = new int[arraySize];
            for (int i = 0; i < arraySize; i++) {
                a[i] = random.nextInt();
            }
            long start = System.nanoTime();
            c = calculateMaskInParallel(a, 0, a.length, ArrayParallelRunner.APR_PARAMETERS_TWO_THREADS);
            long elapsed = System.nanoTime() - start;
            total += elapsed;
        }
        System.out.println(c);
        System.out.println("total = " + total);
        System.out.println("iterations = " + iterations);
        double t2 = (double) total / iterations;
        System.out.println("total/iterations = " + t2);
        Assertions.assertTrue(t2 < t1);
    }

    @Test
    public void testPartitionStableLastBitsParallel() {
        Random random = new Random();
        int arraySize = 20000000;
        int iterations = 100;
        long total = 0;
        MaskInfoInt maskInfo = null;

        for (int iteration = 0; iteration < iterations; iteration++) {
            int[] a = new int[arraySize];
            for (int i = 0; i < arraySize; i++) {
                a[i] = random.nextInt();
            }
            maskInfo = MaskInfoInt.calculateMask(a, 0, a.length);
            int mask = maskInfo.getMask();
            int[] bList = MaskInfoInt.getMaskAsArray(mask);
            Section[] finalSectionList = BitSorterUtils.getOrderedSections(bList, 0, bList.length - 1);
            int[] aux = new int[arraySize];
            long start = System.nanoTime();
            partitionStableLastBits(a, 0, finalSectionList[0], aux, 0, arraySize);
            long elapsed = System.nanoTime() - start;
            total += elapsed;
        }
        System.out.println(maskInfo);
        System.out.println("total = " + total);
        System.out.println("iterations = " + iterations);
        double t1 = (double) total / iterations;
        System.out.println("total/iterations = " + t1);
        total = 0;
        for (int iteration = 0; iteration < iterations; iteration++) {
            int[] a = new int[arraySize];
            for (int i = 0; i < arraySize; i++) {
                a[i] = random.nextInt();
            }
            maskInfo = MaskInfoInt.calculateMask(a, 0, a.length);
            int mask = maskInfo.getMask();
            int[] bList = MaskInfoInt.getMaskAsArray(mask);
            Section[] finalSectionList = BitSorterUtils.getOrderedSections(bList, 0, bList.length - 1);
            int[] aux = new int[arraySize];
            long start = System.nanoTime();
            partitionStableLastBitsParallel(a, 0, finalSectionList[0], aux, arraySize);
            long elapsed = System.nanoTime() - start;
            total += elapsed;
        }
        System.out.println(maskInfo);
        System.out.println("total = " + total);
        System.out.println("iterations = " + iterations);
        double t2 = (double) total / iterations;
        System.out.println("total/iterations = " + t2);
        Assertions.assertTrue(t2 < t1);
    }

    @Test
    public void testPartitionStableParallel() {
        Random random = new Random();
        int arraySize = 1000000;
        int iterations = 100;
        MaskInfoInt maskInfo = null;

        long total1 = 0;
        long total2 = 0;

        for (int iteration = 0; iteration < iterations; iteration++) {
            int[] a1 = new int[arraySize];
            for (int i = 0; i < arraySize; i++) {
                a1[i] = random.nextInt();
            }
            int[] a2 = Arrays.copyOf(a1, a1.length);

            int[] aux = new int[arraySize];
            long start1 = System.nanoTime();
            int left1 = partitionStable(a1, 0, arraySize, 0x80000000, aux);
            long elapsed1 = System.nanoTime() - start1;
            total1 += elapsed1;

            aux = new int[arraySize];
            long start2 = System.nanoTime();
            int left2 = partitionStableParallel(a2, 0, arraySize, 0x80000000, aux);
            long elapsed2 = System.nanoTime() - start2;
            total2 += elapsed2;

            Assertions.assertArrayEquals(a1, a2);
            Assertions.assertEquals(left1, left2);
        }
        System.out.println(maskInfo);
        System.out.println("total = " + total1);
        System.out.println("iterations = " + iterations);
        double t1 = (double) total1 / iterations;
        System.out.println("total/iterations = " + t1);

        System.out.println(maskInfo);
        System.out.println("total = " + total2);
        System.out.println("iterations = " + iterations);
        double t2 = (double) total2 / iterations;
        System.out.println("total/iterations = " + t2);
        Assertions.assertTrue(t2 < t1);
    }

    @Test
    public void testPartitionStableParallel2() {
        Random random = new Random();
        int arraySize = 1000000;
        int iterations = 100;
        MaskInfoInt maskInfo = null;

        long total1 = 0;
        long total2 = 0;

        for (int iteration = 0; iteration < iterations; iteration++) {
            int[] a1 = new int[arraySize];
            for (int i = 0; i < arraySize; i++) {
                a1[i] = random.nextInt();
            }
            int[] a2 = Arrays.copyOf(a1, a1.length);

            int[] aux = new int[arraySize];
            long start1 = System.nanoTime();
            int left1 = partitionStable(a1, 0, arraySize, 0x80000000, aux);
            long elapsed1 = System.nanoTime() - start1;
            total1 += elapsed1;

            aux = new int[arraySize];
            long start2 = System.nanoTime();
            int left2 = partitionStableParallel2(a2, 0, arraySize, 0x80000000, aux);
            long elapsed2 = System.nanoTime() - start2;
            total2 += elapsed2;

            Assertions.assertArrayEquals(a1, a2);
            Assertions.assertEquals(left1, left2);
        }
        System.out.println(maskInfo);
        System.out.println("total = " + total1);
        System.out.println("iterations = " + iterations);
        double t1 = (double) total1 / iterations;
        System.out.println("total/iterations = " + t1);

        System.out.println(maskInfo);
        System.out.println("total = " + total2);
        System.out.println("iterations = " + iterations);
        double t2 = (double) total2 / iterations;
        System.out.println("total/iterations = " + t2);
        Assertions.assertTrue(t2 < t1);
    }

    @Test
    public void testPartitionStableParallel2b() {
        //partitionStableParallel  is faster than partitionStableParallel2 when using two threads only
        Random random = new Random();
        int arraySize = 1000000;
        int iterations = 100;
        MaskInfoInt maskInfo = null;

        long total1 = 0;
        long total2 = 0;

        for (int iteration = 0; iteration < iterations; iteration++) {
            int[] a1 = new int[arraySize];
            for (int i = 0; i < arraySize; i++) {
                a1[i] = random.nextInt();
            }
            int[] a2 = Arrays.copyOf(a1, a1.length);

            int[] aux = new int[arraySize];
            long start1 = System.nanoTime();
            int left1 = partitionStableParallel2(a1, 0, arraySize, 0x80000000, aux);
            long elapsed1 = System.nanoTime() - start1;
            total1 += elapsed1;

            aux = new int[arraySize];
            long start2 = System.nanoTime();
            int left2 = partitionStableParallel(a2, 0, arraySize, 0x80000000, aux);
            long elapsed2 = System.nanoTime() - start2;
            total2 += elapsed2;

            Assertions.assertArrayEquals(a1, a2);
            Assertions.assertEquals(left1, left2);
        }
        System.out.println(maskInfo);
        System.out.println("total = " + total1);
        System.out.println("iterations = " + iterations);
        double t1 = (double) total1 / iterations;
        System.out.println("total/iterations = " + t1);

        System.out.println(maskInfo);
        System.out.println("total = " + total2);
        System.out.println("iterations = " + iterations);
        double t2 = (double) total2 / iterations;
        System.out.println("total/iterations = " + t2);
        Assertions.assertTrue(t2 < t1);
    }
}
