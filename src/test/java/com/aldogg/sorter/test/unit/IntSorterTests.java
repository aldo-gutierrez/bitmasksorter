package com.aldogg.sorter.test.unit;

import com.aldogg.sorter.int_.SorterInt;
import com.aldogg.sorter.int_.st.*;
import org.junit.jupiter.api.Test;

/**
 * This tests don't test performance
 */
public class IntSorterTests {

    @Test
    public void testListPositive() {
        SorterInt[] sorters = new SorterInt[]{new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt(), new RadixByteSorterV2Int(), new RadixByteSorterV3Int()};

    }

    @Test
    public void testListNegative() {

    }

    @Test
    public void testListPositiveAndNegative() {

    }

    @Test
    public void testListUnsigned() {

    }

    @Test
    public void testListPositiveAndNegativeSpecialMasks() {

    }

    @Test
    public void testListMultithread() {

    }


}
