package com.aldogg.sorter.test;

import com.aldogg.sorter.RadixBitSorterInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static com.aldogg.sorter.intType.IntSorterUtils2.partitionStableOneThirdMemory;

public class PartitionTest {
    @Test
    public void testPartitionStable() throws IOException {

        int[] ori2 = new int[]{2, 0, 0, 0, 1, 1, 1};
        int[] target = new int[]{2, 0, 0, 0, 1, 1, 1};
        int[] cache2 = new int[100];
        partitionStableOneThirdMemory(ori2, 1, 7, 1, cache2);
        Assertions.assertArrayEquals(target, ori2);

        ori2 = new int[]{750179019, 583257215, 241131594, 21078738, 61715617, 719529520, 541243472, 765306073, 584696886, 226416842, 38699698, 3877878, 80397738};
        target = Arrays.copyOf(ori2, ori2.length);
        new RadixBitSorterInt().sort(ori2); //todo change to partitionStable with correct order numbers
        Arrays.sort(target);
        Assertions.assertArrayEquals(target, ori2);

        for (int iAux = 1; iAux <= 2; iAux++) {
            for (int z1 = 0; z1 < 2; z1++) {
                for (int z2 = 0; z2 < 2; z2++) {
                    for (int z3 = 0; z3 < 2; z3++) {
                        for (int z4 = 0; z4 < 2; z4++) {
                            for (int z5 = 0; z5 < 2; z5++) {
                                for (int z6 = -1; z6 < 2; z6++) {
                                    int[] cache = new int[2];
                                    int[] aux;
                                    int[] aux2;
                                    if (z6 == -1) {
                                        if (iAux == 2) {
                                            int[] ori = new int[]{2, 2, z1, z2, z3, z4, z5};
                                            aux = new int[]{2, 2, z1, z2, z3, z4, z5};
                                            aux2 = new int[]{2, 2, z1, z2, z3, z4, z5};
                                            Arrays.sort(aux, 2, 7);
                                            int index = partitionStableOneThirdMemory(aux2, 2, 7, 1, cache);
                                            Assertions.assertArrayEquals(aux, aux2, "Error different results");
                                            String strOfInts = Arrays.toString(aux).replaceAll("\\[|\\]|,|\\s", "");
                                            int indexOK = strOfInts.indexOf('1');
                                            if (indexOK == -1) {
                                                indexOK = ori.length;
                                            }
                                            Assertions.assertEquals(indexOK, index, "different indexes " + index + " " + indexOK);
                                        } else {
                                            int[] ori = new int[]{2, z1, z2, z3, z4, z5};
                                            aux = new int[]{2, z1, z2, z3, z4, z5};
                                            aux2 = new int[]{2, z1, z2, z3, z4, z5};
                                            Arrays.sort(aux, 1, 6);
                                            int index = partitionStableOneThirdMemory(aux2, 1, 6, 1, cache);
                                            Assertions.assertArrayEquals(aux, aux2, "Error different results");
                                            String strOfInts = Arrays.toString(aux).replaceAll("\\[|\\]|,|\\s", "");
                                            int indexOK = strOfInts.indexOf('1');
                                            if (indexOK == -1) {
                                                indexOK = ori.length;
                                            }
                                            Assertions.assertEquals(indexOK, index, "different indexes " + index + " " + indexOK);
                                        }
                                    } else {
                                        if (iAux == 2) {
                                            int[] ori = new int[]{2, 2, z1, z2, z3, z4, z5, z6};
                                            aux = new int[]{2, 2, z1, z2, z3, z4, z5, z6};
                                            aux2 = new int[]{2, 2, z1, z2, z3, z4, z5, z6};
                                            Arrays.sort(aux, 2, 8);
                                            int index = partitionStableOneThirdMemory(aux2, 2, 8, 1, cache);
                                            Assertions.assertArrayEquals(aux, aux2, "Error different results");
                                            String strOfInts = Arrays.toString(aux).replaceAll("\\[|\\]|,|\\s", "");
                                            int indexOK = strOfInts.indexOf('1');
                                            if (indexOK == -1) {
                                                indexOK = ori.length;
                                            }
                                            Assertions.assertEquals(indexOK, index, "different indexes " + index + " " + indexOK);
                                        } else {
                                            int[] ori = new int[]{2, z1, z2, z3, z4, z5, z6};
                                            aux = new int[]{2, z1, z2, z3, z4, z5, z6};
                                            aux2 = new int[]{2, z1, z2, z3, z4, z5, z6};
                                            Arrays.sort(aux, 1, 7);
                                            int index = partitionStableOneThirdMemory(aux2, 1, 7, 1, cache);
                                            Assertions.assertArrayEquals(aux, aux2, "Error different results");
                                            String strOfInts = Arrays.toString(aux).replaceAll("\\[|\\]|,|\\s", "");
                                            int indexOK = strOfInts.indexOf('1');
                                            if (indexOK == -1) {
                                                indexOK = ori.length;
                                            }
                                            Assertions.assertEquals(indexOK, index, "different indexes " + index + " " + indexOK);
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
