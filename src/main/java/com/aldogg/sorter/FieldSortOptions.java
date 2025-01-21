package com.aldogg.sorter;

import com.aldogg.sorter.shared.FieldType;
import com.aldogg.sorter.shared.NullHandling;

import static com.aldogg.sorter.shared.NullHandling.NO_HANDLING;

public final class FieldSortOptions {

    public enum ORDER { ASC, DESC}

    private ORDER order = ORDER.ASC;

    private NullHandling nullHandling = NO_HANDLING;

    private boolean stable = false;

    private FieldType fieldType = FieldType.SIGNED_INTEGER;

    public ORDER getOrder() {
        return order;
    }

    public void setOrder(ORDER order) {
        this.order = order;
    }

    public NullHandling getNullHandling() {
        return nullHandling;
    }

    public void setNullHandling(NullHandling nullHandling) {
        this.nullHandling = nullHandling;
    }

    public boolean isStable() {
        return stable;
    }

    public void setStable(boolean stable) {
        this.stable = stable;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }
}
