package com.aldogg.sorter.test.performance;

import com.aldogg.sorter.FieldSortOptions;
import com.aldogg.sorter.generic.RadixBitSorterGenericInt;
import com.aldogg.sorter.int_.object.IntMapper;
import com.aldogg.sorter.int_.object.mt.RadixBitSorterMTObjectInt;
import com.aldogg.sorter.int_.object.st.RadixBitSorterObjectInt;
import com.aldogg.sorter.int_.st.RadixBitSorterInt;
import com.aldogg.sorter.shared.NullHandling;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IntListArrayPTest {

    public static final String JAVA = "Java";

    @Test
    /**
     * {Java=4081, RadixBitSorterGenericInt=1895, RadixBitSorterMTObjectInt=477, RadixBitSorterObjectInt=798, RadixBitSorterInt=229}
     */
    public void testPerformanceSortingIntegerArray() {
        Random random = new Random();
        Supplier<Integer> supplier = () -> random.nextInt();

        Integer[] arrayAux;
        Map<String, Long> totalElapsed = new HashMap<>();
        int numReps = 10;
        for (int i = 0; i < numReps; i++) {

            Integer[] arrayOrig = Stream.generate(supplier).skip(0).limit(10000000).toArray(Integer[]::new);
            Integer[] arraySorted;
            {
                arraySorted = arrayOrig.clone();
                long startTime = System.nanoTime();
                Arrays.sort(arraySorted);
                totalElapsed.merge(JAVA, System.nanoTime() - startTime, Long::sum);
            }

            {
                arrayAux = arrayOrig.clone();
                long startTime = System.nanoTime();
                new RadixBitSorterGenericInt<Integer>().sort(arrayAux, x -> x);
                totalElapsed.merge(RadixBitSorterGenericInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertArrayEquals(arraySorted, arrayAux);
            }
            {
                arrayAux = arrayOrig.clone();
                long startTime = System.nanoTime();
                new RadixBitSorterObjectInt<Integer>().sort(arrayAux, x -> x);
                totalElapsed.merge(RadixBitSorterObjectInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertArrayEquals(arraySorted, arrayAux);
            }
            {
                arrayAux = arrayOrig.clone();
                long startTime = System.nanoTime();
                new RadixBitSorterInt().sort(arrayAux);
                totalElapsed.merge(RadixBitSorterInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertArrayEquals(arraySorted, arrayAux);
            }
            {
                arrayAux = arrayOrig.clone();
                long startTime = System.nanoTime();
                new RadixBitSorterMTObjectInt<Integer>().sort(arrayAux, x -> x);
                totalElapsed.merge(RadixBitSorterMTObjectInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertArrayEquals(arraySorted, arrayAux);
            }
        }
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / numReps));
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / 1000000));
        System.out.println(totalElapsed);
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterGenericInt.class.getSimpleName()));
        Assertions.assertTrue(totalElapsed.get(RadixBitSorterGenericInt.class.getSimpleName()) > totalElapsed.get(RadixBitSorterObjectInt.class.getSimpleName()));
        Assertions.assertTrue(totalElapsed.get(RadixBitSorterObjectInt.class.getSimpleName()) > totalElapsed.get(RadixBitSorterInt.class.getSimpleName()));
    }

    @Test
    /**
     * rep=10, random.nextInt(), default
     * {Java=4039, RadixBitSorterGenericInt=1560, RadixBitSorterMTObjectInt=1593, RadixBitSorterObjectInt=497, RadixBitSorterInt=443}
     *
     * rep=10, IntMapper.isStable=false (Strange Results)`
     * {Java=3888, RadixBitSorterGenericInt=1437, RadixBitSorterMTObjectInt=1104, RadixBitSorterObjectInt=651, RadixBitSorterInt=320}
     *
     * rep=10, random.nexInt(100), IntMapper.isStable=true (Strange Results)`
     * {Java=871, RadixBitSorterGenericInt=122, RadixBitSorterMTObjectInt=1124, RadixBitSorterObjectInt=535, RadixBitSorterInt=100}
     *
     * rep=10, random.nexInt(100), IntMapper.isStable=false (Strange Results)`
     * {Java=872, RadixBitSorterGenericInt=125, RadixBitSorterMTObjectInt=1015, RadixBitSorterObjectInt=588, RadixBitSorterInt=105}
     */
    public void testPerformanceSortingIntegerList() {
        Random random = new Random();
        Supplier<Integer> supplier = () -> random.nextInt();

        Map<String, Long> totalElapsed = new HashMap<>();
        int numReps = 10;
        for (int i = 0; i < numReps; i++) {

            List<Integer> arrayOrig = Stream.generate(supplier).skip(0).limit(10000000).collect(Collectors.toList());
            List<Integer> arraySorted = new ArrayList<>(arrayOrig);
            {
                long startTime = System.nanoTime();
                Collections.sort(arraySorted);
                totalElapsed.merge(JAVA, System.nanoTime() - startTime, Long::sum);
            }

            {
                List<Integer> arrayAux = new ArrayList<>(arrayOrig);
                long startTime = System.nanoTime();
                new RadixBitSorterGenericInt<Integer>().sort(arrayAux, x -> x);
                totalElapsed.merge(RadixBitSorterGenericInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertIterableEquals(arraySorted, arrayAux);
            }
            {
                List<Integer> arrayAux = new ArrayList<>(arrayOrig);
                long startTime = System.nanoTime();
                new RadixBitSorterObjectInt<Integer>().sort(arrayAux, x -> x);
                totalElapsed.merge(RadixBitSorterObjectInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertIterableEquals(arraySorted, arrayAux);
            }
            {
                List<Integer> arrayAux = new ArrayList<>(arrayOrig);
                long startTime = System.nanoTime();
                new RadixBitSorterInt().sort(arrayAux);
                totalElapsed.merge(RadixBitSorterInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertIterableEquals(arraySorted, arrayAux);
            }
            {
                List<Integer> arrayAux = new LinkedList<>(arrayOrig);
                long startTime = System.nanoTime();
                new RadixBitSorterMTObjectInt<Integer>().sort(arrayAux, x -> x);
                totalElapsed.merge(RadixBitSorterMTObjectInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
            }
        }
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / numReps));
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / 1000000));
        System.out.println(totalElapsed);
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterGenericInt.class.getSimpleName()));
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterObjectInt.class.getSimpleName()));
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterInt.class.getSimpleName()));
    }

    @Test
    /**
     * rep=10, random.nextInt(), default
     * {Java=4074, RadixBitSorterGenericInt=1659, RadixBitSorterMTObjectInt=1732, RadixBitSorterObjectInt=644, RadixBitSorterInt=1062}
     *
     * rep=10, IntMapper.isStable=false (Strange Results)
     * {Java=3946, RadixBitSorterGenericInt=1866, RadixBitSorterMTObjectInt=1414, RadixBitSorterObjectInt=1330, RadixBitSorterInt=832}
     */
    public void testPerformanceSortingIntegerLinkedList() {
        Random random = new Random();
        Supplier<Integer> supplier = () -> random.nextInt();

        Map<String, Long> totalElapsed = new HashMap<>();
        int numReps = 10;
        for (int i = 0; i < numReps; i++) {

            List<Integer> arrayOrig = Stream.generate(supplier).limit(10000000).collect(Collectors.toList());
            List<Integer> arraySorted = new LinkedList<>(arrayOrig);
            {
                long startTime = System.nanoTime();
                Collections.sort(arraySorted);
                totalElapsed.merge(JAVA, System.nanoTime() - startTime, Long::sum);
            }

            {
                List<Integer> arrayAux = new LinkedList<>(arrayOrig);
                long startTime = System.nanoTime();
                new RadixBitSorterGenericInt<Integer>().sort(arrayAux, x -> x);
                totalElapsed.merge(RadixBitSorterGenericInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertIterableEquals(arraySorted, arrayAux);
            }
            {
                List<Integer> arrayAux = new LinkedList<>(arrayOrig);
                long startTime = System.nanoTime();
                new RadixBitSorterObjectInt<Integer>().sort(arrayAux, x -> x);
                totalElapsed.merge(RadixBitSorterObjectInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertIterableEquals(arraySorted, arrayAux);
            }
            {
                List<Integer> arrayAux = new LinkedList<>(arrayOrig);
                long startTime = System.nanoTime();
                new RadixBitSorterInt().sort(arrayAux);
                totalElapsed.merge(RadixBitSorterInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertIterableEquals(arraySorted, arrayAux);
            }
            {
                List<Integer> arrayAux = new LinkedList<>(arrayOrig);
                long startTime = System.nanoTime();
                new RadixBitSorterMTObjectInt<Integer>().sort(arrayAux, x -> x);
                totalElapsed.merge(RadixBitSorterMTObjectInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertIterableEquals(arraySorted, arrayAux);
            }
        }
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / numReps));
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / 1000000));
        System.out.println(totalElapsed);
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterGenericInt.class.getSimpleName()));
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterObjectInt.class.getSimpleName()));
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterInt.class.getSimpleName()));
    }

    @Test
    /**
     * {Java=2457, RadixBitSorterGenericInt=1011, RadixBitSorterMTObjectInt=332, RadixBitSorterObjectInt=430, RadixBitSorterInt=216}
     */
    public void testPerformanceSortingIntegerArrayWithNull() {
        Random random = new Random();
        Supplier<Integer> supplier = () -> random.nextInt() % 2 == 0 ? random.nextInt() :  null;
        Comparator<Integer> nullsLast2 = (o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            return o1.compareTo(o2);
        };
        Comparator<Integer> nullsFirst2 = (o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            return o1.compareTo(o2);
        };
        Map<String, Long> totalElapsed = new HashMap<>();
        int numReps = 10;
        for (int i = 0; i < numReps; i++) {

            Integer[] arrayOrig = Stream.generate(supplier).skip(0).limit(10000000).toArray(Integer[]::new);
            Integer[] arraySorted1;
            Integer[] arraySorted2;
            {
                arraySorted1 = arrayOrig.clone();
                arraySorted2 = arrayOrig.clone();
                long startTime = System.nanoTime();
                Arrays.sort(arraySorted1, nullsFirst2);
                Arrays.sort(arraySorted2, nullsLast2);
                totalElapsed.merge(JAVA, System.nanoTime() - startTime, Long::sum);
            }

            IntMapper<Integer> mapper = o -> o;

            FieldSortOptions fieldSortOptionsNF = new FieldSortOptions();
            fieldSortOptionsNF.setNullHandling(NullHandling.NULLS_FIRST);

            FieldSortOptions fieldSortOptionsNL = new FieldSortOptions();
            fieldSortOptionsNL.setNullHandling(NullHandling.NULLS_LAST);


            {
                Integer[] arrayAux1 = arrayOrig.clone();
                Integer[] arrayAux2 = arrayOrig.clone();
                long startTime = System.nanoTime();
                new RadixBitSorterGenericInt<Integer>().sort(arrayAux1, mapper, 0, arrayAux1.length, fieldSortOptionsNF);
                new RadixBitSorterGenericInt<Integer>().sort(arrayAux2, mapper, 0 , arrayAux2.length, fieldSortOptionsNL);
                totalElapsed.merge(RadixBitSorterGenericInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertArrayEquals(arraySorted1, arrayAux1);
                Assertions.assertArrayEquals(arraySorted2, arrayAux2);
            }
            {
                Integer[] arrayAux1 = arrayOrig.clone();
                Integer[] arrayAux2 = arrayOrig.clone();
                long startTime = System.nanoTime();
                new RadixBitSorterObjectInt<Integer>().sort(arrayAux1, mapper, 0, arrayAux1.length, fieldSortOptionsNF);
                new RadixBitSorterObjectInt<Integer>().sort(arrayAux2, mapper, 0 , arrayAux2.length, fieldSortOptionsNL);
                totalElapsed.merge(RadixBitSorterObjectInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                try {
                    Assertions.assertArrayEquals(arraySorted1, arrayAux1);
                    Assertions.assertArrayEquals(arraySorted2, arrayAux2);
                } catch (Throwable ex) {
                    System.out.println(Arrays.toString(arrayOrig));
                    System.out.println(Arrays.toString(arraySorted1));
                    System.out.println(Arrays.toString(arrayAux1));
                    System.out.println(Arrays.toString(arraySorted2));
                    System.out.println(Arrays.toString(arrayAux2));
                    throw ex;
                }
            }
            {
                Integer[] arrayAux1 = arrayOrig.clone();
                Integer[] arrayAux2 = arrayOrig.clone();
                long startTime = System.nanoTime();
                new RadixBitSorterInt().sort(arrayAux1, 0, arrayAux1.length, fieldSortOptionsNF);
                new RadixBitSorterInt().sort(arrayAux2, 0, arrayAux2.length, fieldSortOptionsNL);
                totalElapsed.merge(RadixBitSorterInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertArrayEquals(arraySorted1, arrayAux1);
                Assertions.assertArrayEquals(arraySorted2, arrayAux2);
            }

            {
                Integer[] arrayAux1 = arrayOrig.clone();
                Integer[] arrayAux2 = arrayOrig.clone();
                long startTime = System.nanoTime();
                new RadixBitSorterMTObjectInt().sort(arrayAux1, mapper, 0, arrayAux1.length, fieldSortOptionsNF);
                new RadixBitSorterMTObjectInt().sort(arrayAux2, mapper, 0, arrayAux1.length, fieldSortOptionsNL);
                totalElapsed.merge(RadixBitSorterMTObjectInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertArrayEquals(arraySorted1, arrayAux1);
                Assertions.assertArrayEquals(arraySorted2, arrayAux2);
            }

        }
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / numReps));
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / 1000000));
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / 2));
        System.out.println(totalElapsed);
//        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterGenericInt.class.getSimpleName()));
//        Assertions.assertTrue(totalElapsed.get(RadixBitSorterGenericInt.class.getSimpleName()) > totalElapsed.get(RadixBitSorterObjectInt.class.getSimpleName()));
//        Assertions.assertTrue(totalElapsed.get(RadixBitSorterObjectInt.class.getSimpleName()) > totalElapsed.get(RadixBitSorterInt.class.getSimpleName()));
    }

}
