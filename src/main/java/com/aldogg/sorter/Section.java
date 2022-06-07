package com.aldogg.sorter;

public class Section {
    public int k;
    public int length; //length
    public int sortMask; //sortMask
    public int shiftRight; //shiftRight;

    public boolean isSectionAtEnd() {
        return k + 1 == length;
    }
}
