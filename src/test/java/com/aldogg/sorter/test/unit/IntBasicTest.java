package com.aldogg.sorter.test.unit;

import com.aldogg.sorter.int_.IntSorter;
import com.aldogg.sorter.int_.mt.JavaSorterMTInt;
import com.aldogg.sorter.int_.mt.MixedBitSorterMTInt;
import com.aldogg.sorter.int_.mt.QuickBitSorterMTInt;
import com.aldogg.sorter.int_.mt.RadixBitSorterMTInt;
import com.aldogg.sorter.int_.st.JavaSorterInt;
import com.aldogg.sorter.int_.st.QuickBitSorterInt;
import com.aldogg.sorter.int_.st.RadixBitSorterInt;
import com.aldogg.sorter.int_.st.RadixByteSorterInt;
import com.aldogg.sorter.test.BaseTest;
import com.aldogg.sorter.test.TestAlgorithms;
import org.junit.jupiter.api.Test;

public class IntBasicTest extends BaseTest {
    @Test
    public void basicTests() {
        IntSorter[] sorters = new IntSorter[]{new JavaSorterInt(), new MixedBitSorterMTInt(), new QuickBitSorterInt(), new QuickBitSorterMTInt(), new RadixBitSorterInt(), new RadixBitSorterMTInt(), new RadixByteSorterInt()};
        TestAlgorithms sorterTests = new TestAlgorithms(sorters);
        testSort(new int[]{}, sorterTests);
        testSort(new int[]{1}, sorterTests);
        testSort(new int[]{2, 1}, sorterTests);
        testSort(new int[]{1, 2}, sorterTests);
        testSort(new int[]{1, 1}, sorterTests);
        testSort(new int[]{53, 11, 13}, sorterTests);
        testSort(new int[]{70, 11, 13, 53}, sorterTests);
        testSort(new int[]{54, 46, 95, 96, 59, 58, 29, 18, 6, 12, 56, 76, 55, 16, 85, 88, 87, 54, 21, 90, 27, 79, 29, 23, 41, 74}, sorterTests);
        testSort(new int[]{
                70, 11, 13, 53, 54, 46, 95, 96, 59, 58, 29, 18, 6, 12, 56, 76, 55, 16, 85, 88,
                87, 54, 21, 90, 27, 79, 29, 23, 41, 74, 55, 8, 87, 87, 17, 73, 9, 47, 21, 22,
                77, 53, 67, 24, 11, 24, 47, 38, 26, 42, 14, 91, 36, 19, 12, 35, 79, 91, 71, 81,
                70, 51, 94, 43, 33, 7, 47, 32, 6, 66, 76, 81, 89, 18, 10, 83, 19, 67, 87, 86, 45,
                31, 70, 13, 16, 40, 31, 55, 81, 75, 71, 16, 31, 27, 17, 5, 36, 29, 63, 60}, sorterTests);
        //test bit mask 110110000 and 111110000
        testSort(new int[]{432, 496, 432, 496, 432, 496, 432, 496, 432, 496, 432, 496, 432, 496, 432, 496, 432, 496, 432, 432, 496, 496, 496, 496, 496, 432}, sorterTests);
    }

    @Test
    public void testNegativeNumbers() {
        IntSorter[] sorters = new IntSorter[]{new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt(), new JavaSorterMTInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};
        TestAlgorithms testSorter = new TestAlgorithms(sorters);
        testSort(new int[]{}, testSorter);
        testSort(new int[]{1}, testSorter);
        testSort(new int[]{2, 1}, testSorter);
        testSort(new int[]{1, 2}, testSorter);
        testSort(new int[]{1, 1}, testSorter);
        testSort(new int[]{53, 11, 13}, testSorter);
        testSort(new int[]{70, 11, 13, 53}, testSorter);
        testSort(new int[]{-70, -11, -13, -53}, testSorter);
        testSort(new int[]{-54, -46, -95, -96, -59, -58, -29, 18, 6, 12, 56, 76, 55, 16, 85, 88, 87, 54, 21, 90, 27, 79, 29, 23, 41, 74}, testSorter);

    }

    @Test
    public void testBooleans() {
        IntSorter[] sorters = new IntSorter[]{new JavaSorterInt(), new QuickBitSorterInt()};
        TestAlgorithms sorter = new TestAlgorithms(sorters);
        testSort(new int[]{33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0}, sorter);
    }

}
