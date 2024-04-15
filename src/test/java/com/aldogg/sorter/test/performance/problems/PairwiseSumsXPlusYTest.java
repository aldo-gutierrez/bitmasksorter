package com.aldogg.sorter.test.performance.problems;

import com.aldogg.sorter.int_.object.IntMapper;
import com.aldogg.sorter.int_.object.st.RadixBitSorterObjectInt;
import com.aldogg.sorter.int_.st.RadixBitSorterInt;
import com.aldogg.sorter.shared.NullHandling;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * https://en.wikipedia.org/wiki/X_%2B_Y_sorting
 */
public class PairwiseSumsXPlusYTest {


    private class PW {

        List<Integer> arrayA = new ArrayList<>();
        int sum;

        public PW(int a, int b) {
            arrayA.add(a);
            this.sum = a + b;
        }

        public void add(int a) {
            arrayA.add(a);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PW pw = (PW) o;
            return sum == pw.sum;
        }

        @Override
        public int hashCode() {
            return Objects.hash(sum);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Integer a : arrayA) {
                sb.append("(" + a + ", " + (sum - a) + ") ");
            }
            return sb.toString();
        }
    }

    //n 100:10ms, n 1000:45ms, n10000:18313ms
    @Test
    public void testSolution1() {
        Random random = new Random();
        int n = 1000;
        Supplier<Integer> supplier = () -> random.nextInt(n);
        List<Integer> listA = Stream.generate(supplier).limit(n).collect(Collectors.toList());
        List<Integer> listB = Stream.generate(supplier).limit(n).collect(Collectors.toList());
        Map<String, Long> totalElapsed = new HashMap<>();
        long startTime = System.nanoTime();
        new RadixBitSorterInt().sort(listA);
        new RadixBitSorterInt().sort(listB);
        int low = listA.get(0) + listB.get(0);
        int high = listA.get(listA.size() - 1) + listB.get(listB.size() - 1);
        PW[] list = new PW[high - low + 1];

        for (int a : listA) {
            for (int b : listB) {
                int key = a + b - low;
                if (list[key] == null) {
                    PW pw = new PW(a, b);
                    list[key] = pw;
                } else {
                    list[key].add(a);
                }
            }
        }

        System.out.println(list.length);

        // max O((N^2)*D)  //4 digits for  32 bit integer
        new RadixBitSorterObjectInt<PW>().sort(list, new IntMapper<PW>() {
            @Override
            public int value(PW o) {
                return o.sum;
            }

            @Override
            public NullHandling getNullHandling() {
                return NullHandling.NULLS_LAST;
            }
        });
        //TOTAL O(N^2) if N is big and D is small otherwise is O(N^2*LogN) as D = (log K)/8 and K could be N
        totalElapsed.merge(this.getClass().getSimpleName(), System.nanoTime() - startTime, Long::sum);
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / 1000000));
        System.out.println(totalElapsed);
    }

    //n 100:12ms, n 1000:79ms, n10000:20002ms
    @Test
    public void testSolution2() {
        Random random = new Random();
        int n = 10000;
        Supplier<Integer> supplier = () -> random.nextInt(n);
        List<Integer> listA = Stream.generate(supplier).limit(n).collect(Collectors.toList());
        List<Integer> listB = Stream.generate(supplier).limit(n).collect(Collectors.toList());
        Map<String, Long> totalElapsed = new HashMap<>();
        long startTime = System.nanoTime();
        new RadixBitSorterInt().sort(listA);
        new RadixBitSorterInt().sort(listB);
        int low = listA.get(0) + listB.get(0);
        Map<Integer, PW> map = new HashMap<>();
        //N2
        for (int a : listA) {
            for (int b : listB) {
                int key = a + b - low;
                if (!map.containsKey(key)) {
                    PW pw = new PW(a, b);
                    map.put(key, pw);
                } else {
                    map.get(key).add(a);
                }
            }
        }
        // max N^2
        List<PW> list = new ArrayList<>(map.values());
        System.out.println(list.size());

        // max O((N^2)*D)  //4 digits for  32 bit integer
        new RadixBitSorterObjectInt<PW>().sort(list, o -> o.sum);
        //TOTAL O(N^2) if N is big and D is small otherwise is O(N^2*LogN) as D = (log K)/8 and K could be N
        totalElapsed.merge(this.getClass().getSimpleName(), System.nanoTime() - startTime, Long::sum);
        totalElapsed.forEach((key, value) -> totalElapsed.put(key, value / 1000000));
        System.out.println(totalElapsed);
    }

}
