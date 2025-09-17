package com.aldogg.sorter.int_.object;

public interface IntegerMapper<T> extends  IntMapper<T> {

    default int value(T o) {
        return valueOf(o);
    }

    Integer valueOf(T t);
}
