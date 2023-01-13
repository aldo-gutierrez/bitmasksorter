package com.aldogg.sorter;

import com.aldogg.parallel.ArrayParallelRunner;
import com.aldogg.parallel.ArrayRunnableLong;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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


    public static MaskInfoLong getMaskBitParallel(final long[] array, final int start, final int end, final int maxThreads, final AtomicInteger numThreads) {
        MaskInfoLong maskInfo = ArrayParallelRunner.runInParallel(array, start, end, maxThreads, numThreads, new ArrayRunnableLong<MaskInfoLong>() {
            @Override
            public MaskInfoLong map(final long[] list, final int start, final int end) {
                return getMaskBit(list, start, end);
            }

            @Override
            public MaskInfoLong reduce(final MaskInfoLong m1, final MaskInfoLong m2) {
                MaskInfoLong res = new MaskInfoLong();
                res.p_mask = m1.p_mask | m2.p_mask;
                res.i_mask = m1.i_mask | m2.i_mask;
                return res;
            }
        });
        return maskInfo;
    }

    public static int[] getMaskAsArray(final long mask) {
        List<Integer> list = new ArrayList<>();
        for (int i = 63; i >= 0; i--) {
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
