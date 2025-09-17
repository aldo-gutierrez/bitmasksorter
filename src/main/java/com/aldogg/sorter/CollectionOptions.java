package com.aldogg.sorter;

import com.aldogg.sorter.shared.NullHandling;

import static com.aldogg.sorter.shared.NullHandling.UNKNOWN;

public class CollectionOptions {

    private NullHandling nullHandling = UNKNOWN;

    /**
     * if true, all sortBy(field) will be stable
     * if false, all sortBy(field) will be unstable
     * if null, sortBy(field) will be stable, according to field options
     */
    private Boolean stable = null;

    private MemoryBalance memoryBalance = MemoryBalance.BALANCED_MEMORY_CPU;

    public NullHandling getNullHandling() {
        return nullHandling;
    }

    public void setNullHandling(NullHandling nullHandling) {
        this.nullHandling = nullHandling;
    }

    public Boolean getStable() {
        return stable;
    }

    public void setStable(Boolean stable) {
        this.stable = stable;
    }

    public MemoryBalance getMemoryBalance() {
        return memoryBalance;
    }

    public void setMemoryBalance(MemoryBalance memoryBalance) {
        this.memoryBalance = memoryBalance;
    }
}
