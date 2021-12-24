package com.aldogg.parallel;

public interface ArrayRunnable<R>  {

    R map(int[] list, int start, int end);

    R reduce(R result, R partialResult);
}
