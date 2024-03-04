package com.aldogg.sorter.test.performance;

import com.aldogg.sorter.generic.RadixBitSorterGenericInt;
import com.aldogg.sorter.int_.object.st.RadixBitSorterObjectInt;
import com.aldogg.sorter.int_.st.RadixBitSorterInt;
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
        }
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / 1000000));
        System.out.println(totalElapsed);
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterGenericInt.class.getSimpleName()));
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterObjectInt.class.getSimpleName()));
        Assertions.assertTrue(totalElapsed.get(JAVA) > totalElapsed.get(RadixBitSorterInt.class.getSimpleName()));
    }

}
