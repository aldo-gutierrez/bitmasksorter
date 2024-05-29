package com.aldogg.sorter.float_.object;

import com.aldogg.sorter.int_.object.IntMapper;
import com.aldogg.sorter.shared.FieldType;

public interface FloatToIntMapper<T> extends IntMapper<T> {

    float valueFloat(T o);

    default int value(T o) {
        return Float.floatToRawIntBits(valueFloat(o));
    }

    @Override
    default FieldType getFieldType() {
        return FieldType.IEEE764_FLOAT;
    }
}
