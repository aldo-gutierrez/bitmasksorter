package com.aldogg.sorter.test.performance;

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
     * {Java=40526, RadixBitSorterGenericInt=17064, RadixBitSorterObjectInt=7901, RadixBitSorterInt=2485}
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
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / 1000000));
        System.out.println(totalElapsed);
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterGenericInt.class.getSimpleName()));
        Assertions.assertTrue(totalElapsed.get(RadixBitSorterGenericInt.class.getSimpleName()) > totalElapsed.get(RadixBitSorterObjectInt.class.getSimpleName()));
        Assertions.assertTrue(totalElapsed.get(RadixBitSorterObjectInt.class.getSimpleName()) > totalElapsed.get(RadixBitSorterInt.class.getSimpleName()));
    }

    @Test
    /**
     * rep=10, random.nextInt(), default
     * {Java=37612, RadixBitSorterGenericInt=14317, RadixBitSorterObjectInt=5185, RadixBitSorterInt=2697}
     *
     * rep=10, IntMapper.isStable=false (Strange Results)`
     * {Java=39611, RadixBitSorterGenericInt=14613, RadixBitSorterObjectInt=5097, RadixBitSorterInt=9207}
     *
     * rep=10, random.nexInt(100), IntMapper.isStable=true (Strange Results)`
     * {Java=8532, RadixBitSorterGenericInt=1110, RadixBitSorterObjectInt=1593, RadixBitSorterInt=1135}
     *
     * rep=10, random.nexInt(100), IntMapper.isStable=false (Strange Results)`
     * {Java=8695, RadixBitSorterGenericInt=1099, RadixBitSorterObjectInt=1627, RadixBitSorterInt=1110}
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
                Assertions.assertIterableEquals(arraySorted, arrayAux);
            }
        }
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / 1000000));
        System.out.println(totalElapsed);
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterGenericInt.class.getSimpleName()));
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterObjectInt.class.getSimpleName()));
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterInt.class.getSimpleName()));
    }

    @Test
    /**
     * rep=10, random.nextInt(), default
     * {Java=38380, RadixBitSorterGenericInt=18258, RadixBitSorterObjectInt=21610, RadixBitSorterInt=7742}
     *
     * rep=10, IntMapper.isStable=false (Strange Results)
     * {Java=39786, RadixBitSorterGenericInt=18531, RadixBitSorterObjectInt=10041, RadixBitSorterInt=16221}
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
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / 1000000));
        System.out.println(totalElapsed);
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterGenericInt.class.getSimpleName()));
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterObjectInt.class.getSimpleName()));
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterInt.class.getSimpleName()));
    }

    @Test
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

            IntMapper<Integer> mapperNF = new IntMapper<Integer>() {
                @Override
                public int value(Integer o) {
                    return o;
                }

                @Override
                public NullHandling getNullHandling() {
                    return NullHandling.NULLS_FIRST;
                }
            };
            IntMapper<Integer> mapperNL = new IntMapper<Integer>() {
                @Override
                public int value(Integer o) {
                    return o;
                }

                @Override
                public NullHandling getNullHandling() {
                    return NullHandling.NULLS_LAST;
                }
            };

            {
                Integer[] arrayAux1 = arrayOrig.clone();
                Integer[] arrayAux2 = arrayOrig.clone();
                long startTime = System.nanoTime();
                new RadixBitSorterGenericInt<Integer>().sort(arrayAux1, mapperNF);
                new RadixBitSorterGenericInt<Integer>().sort(arrayAux2, mapperNL);
                totalElapsed.merge(RadixBitSorterGenericInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertArrayEquals(arraySorted1, arrayAux1);
                Assertions.assertArrayEquals(arraySorted2, arrayAux2);
            }
            {
                Integer[] arrayAux1 = arrayOrig.clone();
                Integer[] arrayAux2 = arrayOrig.clone();
                long startTime = System.nanoTime();
                new RadixBitSorterObjectInt<Integer>().sort(arrayAux1, mapperNF);
                new RadixBitSorterObjectInt<Integer>().sort(arrayAux2, mapperNL);
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
                new RadixBitSorterInt().sort(arrayAux1, 0, arrayAux1.length, mapperNF);
                new RadixBitSorterInt().sort(arrayAux2, 0, arrayAux1.length, mapperNL);
                totalElapsed.merge(RadixBitSorterInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertArrayEquals(arraySorted1, arrayAux1);
                Assertions.assertArrayEquals(arraySorted2, arrayAux2);
            }

            {
                Integer[] arrayAux1 = arrayOrig.clone();
                Integer[] arrayAux2 = arrayOrig.clone();
                long startTime = System.nanoTime();
                new RadixBitSorterMTObjectInt().sort(arrayAux1, 0, arrayAux1.length, mapperNF);
                new RadixBitSorterMTObjectInt().sort(arrayAux2, 0, arrayAux1.length, mapperNL);
                totalElapsed.merge(RadixBitSorterMTObjectInt.class.getSimpleName(), System.nanoTime() - startTime, Long::sum);
                Assertions.assertArrayEquals(arraySorted1, arrayAux1);
                Assertions.assertArrayEquals(arraySorted2, arrayAux2);
            }

        }
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / 1000000));
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / 2));
        System.out.println(totalElapsed);
//        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterGenericInt.class.getSimpleName()));
//        Assertions.assertTrue(totalElapsed.get(RadixBitSorterGenericInt.class.getSimpleName()) > totalElapsed.get(RadixBitSorterObjectInt.class.getSimpleName()));
//        Assertions.assertTrue(totalElapsed.get(RadixBitSorterObjectInt.class.getSimpleName()) > totalElapsed.get(RadixBitSorterInt.class.getSimpleName()));
    }

}
