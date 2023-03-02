package com.aldogg.sorter;

import java.util.concurrent.atomic.AtomicInteger;

public class IntContext {
    int[] aux;

    int startAux;

    int maxThreads;

    AtomicInteger numThreads;

    public IntContext setStartAuxAndClone(int startAux) {
        IntContext intContext = new IntContext();
        intContext.startAux = startAux;
        intContext.aux = aux;
        intContext.maxThreads = maxThreads;
        intContext.numThreads = numThreads;
        return intContext;
    }
}
