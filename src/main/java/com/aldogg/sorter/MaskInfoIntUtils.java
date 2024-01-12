package com.aldogg.sorter;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;

import java.util.Random;

import static com.aldogg.sorter.MaskInfoInt.calculateMask;
import static jdk.incubator.vector.VectorOperators.OR;

public class MaskInfoIntUtils {

    static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

    public static MaskInfoInt calculateMaskVector(final int[] array, final int start, final int endP1) {
        int upperBound = SPECIES.loopBound(array.length);
        IntVector vPMask = IntVector.zero(SPECIES);
        IntVector vIMask = IntVector.zero(SPECIES);
        int startV = start;
        for (; startV < upperBound; startV += SPECIES.length()) {
            var v = IntVector.fromArray(SPECIES, array, startV);
            var vNot = v.not();
            vPMask = vPMask.or(v);
            vIMask = vIMask.or(vNot);
        }
        int pMask = vPMask.reduceLanes(OR);
        int iMask = vIMask.reduceLanes(OR);

        for (int i = startV; i < endP1; i++) {
            int e = array[i];
            pMask = pMask | e;
            iMask = iMask | ~e;
        }
        return new MaskInfoInt(pMask, iMask);
    }

    public static void main (String[] args) {
        long timeA = 0;
        long timeB = 0;
        int n = 100;
        int size = 10000000; //Minimum number in 10 power in my machine where vector is faster
        Random random = new Random();
        for (int i =0; i< n; i++) {
            int[] array = new int[size];
            for (int j=0; j < size; j++) {
                array[j] = random.nextInt(20000);
            }
            long start = System.currentTimeMillis();
            MaskInfoInt m1 = calculateMask(array, 0, size);
            timeA+= System.currentTimeMillis() - start;

            start = System.currentTimeMillis();
            MaskInfoInt m2 = calculateMaskVector(array, 0, size);
            timeB+= System.currentTimeMillis() - start;

            if (m1.getMask() != m2.getMask()) {
                System.out.println("Distintos");
            }
        }
        System.out.println(timeA);
        System.out.println(timeB);

    }
}
