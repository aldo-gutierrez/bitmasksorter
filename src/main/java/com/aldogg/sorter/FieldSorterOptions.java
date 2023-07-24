package com.aldogg.sorter;

public interface FieldSorterOptions {

    default boolean isUnsigned() {
        return false;
    }

    default boolean isIeee754() {
        return false;
    }

    default boolean isStable() {
        return false;
    }

    default NullHandling getNullHandling() {
        return NullHandling.NULLS_EXCEPTION;
    }
}
