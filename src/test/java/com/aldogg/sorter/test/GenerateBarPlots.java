package com.aldogg.sorter.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import com.aldogg.sorter.*;
import com.aldogg.sorter.collection.*;
import com.aldogg.sorter.intType.CountSort;
import com.aldogg.sorter.intType.IntSorter;
import com.aldogg.sorter.intType.IntSorterUtils;
import com.aldogg.sorter.intType.Sorter;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.VerticalBarPlot;

import static com.aldogg.sorter.BitSorterUtils.*;
import static com.aldogg.sorter.RadixBitSorterInt.radixSort;
import static com.aldogg.sorter.intType.IntSorterUtils2.partitionStableOneThirdMemory;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** */
public class GenerateBarPlots {

    public static void main(String[] args) throws IOException {

        Table speeds = Table.read().csv("C:\\Users\\aldo\\IdeaProjects\\bitsorter\\speed_object.csv");
        int size;
        int range;

        size = 10000000;
        range = 10000000;
        Plot.show(VerticalBarPlot.create("Size: "+ size +", Range 0:"+ range, speeds.where(
                speeds
                        .intColumn("Size")
                        .isEqualTo(size)
                        .and(speeds.stringColumn("Range").isEqualTo("0:"+range))),
                "Sorter",
                "Time"));

        size = 10000000;
        range = 100000;
        Plot.show(VerticalBarPlot.create("Size: "+ size +", Range 0:"+ range, speeds.where(
                speeds
                        .intColumn("Size")
                        .isEqualTo(size)
                        .and(speeds.stringColumn("Range").isEqualTo("0:"+range))),
                "Sorter",
                "Time"));

        size = 40000000;
        range = 1000000000;
        Plot.show(VerticalBarPlot.create("Size: "+ size +", Range 0:"+ range, speeds.where(
                speeds
                        .intColumn("Size")
                        .isEqualTo(size)
                        .and(speeds.stringColumn("Range").isEqualTo("0:"+range))),
                "Sorter",
                "Time"));

    }

    public static class JavaParallelSorterInt implements IntSorter {
        @Override
        public void sort(int[] list) {
            Arrays.parallelSort(list);
        }
        @Override
        public void setUnsigned(boolean unsigned) {
            throw new UnsupportedOperationException();
        }
    }

    public static class JavaSorterInt implements IntSorter {
        @Override
        public void sort(int[] list) {
            Arrays.sort(list);
        }
        @Override
        public void setUnsigned(boolean unsigned) {
            throw new UnsupportedOperationException();
        }
    }

    public static class MaskTest {
        @Test
        public void maskTest() {
            int[] kList = new int[] {4,2,1,0};
            int[][] parts = getMaskAsSections(kList);
            int key = getKeySN(20, parts);
            assertEquals(key, 12);
        }
    }

