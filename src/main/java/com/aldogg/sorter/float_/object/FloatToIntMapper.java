package com.aldogg.sorter.float_.object;

import com.aldogg.sorter.int_.object.IntMapper;
import com.aldogg.sorter.shared.FieldType;

public interface FloatToIntMapper<T>  {

    float valueFloat(T o);

    default int value(T o) {
        return Float.floatToRawIntBits(valueFloat(o));
    }

}
