package com.aldogg.sorter.shared.int_mask;

import com.aldogg.parallel.ArrayParallelRunner;
import com.aldogg.parallel.ArrayRunnable;
import com.aldogg.sorter.Constants;
import com.aldogg.sorter.int_.object.IntMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MaskInfoInt {
    public static final int UPPER_BIT = 31;
    public int pMask;
    public int iMask;

    public MaskInfoInt() {
    }

    public MaskInfoInt(int pMask, int iMask) {
        this.pMask = pMask;
        this.iMask = iMask;
    }

    public static final int SIZE_FOR_PARALLEL_BIT_MASK = 6000000;

    public static MaskInfoInt calculateMask(final int[] array, final int start, final int endP1) {
        int pMask = 0x00000000;
        int iMask = 0x00000000;
        for (int i = start; i < endP1; i++) {
            int e = array[i];
            pMask = pMask | e;
            iMask = iMask | ~e;
        }
        return new MaskInfoInt(pMask, iMask);
    }

    public static MaskInfoInt calculateMask(final float[] array, final int start, final int endP1) {
        int pMask = 0x00000000;
        int iMask = 0x00000000;
        for (int i = start; i < endP1; i++) {
            int e = Float.floatToRawIntBits(array[i]);
            pMask = pMask | e;
            iMask = iMask | ~e;
        }
        return new MaskInfoInt(pMask, iMask);
    }

    public static<T> MaskInfoInt calculateMask(T[] array, final int start, final int endP1, IntMapper<T> mapper) {
        int pMask = 0x00000000;
        int iMask = 0x00000000;
        for (int i = start; i < endP1; i++) {
            int e = mapper.value(array[i]);
            pMask = pMask | e;
            iMask = iMask | ~e;
        }
        return new MaskInfoInt(pMask, iMask);
    }

    public static MaskInfoInt calculateMaskBreakIfUpperBit(final int[] array, final int start, final int endP1, AtomicBoolean stop) {
        int pMask = 0x00000000;
        int iMask = 0x00000000;
        for (int i = start; i < endP1; i += Constants.CALCULATE_MASK_BATCH_SIZE) {
            if (pMask < 0) {
                if (iMask < 0) {
                    if (stop != null) {
                        stop.set(true);
                    }
                    return new MaskInfoInt(pMask, iMask);
                }
            }
            if (stop != null) {
                if (stop.get()) {
                    return null;
                }
            }
            int startBatch = i;
            int j = Math.min(i + Constants.CALCULATE_MASK_BATCH_SIZE, endP1);
            for (; i < j; i++) {
                int e = array[i];
                pMask = pMask | e;
                iMask = iMask | ~e;
            }
            i = startBatch;
        }
        return new MaskInfoInt(pMask, iMask);
    }

    public static <T> MaskInfoInt calculateMaskBreakIfUpperBit(final T[] array, final int start, final int endP1, AtomicBoolean stop, IntMapper<T> mapper) {
        int pMask = 0x00000000;
        int iMask = 0x00000000;
        for (int i = start; i < endP1; i += Constants.CALCULATE_MASK_BATCH_SIZE) {
            if (pMask < 0) {
                if (iMask < 0) {
                    if (stop != null) {
                        stop.set(true);
                    }
                    return new MaskInfoInt(pMask, iMask);
                }
            }
            if (stop != null) {
                if (stop.get()) {
                    return null;
                }
            }
            int startBatch = i;
            int j = Math.min(i + Constants.CALCULATE_MASK_BATCH_SIZE, endP1);
            for (; i < j; i++) {
                int e = mapper.value(array[i]);
                pMask = pMask | e;
                iMask = iMask | ~e;
            }
            i = startBatch;
        }
        return new MaskInfoInt(pMask, iMask);
    }

    public static MaskInfoInt calculateMaskInParallel(final int[] array, final int start, final int endP1, final ArrayParallelRunner.APRParameters parameters) {
        return ArrayParallelRunner.runInParallel(array, start, endP1, parameters, new ArrayRunnable<MaskInfoInt>() {
            @Override
            public MaskInfoInt map(final Object list, final int start1, final int endP1, int index, final AtomicBoolean stop) {
                return calculateMask((int[]) list, start1, endP1);
            }

            @Override
            public MaskInfoInt reduce(final MaskInfoInt m1, final MaskInfoInt m2) {
                MaskInfoInt res = new MaskInfoInt();
                res.pMask = m1.pMask | m2.pMask;
                res.iMask = m1.iMask | m2.iMask;
                return res;
            }
        });
    }

    public static MaskInfoInt calculateMaskInParallelBreakIfUpperBit(final int[] array, final int start, final int endP1, final ArrayParallelRunner.APRParameters parameters) {
        return ArrayParallelRunner.runInParallel(array, start, endP1, parameters, new ArrayRunnable<MaskInfoInt>() {
            @Override
            public MaskInfoInt map(final Object list, final int start1, final int endP1, final int index, final AtomicBoolean stop) {
                return calculateMaskBreakIfUpperBit((int[]) list, start1, endP1, stop);
            }

            @Override
            public MaskInfoInt reduce(final MaskInfoInt m1, final MaskInfoInt m2) {
                if (m1 == null) {
                    return m2;
                }
                if (m2 == null) {
                    return m1;
                }
                MaskInfoInt res = new MaskInfoInt();
                res.pMask = m1.pMask | m2.pMask;
                res.iMask = m1.iMask | m2.iMask;
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

    public static int getMaskLastBits(final int[] bList, final int bListStart) {
        int mask = 0;
        for (int i = bListStart; i < bList.length; i++) {
            int bIndex = bList[i];
            mask = mask | 1 << bIndex;
        }
        return mask;
    }

    public static int getMaskRangeBits(final int bStart, final int bEnd) {
        return ((1 << bStart + 1 - bEnd) - 1) << bEnd;
    }

    public static int getMask(int[] bList, int start, int endP1) {
        int mask = 0;
        for (int i = start; i <= endP1; i++) {
            mask = mask | 1 << bList[i];
        }
        return mask;
    }

    public int getMask() {
        return pMask & iMask;
    }

    public boolean isUpperBitMaskSet() {
        return (getMask() & 0x80000000) != 0;
    }

    public static int getUpperBitMask() {
        return 0x80000000;
    }


}
