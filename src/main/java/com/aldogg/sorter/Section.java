package com.aldogg.sorter;

public class Section {
    public int start; //bit Index start
    public int bits; //length of bits
    public int shift; //shiftRight;

    public boolean isSectionAtEnd() {
        return start + 1 == bits;
        //or shift == 0?
    }

    public int getKRange() {
        return 1 << bits;
    }
}
