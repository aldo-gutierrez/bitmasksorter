package com.aldogg;

public interface IntSorter {
    void sort(int[] list);
    default String name() {
        return this.getClass().getSimpleName();
    }

}
