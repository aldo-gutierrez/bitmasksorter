package com.aldogg.sorter;

import com.aldogg.sorter.shared.FieldType;
import com.aldogg.sorter.shared.NullHandling;

public interface FieldOptions {

    default FieldType getFieldType() { return FieldType.SIGNED_INTEGER;}

    default boolean isStable() {
        return false;
    }

    default NullHandling getNullHandling() {
        return NullHandling.NULLS_EXCEPTION;
    }
}
