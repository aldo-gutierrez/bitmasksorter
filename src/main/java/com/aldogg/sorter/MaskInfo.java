package com.aldogg.sorter;

import com.aldogg.parallel.ArrayRunnable;
import com.aldogg.parallel.ArrayParallelRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MaskInfo {
    int p_mask;
    int i_mask;
//    int p_mask_l;
//    int i_mask_l;
//    int p_mask_h;
//    int i_mask_h;
    int lc = 0;
    int hc = 0;

    public static MaskInfo getMaskBit(final int[] array, final int start, final int end) {
        int p_mask = 0x00000000;
        int i_mask = 0x00000000;
        for (int i = start; i < end; i++) {
            int e = array[i];
            p_mask = p_mask | e;
            i_mask = i_mask | (~e);
        }
        MaskInfo m= new MaskInfo();
        m.p_mask = p_mask;
        m.i_mask = i_mask;
        return m;
    }

    public static MaskInfo getMaskBitParallel(final int[] array, final int start, final int end, final int maxThreads, final AtomicInteger numThreads) {
        MaskInfo maskInfo = ArrayParallelRunner.runInParallel(array, start, end, maxThreads, numThreads, new ArrayRunnable<MaskInfo>() {
            @Override
            public MaskInfo map(final int[] list, final int start, final int end) {
                return getMaskBit(list, start, end);
            }

            @Override
            public MaskInfo reduce(final MaskInfo m1, final MaskInfo m2) {
                MaskInfo res = new MaskInfo();
                res.p_mask = m1.p_mask | m2.p_mask;
                res.i_mask = m1.i_mask | m2.i_mask;
                return res;
            }
        });
        return maskInfo;
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

    public static MaskInfo getMaskBitAndCount(final int[] array, final int start, final int end) {
        int p_mask = 0x00000000;
        int i_mask = 0x00000000;
//        int p_mask_l = 0x00000000;
//        int i_mask_l = 0x00000000;
//        int p_mask_h = 0x00000000;
//        int i_mask_h = 0x00000000;
        int lc = 0;
        int hc = 0;
        for (int i = start; i < end; ++i) {
            int e = array[i];
            int ie = ~e;
            p_mask = p_mask | e;
            i_mask = i_mask | ie;
            int ie31 = ie >>> 31;
            int e31 = e >>> 31;
//            p_mask_l = p_mask_l | (e * ie31);
//            i_mask_l = i_mask_l | (ie * e31);
            lc += ie31;
//            p_mask_h = p_mask_h | (e * e31);
//            i_mask_h = i_mask_h | (ie * ie31);
            hc += e31;
        }
        MaskInfo m= new MaskInfo();
        m.p_mask = p_mask;
        m.i_mask = i_mask;
//        m.p_mask_l = p_mask_l;
//        m.i_mask_l = i_mask_l;
//        m.p_mask_h = p_mask_h;
//        m.i_mask_h = i_mask_h;
        m.lc = lc;
        m.hc = hc;
        return m;
    }


}
