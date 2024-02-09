package com.aldogg.sorter;

import com.aldogg.sorter.shared.NullHandling;

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
