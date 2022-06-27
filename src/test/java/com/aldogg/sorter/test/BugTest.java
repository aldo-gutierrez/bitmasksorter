package com.aldogg.sorter.test;

import com.aldogg.sorter.intType.st.QuickBitSorterInt;
import com.aldogg.sorter.intType.st.RadixBitSorterInt;
import com.aldogg.sorter.intType.mt.RadixBitSorterMTInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static com.aldogg.sorter.intType.experimental.IntSorterUtils2.partitionStableOneThirdMemory;

public class BugTest {
    @Test
    public void testPartitionStable() throws IOException {
        int[] ori2 = new int[]{2, 0, 0, 0, 1, 1, 1};
        int[] target = new int[]{2, 0, 0, 0, 1, 1, 1};
        partitionStableOneThirdMemory(ori2, 1, 7, 1);
        Assertions.assertArrayEquals(target, ori2);

        ori2 = new int[]{750179019, 583257215, 241131594, 21078738, 61715617, 719529520, 541243472, 765306073, 584696886, 226416842, 38699698, 3877878, 80397738};
        target = Arrays.copyOf(ori2, ori2.length);
        new RadixBitSorterInt().sort(ori2);
        Arrays.sort(target);
        Assertions.assertArrayEquals(target, ori2);

        ori2 = new int[]{9, 1, 3, 7, 1, 5, 8, 7, 8, 8};
        target = Arrays.copyOf(ori2, ori2.length);
        new QuickBitSorterInt().sort(ori2);
        Arrays.sort(target);
        Assertions.assertArrayEquals(target, ori2);

        ori2 = new int[]{-87374, 76388, 54395, -34873, -90640, -16844, 5568, 94798, -68537, 74167};
        target = Arrays.copyOf(ori2, ori2.length);
        new RadixBitSorterMTInt().sort(ori2);
        Arrays.sort(target);
        Assertions.assertArrayEquals(target, ori2);

    }
}