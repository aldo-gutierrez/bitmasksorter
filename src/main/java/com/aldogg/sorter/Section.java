package com.aldogg.sorter;

public class Section {
    public int start; //bit Index start; bStart; //section.start = section.shift + section.bits - 1;
    public int bits; //length of bits
    public int shift; //shiftRight; bEnd; //section.shift = section.start - section.bits + 1;
    public Object mask;

    public Section(int bits, int shift) {
        this.bits = bits;
        this.shift = shift;
        this.start = shift + bits - 1;
    }

    public Section() {

    }

    public int getIntMask() {
        return (Integer) mask;
    }

    public void calculateIntMask() {
        mask = MaskInfoInt.getMaskRangeBits(start, shift);
    }

    public long getLongMask() {
        return (Long) mask;
    }

    public void calculateLongMask() {
        mask = MaskInfoLong.getMaskRangeBits(start, shift);
    }

}
