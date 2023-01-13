package com.aldogg.sorter;

public class LongSection {
    public int k;
    public int length; //length
    public long sortMask; //sortMask
    public int shiftRight; //shiftRight;

    public boolean isSectionAtEnd() {
        return k + 1 == length;
    }
}
