package com.aldogg.sorter.shared;

import com.aldogg.sorter.shared.long_mask.MaskInfoLong;

public class Section {
    public final int start; //bit Index start; bStart; //section.start = section.shift + section.bits - 1;
    public final int bits; //length of bits
    public final int shift; //shiftRight; bEnd; //section.shift = section.start - section.bits + 1;
    private final long mask;

    public static Section createWithStarAndBits(int start, int bits) {
        int shift = start - bits + 1;
        Section section = new Section(bits, shift);
        return section;
    }

    public Section(int bits, int shift) {
        this.bits = bits;
        this.shift = shift;
        this.start = shift + bits - 1;
        mask = MaskInfoLong.getMaskRangeBits(start, shift);
    }

    public int getIntMask() {
        return (int) mask;
    }


    public long getLongMask() {
        return mask;
    }


}