    public static class PartitionTest {
        @Test
        public void testPartitionStable() throws IOException {

            int[] ori2 = new int[]{2, 0, 0, 0, 1, 1, 1};
            //int[] ori2 = new int[]{2, 1, 1, 1, 1, 1, 0};
            int[] cache2 = new int[100];
            partitionStableOneThirdMemory(ori2, 1, 7, 1, cache2);

            ori2 = new int[]{750179019, 583257215, 241131594, 21078738, 61715617, 719529520, 541243472, 765306073, 584696886, 226416842, 38699698, 3877878, 80397738};

            new RadixBitSorterInt().sort(ori2);


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
                                                int[] cache = new int[2];
                                                try {

                                                    int index = partitionStableOneThirdMemory(aux2, 2, 7, 1, cache);
                                                    boolean equal = Arrays.equals(aux, aux2);
                                                    if (!equal) {
                                                        throw new RuntimeException("error");
                                                    }
                                                    String strOfInts = Arrays.toString(aux).replaceAll("\\[|\\]|,|\\s", "");
                                                    int indexOK = strOfInts.indexOf('1');
                                                    if (indexOK == -1) {
                                                        indexOK = ori.length;
                                                    }
                                                    if (index != indexOK) {
                                                        throw new RuntimeException("differentIndex");
                                                    }
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                    System.out.println("asbb");
                                                }
                                            } else {
                                                int[] ori = new int[]{2, z1, z2, z3, z4, z5};
                                                aux = new int[]{2, z1, z2, z3, z4, z5};
                                                aux2 = new int[]{2, z1, z2, z3, z4, z5};
                                                Arrays.sort(aux, 1, 6);
                                                int[] cache = new int[2];
                                                try {
                                                    int index = partitionStableOneThirdMemory(aux2, 1, 6, 1, cache);
                                                    boolean equal = Arrays.equals(aux, aux2);
                                                    if (!equal) {
                                                        throw new RuntimeException("error");
                                                    }
                                                    String strOfInts = Arrays.toString(aux).replaceAll("\\[|\\]|,|\\s", "");
                                                    int indexOK = strOfInts.indexOf('1');
                                                    if (indexOK == -1) {
                                                        indexOK = ori.length;
                                                    }
                                                    if (index != indexOK) {
                                                        throw new RuntimeException("differentIndex");
                                                    }

                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                    System.out.println("asbb");
                                                }
                                            }
                                        } else {
                                            if (iAux == 2) {
                                                int[] ori = new int[]{2, 2, z1, z2, z3, z4, z5, z6};
                                                aux = new int[]{2, 2, z1, z2, z3, z4, z5, z6};
                                                aux2 = new int[]{2, 2, z1, z2, z3, z4, z5, z6};
                                                Arrays.sort(aux, 2, 8);
                                                int[] cache = new int[2];
                                                try {
                                                    int index = partitionStableOneThirdMemory(aux2, 2, 8, 1, cache);
                                                    boolean equal = Arrays.equals(aux, aux2);
                                                    if (!equal) {
                                                        throw new RuntimeException("error");
                                                    }
                                                    String strOfInts = Arrays.toString(aux).replaceAll("\\[|\\]|,|\\s", "");
                                                    int indexOK = strOfInts.indexOf('1');
                                                    if (indexOK == -1) {
                                                        indexOK = ori.length;
                                                    }
                                                    if (index != indexOK) {
                                                        throw new RuntimeException("differentIndex");
                                                    }
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                    System.out.println("asbb");
                                                }
                                            } else {
                                                int[] ori = new int[]{2, z1, z2, z3, z4, z5, z6};
                                                aux = new int[]{2, z1, z2, z3, z4, z5, z6};
                                                aux2 = new int[]{2, z1, z2, z3, z4, z5, z6};
                                                Arrays.sort(aux, 1, 7);
                                                int[] cache = new int[2];
                                                try {
                                                    int index = partitionStableOneThirdMemory(aux2, 1, 7, 1, cache);
                                                    boolean equal = Arrays.equals(aux, aux2);
                                                    if (!equal) {
                                                        throw new RuntimeException("error");
                                                    }
                                                    String strOfInts = Arrays.toString(aux).replaceAll("\\[|\\]|,|\\s", "");
                                                    int indexOK = strOfInts.indexOf('1');
                                                    if (indexOK == -1) {
                                                        indexOK = ori.length;
                                                    }
                                                    if (index != indexOK) {
                                                        throw new RuntimeException("differentIndex");
                                                    }
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                    System.out.println("asbb");
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

    public static class QuickSorter implements IntSorter {
        boolean unsigned = false;

        @Override
        public boolean isUnsigned() {
            return unsigned;
        }

        @Override
        public void setUnsigned(boolean unsigned) {
            this.unsigned = unsigned;
        }
        @Override
        public void sort(int[] list) {
            quickSort(list, 0, list.length - 1);
        }

        private void quickSort(int arr[], int begin, int end) {
            if (begin < end) {
                int partitionIndex = partition(arr, begin, end);

                quickSort(arr, begin, partitionIndex - 1);
                quickSort(arr, partitionIndex + 1, end);
            }
        }

        private int partition(int arr[], int begin, int end) {
            int pivot = arr[end];
            int i = (begin - 1);

            for (int j = begin; j < end; j++) {
                if (arr[j] <= pivot) {
                    i++;

                    int swapTemp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = swapTemp;
                }
            }

            int swapTemp = arr[i + 1];
            arr[i + 1] = arr[end];
            arr[end] = swapTemp;

            return i + 1;
        }

    }

    // Radix Sort in Java Programming
    public static class RadixSorter implements IntSorter {
        boolean unsigned = false;

        @Override
        public boolean isUnsigned() {
            return unsigned;
        }

        @Override
        public void setUnsigned(boolean unsigned) {
            this.unsigned = unsigned;
        }
        // Using counting sort to sort the elements in the basis of significant places
        void countingSort(int array[], int size, int place) {
            int[] output = new int[size + 1];
            int max = array[0];
            for (int i = 1; i < size; i++) {
                if (array[i] > max)
                    max = array[i];
            }
            int[] count = new int[max + 1];

            for (int i = 0; i < max; ++i)
                count[i] = 0;

            // Calculate count of elements
            for (int i = 0; i < size; i++)
                count[(array[i] / place) % 10]++;

            // Calculate cumulative count
            for (int i = 1; i < 10; i++)
                count[i] += count[i - 1];

            // Place the elements in sorted order
            for (int i = size - 1; i >= 0; i--) {
                output[count[(array[i] / place) % 10] - 1] = array[i];
                count[(array[i] / place) % 10]--;
            }

            for (int i = 0; i < size; i++)
                array[i] = output[i];
        }

        // Function to get the largest element from an array
        int getMax(int array[], int n) {
            int max = array[0];
            for (int i = 1; i < n; i++)
                if (array[i] > max)
                    max = array[i];
            return max;
        }

        // Main function to implement radix sort
        void radixSort(int array[], int size) {
            // Get maximum element
            int max = getMax(array, size);

            // Apply counting sort to sort elements based on place value.
            for (int place = 1; max / place > 0; place *= 10)
                countingSort(array, size, place);
        }


        @Override
        public void sort(int[] data) {
            int size = data.length;
            RadixSorter rs = new RadixSorter();
            rs.radixSort(data, size);
        }
    }

    public static class SorterTest {

        @Test
        public void basicTests() {
            IntSorter[] sorters = new IntSorter[] {new MixedBitSorterMTInt(), new QuickBitSorterInt(), new QuickBitSorterMTInt(), new RadixBitSorterInt(), new RadixBitSorterMTInt(), new RadixByteSorterInt()};
            TestSortResults sorterTests = new TestSortResults(sorters.length);
            testIntSort(new int[] {}, sorterTests, sorters);
            testIntSort(new int[] {1}, sorterTests, sorters);
            testIntSort(new int[] {2, 1}, sorterTests, sorters);
            testIntSort(new int[] {1, 2}, sorterTests, sorters);
            testIntSort(new int[] {1, 1}, sorterTests, sorters);
            testIntSort(new int[] {53,11,13}, sorterTests, sorters);
            testIntSort(new int[] {70,11,13,53}, sorterTests, sorters);
            testIntSort(new int[] {54,46,95,96,59,58,29,18,6,12,56,76,55,16,85,88,87,54,21,90,27,79,29,23,41,74}, sorterTests, sorters);
            testIntSort(new int[] {
                    70,11,13,53,54,46,95,96,59,58,29,18,6,12,56,76,55,16,85,88,
                    87,54,21,90,27,79,29,23,41,74,55,8,87,87,17,73,9,47,21,22,
                    77,53,67,24,11,24,47,38,26,42,14,91,36,19,12,35,79,91,71,81,
                    70,51,94,43,33,7,47,32,6,66,76,81,89,18,10,83,19,67,87,86,45,
                    31,70,13,16,40,31,55,81,75,71,16,31,27,17,5,36,29,63,60},sorterTests, sorters);
            //test bit mask 110110000 and 111110000
            testIntSort(new int[] {432,496,432,496,432,496,432,496,432,496,432,496,432,496,432,496,432,496,432,432,496,496,496,496,496,432}, sorterTests, sorters);
        }

        private void testIntSort(int[] list, TestSortResults testSortResults, IntSorter[] sorters) {
            int[] listAux2 = Arrays.copyOf(list, list.length);
            long startJava = System.nanoTime();
            Arrays.sort(listAux2);
            long elapsedJava = System.nanoTime() - startJava;

            for (int i = 0; i < sorters.length; i++) {
                IntSorter sorter = sorters[i];
                if (sorter instanceof JavaSorterInt) {
                    testSortResults.set(i, elapsedJava);
                } else {
                    long start = System.nanoTime();
                    int[] listAux = Arrays.copyOf(list, list.length);
                    sorter.sort(listAux);
                    long elapsed = System.nanoTime() - start;
                    try {
                        assertArrayEquals(listAux2, listAux);
                        testSortResults.set(i, elapsed);
                    } catch (Throwable ex) {
                        testSortResults.set(i, 0);
                        if (list.length <= 10000) {
                            System.err.println("Sorter "+ sorter.name());
                            String orig = Arrays.toString(list);
                            System.err.println("List orig: " + orig);
                            String failed = Arrays.toString(listAux);
                            System.err.println("List fail: " + failed);
                            String ok = Arrays.toString(listAux2);
                            System.err.println("List ok: " + ok);
                        } else {
                            System.err.println("Sorter "+ sorter.name());
                            System.err.println("List order is not OK ");
                        }
                        ex.printStackTrace();
                    }
                }
            }
        }



        private void testObjectSort(Object[] list, IntComparator comparator, TestSortResults testSortResults, ObjectSorter[] sorters) {
            Object[] listAux2 = Arrays.copyOf(list, list.length);
            long startJava = System.nanoTime();
            Arrays.sort(listAux2, comparator);
            long elapsedJava = System.nanoTime() - startJava;

            for (int i = 0; i < sorters.length; i++) {
                ObjectSorter sorter = sorters[i];
                if (sorter instanceof JavaSorterInt) {
                    testSortResults.set(i, elapsedJava);
                } else {
                    long start = System.nanoTime();
                    Object[] listAux = Arrays.copyOf(list, list.length);
                    sorter.sort(listAux, comparator);
                    long elapsed = System.nanoTime() - start;
                    try {
                        for (int j=0; j<listAux.length; j++) {
                            assertEquals(comparator.intValue(listAux[j]), comparator.intValue(listAux2[j]));
                        }
                        //for stable sort
                        //assertArrayEquals(listAux2, listAux);
                        testSortResults.set(i, elapsed);
                    } catch (Throwable ex) {
                        testSortResults.set(i, 0);
                        ex.printStackTrace();
                        String orig = Arrays.toString(list);
                        System.err.println("List orig: " + orig);
                        String failed = Arrays.toString(listAux);
                        System.err.println("List fail: " + failed);
                        String ok = Arrays.toString(listAux2);
                        System.err.println("List ok: " + ok);
                    }
                }
            }
        }



        @Test
        public void speedTestInt() throws IOException {
            BufferedWriter writer = new BufferedWriter(new FileWriter("speed.csv"));
            writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");


            IntSorter[] sorters = new IntSorter[] {new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new JavaParallelSorterInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt(), new RadixByteSorterInt()};
            TestSortResults testSortResults;

            //heatup
            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(10, 80000, 0, 80000, testSortResults, sorters, null);

            int iterations = 20;
            int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

            for (int limitH : limitHigh) {
                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 10000, 0, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 100000, 0, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 1000000, 0, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 10000000, 0, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 40000000, 0, limitH, testSortResults, sorters, writer);

                System.out.println("----------------------");
            }
            System.out.println();
            writer.close();
        }


        @Test
        public void speedTestObject() throws IOException {
            BufferedWriter writer = new BufferedWriter(new FileWriter("speed_object.csv"));
            writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");


            ObjectSorter[] sorters = new ObjectSorter[] {new JavaSorterObject(), new JavaParallelSorterObjectInt(), new RadixBitSorterObjectInt()};
            TestSortResults testSortResults;

            IntComparator comparator = new IntComparator() {
                @Override
                public int intValue(Object o) {
                    return ((Entity1) o).getId();
                }

                @Override
                public int compare(Object entity1, Object t1) {
                    return Integer.compare(((Entity1)entity1).getId(), ((Entity1) t1).getId());
                }
            };

            //heatup
            //testSortResults = new TestSortResults(sorters.length);
            //testSpeedObject(1000, 80000, 0, 80000, testSortResults, sorters, comparator, null);

            int iterations = 20;
            int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

            for (int limitH : limitHigh) {
                testSortResults = new TestSortResults(sorters.length);
                testSpeedObject(iterations, 10000, 0, limitH, testSortResults, sorters, comparator, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedObject(iterations, 100000, 0, limitH, testSortResults, sorters, comparator, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedObject(iterations, 1000000, 0, limitH, testSortResults, sorters, comparator, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedObject(iterations, 10000000, 0, limitH, testSortResults, sorters, comparator, writer);

                //testSortResults = new TestSortResults(sorters.length);
                //testSpeedObject(iterations, 40000000, 0, limitH, testSortResults, sorters, comparator, writer);

                System.out.println("----------------------");
            }
            System.out.println();
            writer.close();
        }


        @Test
        public void smallListAlgorithmSpeedTest() throws IOException {
            BufferedWriter writer = new BufferedWriter(new FileWriter("small.csv"));
            writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");


            IntSorter[] sorters = new IntSorter[] {new IntSorter() {
                @Override
                public void sort(int[] list) {
                    final int start = 0;
                    final int end = list.length;
                    int[] maskParts = getMaskBit(list, start, end);
                    int mask = maskParts[0] & maskParts[1];
                    int[] kList = getMaskAsList(mask);
                    int length = end - start;
                    int[] aux = new int[length];
                    radixSort(list, start, end, aux, kList, kList.length - 1, 0);
                }
                @Override
                public String name() {
                    return "StableByte";
                }

                @Override
                public void setUnsigned(boolean unsigned) {

                }
            },  new IntSorter() {
                @Override
                public void sort(int[] list) {
                    int[] maskParts = getMaskBit(list, 0, list.length);
                    int mask = maskParts[0] & maskParts[1];
                    int[] listK = getMaskAsList(mask);
                    int[] aux = new int[list.length];
                    for (int i = listK.length - 1; i >= 0; i--) {
                        int sortMask = BitSorterUtils.getMaskBit(listK[i]);
                        IntSorterUtils.partitionStable(list, 0, list.length, sortMask, aux);
                    }
                }
                @Override
                public String name() {
                    return "StableBit";
                }

                @Override
                public void setUnsigned(boolean unsigned) {

                }
            }, new IntSorter() {
                @Override
                public void sort(int[] list) {
                    int[] maskParts = getMaskBit(list, 0, list.length);
                    int mask = maskParts[0] & maskParts[1];
                    int[] listK = getMaskAsList(mask);
                    CountSort.countSort(list, 0, list.length, listK, 0);
                }

                @Override
                public String name() {
                    return "CountSort";
                }

                @Override
                public void setUnsigned(boolean unsigned) {

                }
            }};
            TestSortResults testSortResults;

            int iterations = 20;
                int[] limitHigh = new int[] {2, 4, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192,16384,32768,65536};

            for (int limitH : limitHigh) {
                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 16, 0, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 32, 0, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 64, 0, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 128, 0, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 256, 0, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 512, 0, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 1024, 0, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 2048, 0, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 4096, 0, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 8192, 0, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 16384, 0, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 32768, 0, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 65536, 0, limitH, testSortResults, sorters, writer);

                //System.out.println("----------------------");
            }
            System.out.println();
            writer.close();
        }


        @Test
        public void speedTestNegative() throws IOException {
            BufferedWriter writer = new BufferedWriter(new FileWriter("speed_negative.csv"));
            writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");
            IntSorter[] sorters = new IntSorter[] {new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new JavaParallelSorterInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt(), new RadixByteSorterInt()};


            TestSortResults testSortResults;

            //heatup
            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(1000, 80000, 0, 80000, testSortResults, sorters, null);

            int iterations = 200;
            int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

            for (int limitH : limitHigh) {
                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 10000, -limitH, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 100000, -limitH, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 1000000, -limitH, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 10000000, -limitH, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 40000000, -limitH, limitH, testSortResults, sorters, writer);

                System.out.println("----------------------");
            }
            System.out.println();
            writer.close();
        }


        @Test
        public void speedTestUnsigned() throws IOException {
            BufferedWriter writer = new BufferedWriter(new FileWriter("speed_negative.csv"));
            writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\""+  "," + "\"Time\""+"\n");
            IntSorter[] sorters = new IntSorter[] {new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new JavaParallelSorterInt(), new QuickBitSorterMTInt(), new MixedBitSorterMTInt(), new RadixBitSorterMTInt()};

            for (IntSorter sorter: sorters) {
                sorter.setUnsigned(true);
            }

            TestSortResults testSortResults;

            //heatup
            testSortResults = new TestSortResults(sorters.length);
            testSpeedInt(1000, 80000, 0, 80000, testSortResults, sorters, null);

            int iterations = 200;
            int[] limitHigh = new int[] {10, 1000, 100000, 10000000, 1000000000};

            for (int limitH : limitHigh) {
                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 10000, -limitH, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 100000, -limitH, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 1000000, -limitH, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 10000000, -limitH, limitH, testSortResults, sorters, writer);

                testSortResults = new TestSortResults(sorters.length);
                testSpeedInt(iterations, 40000000, -limitH, limitH, testSortResults, sorters, writer);

                System.out.println("----------------------");
            }
            System.out.println();
            writer.close();
        }

        private void testSpeedInt(int iterations, int size, int limitLow, int limitHigh, TestSortResults testSortResults, IntSorter[] sorters, Writer writer) throws IOException {
            Random random = new Random();
            int f = limitHigh - limitLow;
            for (int iter = 0; iter < iterations; iter++) {
                int[] list = new int[size];
                for (int i = 0; i < size; i++) {
                    int randomInt = random.nextInt(f) + limitLow;
                    list[i] = randomInt;
                }
                testIntSort(list, testSortResults, sorters);
            }
            printTestSpeed(size, limitLow, limitHigh, testSortResults, sorters, writer);
        }

        private void testSpeedObject(int iterations, int size, int limitLow, int limitHigh, TestSortResults testSortResults, ObjectSorter[] sorters, IntComparator comparator, Writer writer) throws IOException {
            Random random = new Random();
            int f = limitHigh - limitLow;
            for (int iter = 0; iter < iterations; iter++) {
                Entity1[] list = new Entity1[size];
                for (int i = 0; i < size; i++) {
                    int randomInt = random.nextInt(f) + limitLow;
                    list[i] = new Entity1(randomInt, randomInt+"");
                }
                testObjectSort(list, comparator, testSortResults, sorters);
            }
            printTestSpeed(size, limitLow, limitHigh, testSortResults, sorters, writer);
        }

        private void printTestSpeed(int size, int limitLow, int limitHigh, TestSortResults testSortResults, Sorter[] sorters, Writer writer) throws IOException {
            if (writer != null) {
               for (int i = 0; i < sorters.length; i++) {
                   Sorter sorter = sorters[i];
                   writer.write(size + ",\"" + limitLow + ":" + limitHigh + "\",\""+sorter.name()+"\"," + testSortResults.getAVG(i)/1000000 + "\n");
                   writer.flush();
               }
               System.out.printf("%12d, %12d, ", size,  limitHigh);
               for (int i = 0; i < sorters.length; i++) {
                   Sorter sorter = sorters[i];
                   System.out.printf("%20s, %12d, ", sorter.name(), testSortResults.getAVG(i));
               }
               String sorterWinner = "";
               long sorterWinnerTime = 0;
               for (int i = 0; i < sorters.length; i++) {
                   Sorter sorter = sorters[i];
                   if (i==0) {
                       sorterWinner = sorter.name();
                       sorterWinnerTime = testSortResults.getAVG(i);
                   } else {
                       if (testSortResults.getAVG(i) < sorterWinnerTime) {
                           sorterWinner = sorter.name();
                           sorterWinnerTime = testSortResults.getAVG(i);
                       }
                   }
               }
               System.out.print(sorterWinner  + ",\t");
               System.out.print(sorterWinnerTime  + ",\t");
               System.out.println();
           }
        }

        @Test
        public void testNegativeNumbers() {
            IntSorter[] sorters = new IntSorter[] {new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt()};
            TestSortResults testSorter = new TestSortResults(sorters.length);
            testIntSort(new int[] {}, testSorter, sorters);
            testIntSort(new int[] {1}, testSorter, sorters);
            testIntSort(new int[] {2, 1}, testSorter, sorters);
            testIntSort(new int[] {1, 2}, testSorter, sorters);
            testIntSort(new int[] {1, 1}, testSorter, sorters);
            testIntSort(new int[] {53,11,13}, testSorter, sorters);
            testIntSort(new int[] {70,11,13,53}, testSorter, sorters);
            testIntSort(new int[] {-70,-11,-13,-53}, testSorter, sorters);
            testIntSort(new int[] {-54,-46,-95,-96,-59,-58,-29,18,6,12,56,76,55,16,85,88,87,54,21,90,27,79,29,23,41,74}, testSorter, sorters);

        }

        @Test
        public void testBooleans() {
            IntSorter[] sorters = new IntSorter[]{new JavaSorterInt(), new QuickBitSorterInt()};
            TestSortResults sorter = new TestSortResults(sorters.length);
            testIntSort(new int[]{33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0, 33554431, 0}, sorter, sorters);
        }

        public static  void main (String[] args) {
            for (int j=0; j < 100000; j++) {
                List<Entity1> entity1sList = new ArrayList<>();
                Entity1[] entity1sArray = new Entity1[entity1sList.size()];
                entity1sList.toArray(entity1sArray);
                Random random = new Random();
                for (int i = 0; i < 10000000; i++) {
                    int randomInt = random.nextInt();
                    entity1sList.add(new Entity1(randomInt, randomInt + ""));
                }

                Entity1[] listAux1 = Arrays.copyOf(entity1sArray, entity1sArray.length);

                long startJava = System.nanoTime();
                Arrays.sort(listAux1, new Comparator<Entity1>() {
                    @Override
                    public int compare(Entity1 entity1, Entity1 t1) {
                        return Integer.compare(entity1.getId(), t1.getId());
                    }
                });
                long elapsedJava = System.nanoTime() - startJava;
                System.out.println("elapsed Java: " + elapsedJava);

                Entity1[] listAux2 = Arrays.copyOf(entity1sArray, entity1sArray.length);

                startJava = System.nanoTime();
                new RadixBitSorterObjectInt().sort(listAux2, new IntComparator() {
                    @Override
                    public int intValue(Object o) {
                        return ((Entity1) o).getId();
                    }
                });
                elapsedJava = System.nanoTime() - startJava;
                System.out.println("elapsed Radix: " + elapsedJava);
                boolean equals = Arrays.equals(listAux1, listAux2);
                System.out.println("equals: " + equals);
            }
        }
    }

    public static class TestSortResults {
        private final List<Long> totalElapsed;
        private final List<Integer> count;

        public TestSortResults(int sortersSize) {
            this.totalElapsed = new ArrayList<>(Collections.nCopies(sortersSize, 0L));
            this.count = new ArrayList<>(Collections.nCopies(sortersSize, 0));
        }


        public void set(int i, long elapsedTime) {
            totalElapsed.set(i, totalElapsed.get(i) + elapsedTime);
            count.set(i, count.get(i) + 1);
        }

        public long getAVG(int i) {
            return totalElapsed.get(i) / count.get(i);
        }

    }
}
