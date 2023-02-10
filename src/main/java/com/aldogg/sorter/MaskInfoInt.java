package com.aldogg.sorter;

import com.aldogg.parallel.ArrayRunnableInt;
import com.aldogg.parallel.ArrayParallelRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MaskInfoInt {
    public int p_mask;
    public int i_mask;

    public static MaskInfoInt getMaskBit(final int[] array, final int start, final int end) {
        int p_mask = 0x00000000;
        int i_mask = 0x00000000;
        for (int i = start; i < end; i++) {
            int e = array[i];
            p_mask = p_mask | e;
            i_mask = i_mask | (~e);
        }
        MaskInfoInt m = new MaskInfoInt();
        m.p_mask = p_mask;
        m.i_mask = i_mask;
        return m;
    }

    public static MaskInfoInt getMaskBitDetectSignBit(final int[] array, final int start, final int end) {
        int p_mask = 0x00000000;
        int i_mask = 0x00000000;
        int i = start;
        for (; i < end; i += 512) {
            int j = Math.min(i + 512, end);
            for (; i < j; i++) {
                int e = array[i];
                p_mask = p_mask | e;
                i_mask = i_mask | (~e);
            }
            if (p_mask < 0) {
                if (i_mask < 0) {
                    return null;
                }
            }
        }
        MaskInfoInt m = new MaskInfoInt();
        m.p_mask = p_mask;
        m.i_mask = i_mask;
        return m;
    }

    public static MaskInfoInt getMaskBit(final float[] array, final int start, final int end) {
        int p_mask = 0x00000000;
        int i_mask = 0x00000000;
        for (int i = start; i < end; i++) {
            int e = Float.floatToRawIntBits(array[i]);
            p_mask = p_mask | e;
            i_mask = i_mask | (~e);
        }
        MaskInfoInt m = new MaskInfoInt();
        m.p_mask = p_mask;
        m.i_mask = i_mask;
        return m;
    }


    public static MaskInfoInt getMaskBitParallel(final int[] array, final int start, final int end, final int maxThreads, final AtomicInteger numThreads) {
        return ArrayParallelRunner.runInParallel(array, start, end, maxThreads, numThreads, new ArrayRunnableInt<MaskInfoInt>() {
            @Override
            public MaskInfoInt map(final int[] list, final int start1, final int end1) {
                return getMaskBit(list, start1, end1);
            }

            @Override
            public MaskInfoInt reduce(final MaskInfoInt m1, final MaskInfoInt m2) {
                MaskInfoInt res = new MaskInfoInt();
                res.p_mask = m1.p_mask | m2.p_mask;
                res.i_mask = m1.i_mask | m2.i_mask;
                return res;
            }
        });
    }

    public static int[] getMaskAsArray(final int mask) {
        List<Integer> list = new ArrayList<>();
        for (int i = 31; i >= 0; i--) {
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

    public static int getMaskLastBits(final int[] kList, final int kIndex) {
        int mask = 0;
        for (int i = kIndex; i < kList.length; i++) {
            int k = kList[i];
            mask = mask | 1 << k;
        }
        return mask;
    }

    public static int getMaskRangeBits(final int kIndexStart, final int kIndexEnd) {
        int mask = 0;
        for (int k = kIndexStart; k >= kIndexEnd; k--) {
            mask = mask | 1 << k;
        }
        return mask;
    }

    public int getMask() {
        return p_mask & i_mask;
    }


}
