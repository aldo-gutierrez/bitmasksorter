package com.aldogg.parallel;

public interface ArrayRunnableInt<R>  {

    R map(final int[] array, final int start, final int end);

    R reduce(final R result, final R partialResult);
}
