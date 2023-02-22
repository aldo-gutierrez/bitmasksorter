package com.aldogg.sorter.test;

import com.aldogg.sorter.BitSorterUtils;
import com.aldogg.sorter.IntSection;
import com.aldogg.sorter.IntSectionsInfo;
import com.aldogg.sorter.MaskInfoInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static com.aldogg.sorter.MaskInfoInt.*;
import static com.aldogg.sorter.intType.IntSorterUtils.partitionStableLastBits;
import static com.aldogg.sorter.intType.IntSorterUtils.partitionStableLastBitsParallel;

public class BitMaskTest {

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
            c = getMaskBit(a, 0, a.length);
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
            c = getMaskBitParallel(a, 0, a.length, 2, null);
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
            maskInfo = getMaskBit(a, 0, a.length);
            int mask = maskInfo.getMask();
            int[] kList = MaskInfoInt.getMaskAsArray(mask);
            IntSectionsInfo sectionsInfo = BitSorterUtils.getOrderedSections(kList, 0, kList.length - 1);
            IntSection[] finalSectionList = sectionsInfo.sections;
            int[] aux = new int[arraySize];
            long start = System.nanoTime();
            partitionStableLastBits(a, 0, finalSectionList[0], aux, arraySize);
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
            maskInfo = getMaskBit(a, 0, a.length);
            int mask = maskInfo.getMask();
            int[] kList = MaskInfoInt.getMaskAsArray(mask);
            IntSectionsInfo sectionsInfo = BitSorterUtils.getOrderedSections(kList, 0, kList.length - 1);
            IntSection[] finalSectionList = sectionsInfo.sections;
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

}
