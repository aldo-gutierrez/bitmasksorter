package com.aldogg.sorter.test;

import com.aldogg.sorter.intType.experimental.IntSorterUtils2;
import com.aldogg.sorter.intType.experimental.IntSorterUtils3;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartitionTest {

    private interface PartitionMethod {
        int partition(final int[] list, final int start, final int end, final int sortMask);
    }

    @Test
    public void testPartitionStable() throws IOException {
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
