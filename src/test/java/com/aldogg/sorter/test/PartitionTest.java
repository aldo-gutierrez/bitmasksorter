package com.aldogg.sorter.test;

import com.aldogg.sorter.QuickBitSorterInt;
import com.aldogg.sorter.RadixBitSorterInt;
import com.aldogg.sorter.intType.IntSorterUtils2;
import com.aldogg.sorter.intType.IntSorterUtils3;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.aldogg.sorter.intType.IntSorterUtils2.partitionStableOneThirdMemory;


public class PartitionTest {

    private interface PartitionMethod {
        int partition(final int[] list, final int start, final int end, final int sortMask);
    }

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

        List<PartitionMethod> partitionMethods = new ArrayList<>();
        partitionMethods.add(IntSorterUtils3::partitionStableRecursive);
        partitionMethods.add(IntSorterUtils2::partitionStableOneThirdMemory);
        partitionMethods.add(IntSorterUtils2::partitionStableHalfMemory);

        for (PartitionMethod partitionMethod : partitionMethods) {
            for (int iAux = 1; iAux <= 2; iAux++) {
                for (int z1 = 0; z1 < 2; z1++) {
                    for (int z2 = 0; z2 < 2; z2++) {
                        for (int z3 = 0; z3 < 2; z3++) {
                            for (int z4 = 0; z4 < 2; z4++) {
                                for (int z5 = 0; z5 < 2; z5++) {
                                    for (int z6 = -1; z6 < 2; z6++) {
                                        int[] aux;
                                        int[] aux2;
                                        if (z6 == -1) {
                                            if (iAux == 2) {
                                                int[] ori = new int[]{2, 2, z1, z2, z3, z4, z5};
                                                aux = new int[]{2, 2, z1, z2, z3, z4, z5};
                                                aux2 = new int[]{2, 2, z1, z2, z3, z4, z5};
                                                Arrays.sort(aux, 2, 7);
                                                int index = partitionMethod.partition(aux2, 2, 7, 1);
                                                Assertions.assertArrayEquals(aux, aux2, "Error different results" + Arrays.toString(ori));
                                                String strOfInts = Arrays.toString(aux).replaceAll("\\[|\\]|,|\\s", "");
                                                int indexOK = strOfInts.indexOf('1');
                                                if (indexOK == -1) {
                                                    indexOK = ori.length;
                                                }
                                                Assertions.assertEquals(indexOK, index, "different indexes " + Arrays.toString(ori));
                                            } else {
                                                int[] ori = new int[]{2, z1, z2, z3, z4, z5};
                                                aux = new int[]{2, z1, z2, z3, z4, z5};
                                                aux2 = new int[]{2, z1, z2, z3, z4, z5};
                                                Arrays.sort(aux, 1, 6);
                                                int index = partitionMethod.partition(aux2, 1, 6, 1);
                                                Assertions.assertArrayEquals(aux, aux2, "Error different results" + Arrays.toString(ori));
                                                String strOfInts = Arrays.toString(aux).replaceAll("\\[|\\]|,|\\s", "");
                                                int indexOK = strOfInts.indexOf('1');
                                                if (indexOK == -1) {
                                                    indexOK = ori.length;
                                                }
                                                Assertions.assertEquals(indexOK, index, "different indexes " + Arrays.toString(ori));
                                            }
                                        } else {
                                            if (iAux == 2) {
                                                int[] ori = new int[]{2, 2, z1, z2, z3, z4, z5, z6};
                                                aux = new int[]{2, 2, z1, z2, z3, z4, z5, z6};
                                                aux2 = new int[]{2, 2, z1, z2, z3, z4, z5, z6};
                                                Arrays.sort(aux, 2, 8);
                                                int index = partitionMethod.partition(aux2, 2, 8, 1);
                                                Assertions.assertArrayEquals(aux, aux2, "Error different results" + Arrays.toString(ori));
                                                String strOfInts = Arrays.toString(aux).replaceAll("\\[|\\]|,|\\s", "");
                                                int indexOK = strOfInts.indexOf('1');
                                                if (indexOK == -1) {
                                                    indexOK = ori.length;
                                                }
                                                Assertions.assertEquals(indexOK, index, "different indexes " + Arrays.toString(ori));
                                            } else {
                                                int[] ori = new int[]{2, z1, z2, z3, z4, z5, z6};
                                                aux = new int[]{2, z1, z2, z3, z4, z5, z6};
                                                aux2 = new int[]{2, z1, z2, z3, z4, z5, z6};
                                                Arrays.sort(aux, 1, 7);
                                                int index = partitionMethod.partition(aux2, 1, 7, 1);
                                                Assertions.assertArrayEquals(aux, aux2, "Error different results" + Arrays.toString(ori));
                                                String strOfInts = Arrays.toString(aux).replaceAll("\\[|\\]|,|\\s", "");
                                                int indexOK = strOfInts.indexOf('1');
                                                if (indexOK == -1) {
                                                    indexOK = ori.length;
                                                }
                                                Assertions.assertEquals(indexOK, index, "different indexes " + Arrays.toString(ori));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
