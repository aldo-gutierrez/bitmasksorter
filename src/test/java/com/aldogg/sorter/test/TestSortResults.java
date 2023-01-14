package com.aldogg.sorter.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestSortResults {
    private final List<Long> totalElapsed;
    private final List<Integer> count;

    public TestSortResults(int sortersSize) {
        this.totalElapsed = new ArrayList<>(Collections.nCopies(sortersSize, 0L));
        this.count = new ArrayList<>(Collections.nCopies(sortersSize, 0));
    }


    public void set(int i, long elapsedTime) {
        totalElapsed.set(i, totalElapsed.get(i) + elapsedTime);
        count.set(i, count.get(i) + 1);
    }

    public long getAVG(int i) {
        return totalElapsed.get(i) / count.get(i);
    }

}
