# Mask Bit Sorters
This project tests different ideas for sorting algorithms using a bitmask. 

This repository has the Java implementation.

To see other language implementations see:

[C# Version] (https://github.com/aldo-gutierrez/bitmasksorterCSharp)

[C++ Version] (https://github.com/aldo-gutierrez/bitmasksorterCpp)

[Javascript Version] (https://github.com/aldo-gutierrez/bitmasksorterJS)

[Python Version] (https://github.com/aldo-gutierrez/bitmasksorterPython)

I use a bitmask as a way to get statistical information about the numbers to be sorted.

For example suppose the list contains numbers from 0 to 127 then the bitmask is 1111111.

|                           | Number |             Bits | 
|---------------------------|-------:|-----------------:|
| Plausible Min:            |      0 | 0000000000000000 |
| Plausible Max:            |    127 | 0000000001111111 |
| Plausible Average/Median: |     64 | 0000000001000000 |
| Bits Used (K):            |      7 | 0000000001111111 |

For this case we can do a Count Sort with a Count array of 128 values:  0 to 127.

If the list contains the numbers 85, 84, 21 and 5 then the mask is 1010001, 
only the bits that change are part of the mask, in this case only 3 bits

|        Number |             Bits |     Changing Bits |
|--------------:|-----------------:|------------------:|
|            85 | 0000000001010101 |               111 | 
|            84 | 0000000001010100 |               110 |
|            21 | 0000000000010101 |               011 |
|             5 | 0000000000000101 |               001 |
|          MASK | 0000000001010001 |               111 |
| CONSTANT MASK | 0000000000000100 |                   |

|                           | Number |             Bits | 
|---------------------------|-------:|-----------------:|
| Plausible Min:            |      4 | 0000000000000100 |
| Plausible Max:            |     85 | 0000000001010101 |
| Plausible Average/Median: |     64 | 0000000001000000 |
| Bits Used (K):            |      3 | 0000000001010001 |

For this case I can do a Count Sort with a Count array of size 8, for values: 000 to 111.

First for each number we need to extract the bits that are part of the mask put the bits together, do the count
sort and reverse the procedure, to reverse the procedure I can OR it with the Constant Mask or have an auxiliary original number array.

BitMask Algorithm:
```
    public static int[] getMaskBit(int[] array, int start, int end) {
        int mask = 0x00000000;
        int inv_mask = 0x00000000;
        for (int i = start; i < end; i++) {
            int ei = array[i];
            mask = mask | ei;
            inv_mask = inv_mask | (~ei);
        }
        return new int[]{mask, inv_mask};
    }

    int mask = maskParts[0] & maskParts[1];
```

So in cases of hybrid algorithms this mask adds another dimension to the selection of the best algorithms. 
For example TimSort uses merge sort for big lists and insertion sort for small lists, so it depends  the size of the array (N)
I suppose that other hybrid algorithms use or could use the range to know if a Count Sort could be performed instead.

K is the number of bits in the bitmask. I am proposing to use N and (2 ^ K) as a way to decide what algorithm or optimization to use in a hybrid sorting algorithm.
(2 ^ K) could be better than the simple range. I think it could be faster as the bitmask calculation doesn't have an IF instruction in the for loop
So  we could have two dimensions N and 2^K to decide which algorithm to use, there could be a better algorithm for each combination.

Off course there are bad cases, so for example if a list contains only the values 1111111110000000(65408) and 0000000111111111(511). In this case the range is (64897)
, but if we use the bit mask 1111111001111111 then the k range (2^k) is also (65151). 
If we partition by the first bit in QuickBitSorter then half the numbers will go correctly to the first partition and half to the other partition,
and then in the next level of recursion after the partition all numbers will go to one partition, this is the case were a bitmask recalculation is needed.
The bitmask recalculation will produce 0000000000000000 that means everything is the same value and already sorted
RadixBitSorter doesn't consider this Bad case but QuickBitSorter does.

## QuickBitSorter
Is similar to QuickSort but for choosing the pivot I use the bit mask. To be precise the plausible Average/Median according to the bit mask. 
Having the bit mask helps when doing a Count Sort or Radix Sort for the last bits too.

This is different to other QuickSort algorithms that normally use the last element as pivot or choose the average of three
pivots.

This is similar to Binary MSD radix sort, but I think that the final bits count sort is different. And also Binary MSD radix sort uses all bits.
I need to do more reading and comparisons to understand the differences

For example if list contains all the numbers from 0 to 127  the bit mask is 1111111 and the first partition is done using the plausible average/median
that is zeroing all bits but the first in the bit mask:  1000000 (64). 

This could be a good or bad pivot depending on the distribution of the numbers. The bitmask doesn't provide information of the distribution

This algorithm when not using the Count Sort for the last X bits is slower than TimSort but faster than a Naive Stack Overflow QuickSort.
The reason I think is that in the worst case there could be 32 levels of recursion and Count Sort could reduce that to (32 - Number Bits for CountSort)
For example when using 16 bits for quicksort the max level of recursion in the worst case scenario is 32-16=16

Optimizations:
- The partition is only done by the bits in the bit mask as described above
- Optimization for small lists and or for the last bits, to choose between count sort and two different radix sort implementations
- Multithreading support

See an analysis of the performance in the section Speed and Performance


Usage:
```
//Sorting int numbers 
IntSorter sorter = new QuickBitSorterInt();
int[] list = ....
sorter.sort(list);
```
```
//Parallel sorting int numbers 
IntSorter sorter = new QuickBitSorterMTInt();
int[] list = ....
sorter.sort(list);
```
```
//Sorting unsinged int numbers 
IntSorter sorter = new QuickBitSorterInt();
sorter.setUnsigned(true);
int[] list = ....
sorter.sort(list);
```

## RadixBitSorter:

Is similar to the traditional Radix Sorter but instead of using a number in base 10  it uses a number in base 2 binary format.
Radix Bit Sorter also uses an adhoc Count Sort. It doesn't need to sort by all bits  just by the bits that are in the bit mask.

RadixBitSorter is a lot faster than to the typical stackoverflow, baeldung, hackerearth implementations.
The reason is that sometimes they use base 10 implementations with modulo operation, or they use bytes but not the binary form with binary operations.

After hearing about Ska Sort using the ideas described in the forums I created the class RadixByteSorter which uses bytes instead of bits.
The main difference between RadixByteSorterInt and RadixBitSorter is that RadixByteSorter works on bytes and RadixBitSorter works on bits.

RadixByteSorterInt when working an all bytes should have the same performance than SkaSort. See the C++ Implementation for more details on SkaSort
RadixBitSorter is faster than RadixByteSorter and SkaSort when less than 31 bits are in the bitmask otherwise SkaSort and RadixByteSorter are faster

Radix Bit Sorter Optimizations:
- It does not need to sort by all the bits just the ones in the bitmask
- It sorts by 11 bits at a time as maximum and 1 as minimum (Instead of 8 as SkaSort and RadixByteSorterInt do)

Radix Byte Sorter Optimizations:
- An option to detect which bytes do not use has been added to the algorithm (calculateBitMaskOptimization) and enable by default.


Usage:
```
//Sorting unsigned int  numbers 
IntSorter sorter = RadixBitSorterInt();
sorter.setUnsigned(true);
int[] list = ....
sorter.sort(list);

//Sorting int numbers 
IntSorter sorter = RadixBitSorterInt();
int[] list = ....
sorter.sort(list);

```

## MixedBitSorter:
It is a multi thread sorter, it combines previous QuickBitSorter and RadixBitSorter

For example if the bitmask is 00000000111111111111111111111110  23 bits, and we have a 32 thread processor then:

- The first 5 bits are recursively processed by doing QuickBitSorter in multiple threads
- The next 2 bits are recursively processed by doing RadixBitSort in each thread
- The last 16 bits are done using RadixBitSort + CountSort in each thread

# Stability
int[] sort in java use a not stable algorithm, it doesn't make sense to have a stable algorithm for ints. 
Object[] sort in java is a stable algorithm, so ObjectSorter has the parameter setStable but that uses more memory off course.

# Speed
Most of the algorithms are faster than the Java default and Parallel sorters in most of the cases.

See the performance analysis at the end of this section:

### Example 1: 

Comparison for sorting 10 Million int elements with range from 0 to 10 Million in an AMD Ryzen 7 4800H processor, Java 11.0.13

| Algorithm             | AVG CPU time [ms] |
|-----------------------|------------------:|
| JavaIntSorter         |               832 |
| QuickBitSorterInt     |               358 |
| RadixBitSorterInt     |               112 |
| RadixByteSorterInt    |               155 |
| JavaParallelSorterInt |                93 |
| QuickBitSorterMTInt   |               104 |
| MixedBitSorterMTInt   |                97 |
| RadixBitSorterMTInt   |                84 |


![Graph2](test-results/old/plot-S10000000-Range0-10000000-random.png?raw=true "Graph2")

### Example 2:

Comparison for sorting 10 Million int elements with range from 0 to 100000 in an AMD Ryzen 7 4800H processor, Java 11.0.13

| Algorithm             | AVG CPU time  [ms] |
|-----------------------|-------------------:|
| JavaSorterInt         |                605 |
| QuickBitSorterInt     |                 53 |
| RadixBitSorterInt     |                103 |
| RadixByteSorterInt    |                151 |
| JavaParallelSorterInt |                 92 |
| QuickBitSorterMTInt   |                 51 |
| MixedBitSorterMTInt   |                 51 |
| RadixBitSorterMTInt   |                 60 |

![Graph2](test-results/old/plot-S10000000-Range0-100000-random.png?raw=true "Graph2")

### Example 3:

Comparison for sorting 40 Million int elements with range from 0 to 1000000000 in an AMD Ryzen 7 4800H processor, Java 11.0.13

| Algorithm             | AVG CPU time  [ms] |
|-----------------------|-------------------:|
| JavaSorterInt         |               3623 |
| QuickBitSorterInt     |               2849 |
| RadixBitSorterInt     |                701 |
| RadixByteSorterInt    |                703 |
| JavaParallelSorterInt |                406 |
| QuickBitSorterMTInt   |                578 |
| MixedBitSorterMTInt   |                571 |
| RadixBitSorterMTInt   |                372 |

![Graph2](test-results/old/plot-S40000000-Range0-1000000000-random.png?raw=true "Graph2")

### Table of 1st and 2nd algorithm by speed AMD Ryzen 7 4800H processor, Java 11.0.13, pluged in

| N / range  | 10                                    | 1,000                                   | 100,000                               | 10,000,000                             | 1,000,000,000                            |
|------------|---------------------------------------|-----------------------------------------|---------------------------------------|----------------------------------------|-------------------------------------------|
| 10,000     | RadixBitSorterInt RadixBitSorterMTInt | RadixBitSorterInt RadixBitSorterMTInt   | RadixBitSorterInt RadixBitSorterMTInt | RadixBitSorterInt RadixBitSorterMTInt  | RadixBitSorterInt RadixBitSorterMTInt     |
| 100,000    | RadixBitSorterMTInt MixedBitSorterMTInt | RadixBitSorterMTInt MixedBitSorterMTInt | RadixBitSorterInt QuickBitSorterInt   | RadixBitSorterInt RadixByteSorterInt   | RadixBitSorterInt RadixByteSorterInt      |
| 1,000,000  | RadixBitSorterMTInt MixedBitSorterMTInt | RadixBitSorterMTInt QuickBitSorterMTInt | QuickBitSorterInt QuickBitSorterMTInt | RadixBitSorterMTInt JavaParallelSorterInt | RadixBitSorterMTInt JavaParallelSorterInt |
| 10,000,000 | QuickBitSorterMTInt MixedBitSorterMTInt | QuickBitSorterInt QuickBitSorterMTInt | MixedBitSorterMTInt QuickBitSorterMTInt | RadixBitSorterMTInt JavaParallelSorterInt  | RadixBitSorterMTInt JavaParallelSorterInt |
| 40,000,000 | MixedBitSorterMTInt RadixBitSorterMTInt | QuickBitSorterInt RadixBitSorterMTInt | MixedBitSorterMTInt QuickBitSorterMTInt | RadixBitSorterMTInt MixedBitSorterMTInt | RadixBitSorterMTInt JavaParallelSorterInt |

### Table of 1st and 2nd algorithm by speed AMD Ryzen 7 4800H processor, Java 11.0.13, with battery


| N / range  | 10                                    | 1,000                                   | 100,000                               | 10,000,000                                | 1,000,000,000                            |
|------------|---------------------------------------|-----------------------------------------|---------------------------------------|-------------------------------------------|-------------------------------------------|
| 10,000     | RadixBitSorterInt RadixBitSorterMTInt | RadixBitSorterMTInt RadixBitSorterInt   | RadixBitSorterInt RadixBitSorterMTInt | RadixBitSorterInt RadixByteSorterInt     | RadixBitSorterInt RadixBitSorterMTInt     |
| 100,000    | RadixBitSorterMTInt QuickBitSorterInt | RadixBitSorterMTInt MixedBitSorterMTInt | RadixBitSorterInt QuickBitSorterInt   | RadixBitSorterInt RadixByteSorterInt      | RadixBitSorterInt RadixByteSorterInt      |
| 1,000,000  | MixedBitSorterMTInt QuickBitSorterMTInt | QuickBitSorterInt RadixBitSorterMTInt | QuickBitSorterInt QuickBitSorterMTInt | RadixBitSorterInt RadixBitSorterMTInt | RadixBitSorterMTInt JavaParallelSorterInt |
| 10,000,000 | MixedBitSorterMTInt QuickBitSorterMTInt | QuickBitSorterMTInt MixedBitSorterMTInt | QuickBitSorterMTInt QuickBitSorterInt | RadixBitSorterMTInt RadixBitSorterInt | RadixBitSorterMTInt JavaParallelSorterInt |
| 40,000,000 | QuickBitSorterMTInt QuickBitSorterInt | QuickBitSorterMTInt RadixBitSorterMTInt | MixedBitSorterMTInt QuickBitSorterMTInt | RadixBitSorterMTInt MixedBitSorterMTInt | RadixBitSorterMTInt JavaParallelSorterInt |


Object Sort using the Interface IntComparator

```
        public class Entity1 {
            int id;
            String name;
        }

        IntComparator<Entity1> comparator = new IntComparator<Entity1>() {
            @Override
            public int intValue(Entity1 o) {
                return o.getId();
            }

            @Override
            public int compare(Entity1 entity1, Entity1 t1) {
                return Integer.compare(entity1.getId(), t1.getId());
            }
        };

        Object[] list = ...;
        ObjectSorter sorter = new RadixBitSorterObjectInt();
        sorter.sort(list, comparator);
```

###Example 4: 

Comparison for sorting 10 Million objects with int key  with range from 0 to 10 Million in an AMD Ryzen 7 4800H processor, Java 11.0.13

| Algorithm                   | AVG CPU time [ms] |
|-----------------------------|------------------:|
| JavaSorterObject            |              6750 |
| JavaParallelSorterObjectInt |               946 |
| RadixBitSorterObjectInt     |               801 |

![Graph2](test-results/old/plot-S10000000-Range0-10000000-random-object.png?raw=true "Graph2")

###Example 5:

Comparison for sorting 10 Million elements with int key range from 0 to 100000 in an AMD Ryzen 7 4800H processor, Java 11.0.13

| Algorithm                   | AVG CPU time  [ms] |
|-----------------------------|-------------------:|
| JavaSorterObject            |               5008 |
| JavaParallelSorterObjectInt |                876 |
| RadixBitSorterObjectInt     |                742 |

![Graph2](test-results/old/plot-S10000000-Range0-100000-random-object.png?raw=true "Graph2")


##Analysis of Speed and Performance:

TODO: need to separate single threaded vs multithreaded benchmarks and analyze

# O(N) Complexity

TODO Needs to be evaluated in detail

- n = number of elements
- k = number of bits on mask, 2^k is the plausible range of numbers
- t = number of hardware threads
- c = number of bits for counting sort
- q = number of bits for quick sort


| Algorithm        | CPU worst     | CPU average         | CPU best       | MEM worst | MEM average | MEM best |
| ---------------- |:-------------:| -------------------:| --------------:| ---------:| -----------:|---------:|
| QuickBitSorter   | O(n * log(n)) | O(n * log(n))?      | O(n+k), k <= c | O(2^c)    | O(2^c)      |    1     |
| RadixBitSorter   | O(n * log(n)) | O(n * k), k<log2(n) | O(n), k = 1    | O(n)      | O(n)        |    1     |
| QuickBitSorterMT | O(n * log(n)) | O(n * log(n))?      | O(n+k), k <= c | O(t*2^c)  | O(2^c)      |    1     |
| MixedBitSorterMT | O(n * log(n)) | O(n * log(n))?      | O(n), k <= t   | O(n*t)    | O(n*t)      |  O(2^c)  |
| RadixBitSorterMT | O(n * log(n)) | O(n * k), k<log2(n) | O(n), k = 1    | O(n)      | O(n)        |    1     |

## Comparing to Ska Sort
See the repository (https://github.com/aldo-gutierrez/bitmasksorterCpp) where a comparison has been done.
In summary RadixBitSorterInt is faster than SkaSort when the numbers range is bellow 30 bits (the range of different bits).

RadixByteSorterInt with calculateBitMaskOptimization=false looks similar in performance than SkaSort

## TODO
- Add More Object sorters in addition to RadixBitSorter
- Add Long, Short, Byte sorters (Long is the priority)
- More Detailed evaluation on complexity
- More Testing
- Compare with [Wolf Sort] (https://github.com/scandum/wolfsort) 
- Merge partition with bitmask extraction or isSorted with partition and bitmask and compare speed
- Test different algorithms for stable partition with less memory and compare speed
- Find the number N where Parallelization  of bitmask is faster than serial
- Find the number N where Parallelization  of isOrdered is faster than serial
- Find the number N where Parallelization  of partition is faster than serial
- Tests with repeatable random seeds
- Have comparisons and documentation of speed in powers of two instead of powers of ten

## Algorithms Testing
|Algorithm|Positive random numbers|Negative random numbers| Unsigned numbers | Sorted         | Reverse sorted     |
|---------|-----------------------|-----------------------|------------------|----------------|--------------------|
|QuickBitSorterInt|ok|ok| ok               | ok | ok |
|RadixBitSorterInt|ok|ok| ok               | ok | ok |
|RadixByteSorterInt|ok|ok| ok               | ok | ok |
|QuickBitSorterMTInt|ok|ok| ok               | ok | ok |
|MixedBitSorterMTInt|ok|ok| ok               | ok | ok |
|RadixBitSorterMTInt|ok|ok| ok               | ok | ok |
|RadixBitSorterObject|ok|ok| not tested       | ok | ok |
