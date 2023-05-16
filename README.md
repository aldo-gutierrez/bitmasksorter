# Bit Mask Sorters

This project tests different ideas for sorting algorithms using a Bitmask.

This repository has the Java implementation.

For implementations in other languages, see:

[C# Version] (https://github.com/aldo-gutierrez/bitmasksorterCSharp)

[C++ Version] (https://github.com/aldo-gutierrez/bitmasksorterCpp)

[Javascript Version] (https://github.com/aldo-gutierrez/bitmasksorterJS)

[Python Version] (https://github.com/aldo-gutierrez/bitmasksorterPython)

A Bitmask is used as a way to reduce the necessary calculations on the numbers to be sorted.

For example, suppose a list of integers contains numbers from 0 to 127, so the bitmask is 1111111.

|                 | Number |             Bits | 
|-----------------|-------:|-----------------:|
| Min:            |      0 | 0000000000000000 |
| Max:            |    127 | 0000000001111111 |
| Average/Median: |     64 | 0000000001000000 |
| Bits Used (b):  |      7 | 0000000001111111 |

For this example we could do a simple Count Sort with a Count array of 128 values:  0 to 127. 
Just one pass of Count Sort is necessary, it's not necessary to do 4 passes as in typical radix byte sorter.
A typical radix byte sorter for integers (32 bits) uses  8bits (words/digits) and needs 4 passes

If the list contains the numbers 85, 84, 21 and 5 then the mask is 1010001,
only the bits that change are part of the mask, in this case only 3 bits

|        Number |             Bits |
|--------------:|-----------------:|
|            85 | 0000000001010101 | 
|            84 | 0000000001010100 |
|            21 | 0000000000010101 |
|             5 | 0000000000000101 |
|          MASK | 0000000001010001 |

|                      | Number |             Bits | 
|----------------------|-------:|-----------------:|
| Mask Min:            |      4 | 0000000000000100 |
| Mask Max:            |     85 | 0000000001010101 |
| Mask Average/Median: |     64 | 0000000001000000 |
| Mask Bits Used (d):  |      3 | 0000000001010001 |

For this case you can do a Count Sort for only the 3 bits that are part of the mask, 2^3 = 8, with a modified Count Sort that counts 8 values from  000(0) to 111(7)

Variables defined in a radix sorter

r = range, for example for unsigned integers is 2^32
d = length of word or digit in bits, typical value is 8 bits (a byte)
k = number of words/digits, for an integer this is 4 (32/8)

For BitMask sorters 
m = length in bits of the bitmask, in integers from 1 to 32
r = range, that is calculated as 2^m or 2 ^(d1+d2..dn)
d = length of word or digit in bits, it can be from 1..16 bits depending on configuration and bitmask
k = number of word/digits, it could be 3 or 4 depending on detected cpu and configured RADIX_SORTER_MASK_BITS that returns 11 in modern processors

Algorithm to obtain the BitMask:

```
    public static int calculateBitMask(int[] array, int start, int endP1) {
        int mask = 0x00000000;
        int inv_mask = 0x00000000;
        for (int i = start; i < endP1; i++) {
            int ei = array[i];
            mask = mask | ei;
            inv_mask = inv_mask | ~ei;
        }
        return mask & inv_mask;
    }
```

The best case is that if all the numbers are equal, the bitmask is 0, so no further calculations are needed;

There are some bad cases to be aware of, and when the bitmask needs to be recalculated, for example:

Example 1: If a list contains only two values 1111111110000000(65408) and 0000000111111111(511). In this case the mask
is 1111111001111111. For this example QuickBitSort will partition by the first bit
(1000000000000000) and the numbers will be sorted, when doing the next recursive partition it detects that all numbers
go to one partition and then after recalculating the mask as all the numbers are the same the bitMask will be 0 and the sort is finished

Example 2: If a list contains positive and negative numbers then the mask will probably be -1 of 0xFFFFFFFF or 1111111111111111111111111111111.
Then after partitioning the numbers in two, positives to one side, negatives to the other is better to recalculate the mask for each part
This is done by all algorithms that use the BitMask QuickBitSort, RadixBitSort and others.


TimSort uses merge sort for big lists and insertion sort for small lists, so it depends on the size of the array (N).
TimSort is a hybrid algorithm.

We can use the bitmask to create a hybrid algorithm that takes into account not only N but also r (the range).
where r = 2^(k*d) or  r = 2^(d1 + d2 ....).

AGSelectorSorterInt is an example of that hybrid algorithm that choose between different algorithms (QuickBitSorter, RadixBitSorter and RadixByteSorter )

## QuickBitSorter

Is similar to QuickSort but for choosing the pivot we use the bit mask. The first (one) bit of the bitmask is used as pivot
In the next recursion the next bit of the bitmask is used.
This is different to other QuickSort algorithms that normally use the last element as pivot or choose the average of
three pivots.

This is somehow similar to Binary MSD radix sort, but instead of choosing a digit(word) or byte a bit is used
More comparisons is need to understand the differences in performance and implementation.

This algorithm is slower than TimSort but faster than a Stack Overflow QuickSort in general. 
However, when the number of bits is low is faster than Java TimSort.

If the bitmask is 11_1111_1111_1111_1111 then the first partition uses the mask 10_0000_0000_0000_0000, then the left and right
partitions use the mask for the next bit 01_0000_0000_0000_0000, then for the las 16 bits another algorithm is used

To further improve the performance for the last 16 bits we choose between a "Destructive Count Sort", and two different types of radix sort
one that use radix sort by bits and the other by words (8-11bits), this depends  on the values of N and r range (2^(k*d))

Optimizations:

- The partition is only done by the bits in the bit mask as described above
- Optimization for small lists and or for the last bits, to choose between destructive count sort and two different radix sort implementations
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

Is similar to the traditional Radix Sorter but instead of using a digit  in base 10 it uses a digit which is a base 2 binary number
Ska Sort as I understand uses base 256, it means a byte, each digit(word) has length of 8 bits.

Similar to the concepts of SkaSort RadixByteSorterInt has been written, you could configure it to use or not the bitmask, if using the bitmask
it can detect which of the 4 bytes of an integer needs or not to be sorted.

RadixBitSorterInt uses the bitmask, to sort just the bits that need to be sorted, bits are grouped in digit(words) 
of dynamic digit sizes from 4-16, by default a maximum size of 11 bits is used in modern computes and 8 bits in older computers

RadixBitSorter is a lot faster than to the typical stackoverflow, baeldung, hackerearth implementations as those sorters
don't use a digit in base 10, don't use bit operations as ska sort, don't use a bitmask and don't reuse the aux buffer

RadixByteSorterInt when working an all bytes (not using the bitmask) should have the same performance as SkaSort. 
See the C++ Implementation for more details on SkaSort

RadixBitSorter is faster than RadixByteSorter and SkaSort in most of the cases and machines, except when the bitmask is of size 30-32 bits
then it can have similar or a little lower performance than SkaSort.

Radix Bit Sorter Optimizations:

- It does not need to sort by all the bits just the ones in the bitmask
- It sorts by 11 bits at a time as maximum and 4 as minimum (Instead of 8 as SkaSort and RadixByteSorterInt do)

Radix Byte Sorter Optimizations:

- An option to use the bitmask and  detect which bytes do not use has been added to the algorithm (calculateBitMaskOptimization) and enable
  by default.

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

For example if the bitmask is 00000000111111111111111111111110 23 bits, and we have a 32 thread processor then:

- The first 5 bits are recursively processed by doing QuickBitSorter in multiple threads
- The next 2 bits are recursively processed by doing RadixBitSort in each thread
- The last 16 bits are done using RadixBitSort by bits or by words(4-15 bits) or destructive CountSort in each thread

# Stability

int[] sort in java use a not stable algorithm, it doesn't make sense to have a stable algorithm for ints.
Object[] sort in java is a stable algorithm, so ObjectSorter has the parameter setStable that uses more memory

# Speed

Most of the algorithms are faster than the Java default and Parallel sorters in most of the cases.

See the performance analysis at the end of this section:

### Example 1:

Comparison for sorting 10 Million int elements with range from 0 to 10 Million in an AMD Ryzen 7 4800H processor, Java
1.8.0_341

| Algorithm           | AVG CPU time [ms] |
|---------------------|------------------:|
| JavaSorterInt       |               729 |
| QuickBitSorterInt   |               298 |
| RadixBitSorterInt   |               108 |
| RadixByteSorterInt  |               110 |
| JavaSorterMTInt     |                85 |
| QuickBitSorterMTInt |               103 |
| MixedBitSorterMTInt |                97 |
| RadixBitSorterMTInt |                65 |

![Graph2](test-results/ST_Int_AMD4800H_10M_randomRange_0_10M.png?raw=true "Graph2")

![Graph2](test-results/MT_Int_AMD4800H_10M_randomRange_0_10M.png?raw=true "Graph2")

### Example 2:

Comparison for sorting 10 Million int elements with range from 0 to 100000 in an AMD Ryzen 7 4800H processor, Java
1.8.0_341

| Algorithm           | AVG CPU time  [ms] |
|---------------------|-------------------:|
| JavaSorterInt       |                541 |
| QuickBitSorterInt   |                 54 |
| RadixBitSorterInt   |                 85 |
| RadixByteSorterInt  |                111 |
| JavaSorterMTInt     |                 81 |
| QuickBitSorterMTInt |                 54 |
| MixedBitSorterMTInt |                 48 |
| RadixBitSorterMTInt |                 54 |

![Graph2](test-results/ST_Int_AMD4800H_10M_randomRange_0_100000.png?raw=true "Graph2")

![Graph2](test-results/MT_Int_AMD4800H_10M_randomRange_0_100000.png?raw=true "Graph2")

### Example 3:

Comparison for sorting 40 Million int elements with range from 0 to 1000000000 in an AMD Ryzen 7 4800H processor, Java
1.8.0_341

| Algorithm             | AVG CPU time  [ms] |
|-----------------------|-------------------:|
| JavaSorterInt         |               3174 |
| QuickBitSorterInt     |               2593 |
| RadixBitSorterInt     |                474 |
| RadixByteSorterInt    |                505 |
| JavaParallelSorterInt |                365 |
| QuickBitSorterMTInt   |                589 |
| MixedBitSorterMTInt   |                449 |
| RadixBitSorterMTInt   |                289 |

![Graph2](test-results/ST_Int_AMD4800H_40M_randomRange_0_1000M.png?raw=true "Graph2")

![Graph2](test-results/MT_Int_AMD4800H_40M_randomRange_0_1000M.png?raw=true "Graph2")

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

Comparison for sorting 10 Million objects with int key with range from 0 to 10 Million in an AMD Ryzen 7 4800H
processor, Java 1.8.0_341

| Algorithm               | AVG CPU time [ms] |
|-------------------------|------------------:|
| JavaSorterObjectInt     |              5584 |
| JavaSorterMTObjectInt   |               717 |
| RadixBitSorterObjectInt |               813 |

![Graph2](test-results/ST_ObjectInt_AMD4800H_10M_randomRange_0_10M.png?raw=true "Graph2")

###Example 5:

Comparison for sorting 10 Million elements with int key range from 0 to 100000 in an AMD Ryzen 7 4800H processor, Java
1.8.0_341

| Algorithm               | AVG CPU time  [ms] |
|-------------------------|-------------------:|
| JavaSorterObjectInt     |               4450 |
| JavaSorterMTObjectInt   |                826 |
| RadixBitSorterObjectInt |                644 |

![Graph2](test-results/ST_ObjectInt_AMD4800H_10M_randomRange_0_100000.png?raw=true "Graph2")

##History of improvements on performance on this project

![Graph2](test-results/ST_Int_AMD4800H_40M_randomRange_0_1000M_HISTORY.png?raw=true "Graph2")

![Graph2](test-results/MT_Int_AMD4800H_40M_randomRange_0_1000M_HISTORY.png?raw=true "Graph2")

# Support for other types have been added
- Support for sorting floats, doubles, longs and Objects with int fields is done

# O(N) Complexity

TODO Needs to be evaluated in detail

(using wikipedia names)
https://en.wikipedia.org/wiki/Sorting_algorithm

- n = number of elements
- r = range (2^(k*d)) or (2^(d1+d2...dn) or (2^m) 
- m = number of bits on mask
- k = number of digits/words
- d = length of a digit/word in bits (tipically 8 bits)
- t = number of hardware threads

| Algorithm        |   CPU worst   |         CPU average |       CPU best | MEM worst | MEM average | MEM best |
|------------------|:-------------:|--------------------:|---------------:|----------:|------------:|---------:|

The table above needs to be recalculated

## Comparing to Ska Sort

See the repository (https://github.com/aldo-gutierrez/bitmasksorterCpp) where a comparison has been done.
In summary RadixBitSorterInt is faster than SkaSort when the mask has 30 bits or lower, m<=30, r<=2^30, 
with 31, 32 bits it could be slower, of similar performance or faster (faster when using a digit/word of 9 bits or larger when detected a modern processor)

RadixByteSorterInt with calculateBitMaskOptimization=false looks similar in performance than SkaSort

## TODO

- Support for sorting Integer, Floats, Doubles, Longs and Objects with any primitive or native wrapper field needs to be done
- Evaluation on complexity
- More Testing, have 100% code coverage
- Compare with [Wolf Sort] (https://github.com/scandum/wolfsort)
- Find the best values for constants for single and multi-thread and for different machines
- Have comparisons and documentation of speed in powers of two instead of some powers of ten
- Optimize for almost sorted list or for list that have few sorted parts as JavaSorter

## DONE

- Test with stable partition with less memory, no memory and other experiments have been moved to a different project called bit-mask-sorters-experimental

## Algorithms Testing

| Algorithm            | Positive random numbers | Negative random numbers | Unsigned numbers | Sorted | Reverse sorted |
|----------------------|-------------------------|-------------------------|------------------|--------|----------------|
| QuickBitSorterInt    | ok                      | ok                      | ok               | ok     | ok             |
| RadixBitSorterInt    | ok                      | ok                      | ok               | ok     | ok             |
| RadixByteSorterInt   | ok                      | ok                      | ok               | ok     | ok             |
| QuickBitSorterMTInt  | ok                      | ok                      | ok               | ok     | ok             |
| MixedBitSorterMTInt  | ok                      | ok                      | ok               | ok     | ok             |
| RadixBitSorterMTInt  | ok                      | ok                      | ok               | ok     | ok             |
| RadixBitSorterObject | ok                      | ok                      | not tested       | ok     | ok             |
