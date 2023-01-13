package com.aldogg.parallel;

public interface ArrayRunnableLong<R> {

    R map(final long[] array, final int start, final int end);

    R reduce(final R result, final R partialResult);
}

