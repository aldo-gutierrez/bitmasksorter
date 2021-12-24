package com.aldogg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestSortResults {
    private List<IntSorter> sorters;
    private List<Long> totalElapsed;
    private List<Integer> count;
    private List<Long> elapsed;
    private List<Boolean> success;

    public TestSortResults(IntSorter... _sorters) {
        this.sorters = Arrays.asList(_sorters);
        this.totalElapsed = new ArrayList<>(Collections.nCopies(sorters.size(), 0L));
        this.elapsed = new ArrayList<>(Collections.nCopies(sorters.size(), null));
        this.success = new ArrayList<>(Collections.nCopies(sorters.size(), null));
        this.count = new ArrayList<>(Collections.nCopies(sorters.size(), 0));
    }

    public List<IntSorter> getSorters() {
        return sorters;
    }

    public void set(int i, boolean successSort, long elapsedTime) {
        elapsed.set(i, elapsedTime);
        success.set(i, successSort);
        totalElapsed.set(i, totalElapsed.get(i) + elapsedTime);
        count.set(i, count.get(i) + 1);
    }

    public long getAVG(int i) {
        return totalElapsed.get(i) / count.get(i);
    }

}
