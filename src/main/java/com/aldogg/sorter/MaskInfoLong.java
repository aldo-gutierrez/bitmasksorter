package com.aldogg.sorter;

import com.aldogg.parallel.ArrayParallelRunner;
import com.aldogg.parallel.ArrayRunnable;
import com.aldogg.sorter.long_.object.LongMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.aldogg.sorter.Constants.CALCULATE_MASK_BATCH_SIZE;

public class MaskInfoLong {
    public static final int UPPER_BIT = 63;
    long pMask;
    long iMask;

    public MaskInfoLong() {
    }

    public MaskInfoLong(long pMask, long iMask) {
        this.pMask = pMask;
        this.iMask = iMask;
    }

    public static MaskInfoLong calculateMask(final long[] array, final int start, final int endP1) {
        long pMask = 0x0000000000000000;
        long iMask = 0x0000000000000000;
        for (int i = start; i < endP1; i++) {
            long e = array[i];
            pMask = pMask | e;
            iMask = iMask | ~e;
        }
        return new MaskInfoLong(pMask, iMask);
    }

    public static MaskInfoLong calculateMask(final double[] array, final int start, final int endP1) {
        long pMask = 0x0000000000000000;
        long iMask = 0x0000000000000000;
        for (int i = start; i < endP1; i++) {
            long e = Double.doubleToRawLongBits(array[i]);
            pMask = pMask | e;
            iMask = iMask | ~e;
        }
        return new MaskInfoLong(pMask, iMask);
    }

    public static MaskInfoLong calculateMask(final Object[] array, final int start, final int endP1, LongMapper mapper) {
        long pMask = 0x0000000000000000;
        long iMask = 0x0000000000000000;
        for (int i = start; i < endP1; i++) {
            long e = mapper.value(array[i]);
            pMask = pMask | e;
            iMask = iMask | ~e;
        }
        return new MaskInfoLong(pMask, iMask);
    }

    public static <T> MaskInfoLong calculateMaskBreakIfUpperBit(final T[] array, final int start, final int endP1, AtomicBoolean stop, LongMapper<T> mapper) {
        long pMask = 0x0000000000000000;
        long iMask = 0x0000000000000000;
        for (int i = start; i < endP1; i += CALCULATE_MASK_BATCH_SIZE) {
            if (pMask < 0) {
                if (iMask < 0) {
                    if (stop != null) {
                        stop.set(true);
                    }
                    return new MaskInfoLong(pMask, iMask);
                }
            }
            if (stop != null) {
                if (stop.get()) {
                    return null;
                }
            }
            int startBatch = i;
            int j = Math.min(i + CALCULATE_MASK_BATCH_SIZE, endP1);
            for (; i < j; i++) {
                long e = mapper.value(array[i]);
                pMask = pMask | e;
                iMask = iMask | ~e;
            }
            i = startBatch;
        }
        return new MaskInfoLong(pMask, iMask);
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

    public static long getMaskLastBits(final int[] bList, final int bListStart) {
        long mask = 0;
        for (int i = bListStart; i < bList.length; i++) {
            int bIndex = bList[i];
            mask = mask | 1L << bIndex;
        }
        return mask;
    }

    public static long getMaskRangeBits(final int bStart, final int bEnd) {
        return ((1L << bStart + 1 - bEnd) - 1L) << bEnd;
    }

    public long getMask() {
        return pMask & iMask;
    }

    public boolean isUpperBitMaskSet() {
        return (getMask() & 0x80000000_00000000L) != 0L;
    }
    
}
