package com.aldogg.parallel;

public interface ArrayRunnable<R>  {

    R map(int[] array, int start, int end);

    R reduce(R result, R partialResult);
}
