package com.aldogg.sorter;

import com.aldogg.Order;
import com.aldogg.sorter.shared.FieldType;
import com.aldogg.sorter.shared.NullHandling;

import static com.aldogg.Order.ASC;
import static com.aldogg.sorter.shared.NullHandling.UNKNOWN;

public final class FieldSortOptions {

    private Order order = ASC;

    private NullHandling nullHandling = UNKNOWN;

    private boolean stable = false;

    private FieldType fieldType = FieldType.SIGNED_INTEGER;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
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
