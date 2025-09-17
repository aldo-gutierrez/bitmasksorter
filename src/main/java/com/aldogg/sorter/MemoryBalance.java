package com.aldogg.sorter;

public enum MemoryBalance {

    /**
     * O(1) additional memory
     *   One buffer int[] of size N for LinkedList as LinkedList don't have pointers to use any algorithm with pointers
     *   For list that support RandomAccess no aux arrays will be created an the algorithm will swap directly List elements
     *   A buffer of size MIN(256, LOG_2(N)*SQRT(N)) will be used automatically in some cases if it improves performance
     *   Most algorithms will not support this mode, some will support it only when stable = false
     * Some algorithms that support this:
     *   - QuickBitSort when stable=false with unstable partition
     *   - QuickBitSort when stable=true with stable low mem partition that could use up to 256(elements) cache
     *   - RadixBitBaseSort when stable=true with stable low mem partition that could use 256(elements) cache
     * If not supported it will throw an exception
     */
    MINIMAL_MEMORY,

    /**
     * O(X) additional memory
     *   One buffer primitive[] of size N for List<Object>
     *   An additional buffer primitive[] of MIN size LOG_2(N)*SQRT(N) will be used when it improves performance
     *   Most algorithms will not support this mode, some will support it only when stable = false
     * Some algorithms that support this:
     *   - QuickBitSort when stable=false with unstable partition
     *   - QuickBitSort when stable=true with stable low mem partition that could use the buffer cache
     *   - RadixBitBaseSort when stable=true with stable low mem partition that could use the buffer cache
     * If not supported it will throw an exception
     */
    FIXED_MEMORY,


    /**
     * O(N) additional memory
     *   One buffer primitive[] of size N for List<Object>
     *   An additional buffer primitive[] of size N or O(N) could be used
     *     Or Many additional buffers primitive[] adding to N could be used
     *     The best performant algorithm will be chosen but not adding any additional buffer
     *   Default mode, Most algorithms will support this mode
     * This option should never throw an exception as is the default mode
     */
    BALANCED_MEMORY_CPU,

    /**
     * O(N) additional memory
     *   One buffer primitive[] of size N for List<Object>
     *   Many additional buffer primitive[] of size N or O(N) or could be used
     *     for example if stable=false it could be decided to use stable partition with aux buffer if stable partition is always faster
     *     for example in multithreading new aux buffers could be used to use base 0 indexes for best code path
     * The best performant algorithm will be chosen regarding memory usage
     * This option should never throw an exception if the algorithm can't make any use of additional memory
     */
    UNLIMITED_MEMORY_CPU,


}
