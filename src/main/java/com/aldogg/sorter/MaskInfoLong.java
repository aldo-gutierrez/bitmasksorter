package com.aldogg.sorter;

import com.aldogg.parallel.ArrayParallelRunner;
import com.aldogg.parallel.ArrayRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.aldogg.sorter.long_.LongSorter.LONG_SIGN_BIT_POS;

public class MaskInfoLong {
    long p_mask;
    long  i_mask;

    public static MaskInfoLong getMaskBit(final long[] array, final int start, final int end) {
        long p_mask = 0x0000000000000000;
        long i_mask = 0x0000000000000000;
        for (int i = start; i < end; i++) {
            long e = array[i];
            p_mask = p_mask | e;
            i_mask = i_mask | (~e);
        }
        MaskInfoLong m = new MaskInfoLong();
        m.p_mask = p_mask;
        m.i_mask = i_mask;
        return m;
    }

    public static MaskInfoLong getMaskBit(final double[] array, final int start, final int end) {
        long p_mask = 0x0000000000000000;
        long i_mask = 0x0000000000000000;
        for (int i = start; i < end; i++) {
            long e = Double.doubleToRawLongBits(array[i]);
            p_mask = p_mask | e;
            i_mask = i_mask | (~e);
        }
        MaskInfoLong m = new MaskInfoLong();
        m.p_mask = p_mask;
        m.i_mask = i_mask;
        return m;
    }


    public static MaskInfoLong getMaskBitParallel(final long[] array, final int start, final int end, final ArrayParallelRunner.APRParameters parameters) {
        return ArrayParallelRunner.runInParallel(array, start, end, parameters, new ArrayRunnable<MaskInfoLong>() {

            @Override
            public MaskInfoLong map(Object array1, int start1, int end1, int index, AtomicBoolean stop) {
                return getMaskBit((long[]) array1, start1, end1);
            }

            @Override
            public MaskInfoLong reduce(final MaskInfoLong m1, final MaskInfoLong m2) {
                MaskInfoLong res = new MaskInfoLong();
                res.p_mask = m1.p_mask | m2.p_mask;
                res.i_mask = m1.i_mask | m2.i_mask;
                return res;
            }
        });
    }

    public static int[] getMaskAsArray(final long mask) {
        List<Integer> list = new ArrayList<>();
        for (int i = LONG_SIGN_BIT_POS; i >= 0; i--) {
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

    public static long getMaskLastBits(final int[] kList, final int kIndex) {
        long mask = 0;
        for (int i = kIndex; i < kList.length; i++) {
            int k = kList[i];
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
        return p_mask & i_mask;
    }

}
