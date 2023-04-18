package com.aldogg.parallel;

import java.util.concurrent.atomic.AtomicBoolean;

public interface ArrayRunnable<R>  {

    R map(final Object array, final int start, final int endP1, final int index, final AtomicBoolean stop);

    R reduce(final R result, final R partialResult);
}
