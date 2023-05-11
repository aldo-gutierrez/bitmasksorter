package com.aldogg.sorter;

import com.aldogg.parallel.ArrayParallelRunner;
import com.aldogg.parallel.ArrayRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MaskInfoLong {
    public static final int UPPER_BIT = 63;
    long pMask;
    long iMask;

    public static MaskInfoLong calculateMask(final long[] array, final int start, final int endP1) {
        long pMask = 0x0000000000000000;
        long iMask = 0x0000000000000000;
        for (int i = start; i < endP1; i++) {
            long e = array[i];
            pMask = pMask | e;
            iMask = iMask | ~e;
        }
        MaskInfoLong m = new MaskInfoLong();
        m.pMask = pMask;
        m.iMask = iMask;
        return m;
    }

    public static MaskInfoLong calculateMask(final double[] array, final int start, final int endP1) {
        long pMask = 0x0000000000000000;
        long iMask = 0x0000000000000000;
        for (int i = start; i < endP1; i++) {
            long e = Double.doubleToRawLongBits(array[i]);
            pMask = pMask | e;
            iMask = iMask | ~e;
        }
        MaskInfoLong m = new MaskInfoLong();
        m.pMask = pMask;
        m.iMask = iMask;
        return m;
    }


    public static MaskInfoLong calculateMaskInParallel(final long[] array, final int start, final int endP1, final ArrayParallelRunner.APRParameters parameters) {
        return ArrayParallelRunner.runInParallel(array, start, endP1, parameters, new ArrayRunnable<MaskInfoLong>() {

            @Override
            public MaskInfoLong map(Object array1, int start1, int endP1, int index, AtomicBoolean stop) {
                return calculateMask((long[]) array1, start1, endP1);
            }

            @Override
            public MaskInfoLong reduce(final MaskInfoLong m1, final MaskInfoLong m2) {
                MaskInfoLong res = new MaskInfoLong();
                res.pMask = m1.pMask | m2.pMask;
                res.iMask = m1.iMask | m2.iMask;
                return res;
            }
        });
    }

    public static int[] getMaskAsArray(final long mask) {
        List<Integer> list = new ArrayList<>();
        for (int i = UPPER_BIT; i >= 0; i--) {
            if (((mask >> i) & 1) == 1) {
                list.add(i);
            }
        }
        int[] res = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            res[i] = list.get(i);
        }
        return res;
    }

    public static long getMaskLastBits(final int[] bList, final int kIndex) {
        long mask = 0;
        for (int i = kIndex; i < bList.length; i++) {
            int k = bList[i];
            mask = mask | 1L << k;
        }
        return mask;
    }

    public static long getMaskRangeBits(final int kIndexStart, final int kIndexEnd) {
        long mask = 0;
        for (int k = kIndexStart; k >= kIndexEnd; k--) {
            mask = mask | 1L << k;
        }
        return mask;
    }

    public long getMask() {
        return pMask & iMask;
    }

}
