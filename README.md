# Bit Mask Sorters

This project explores various sorting algorithms employing a BitMask approach.

The Java implementation can be found in this repository.

For implementations in other languages, please refer to the following repositories:

[C# Version] (https://github.com/aldo-gutierrez/bitmasksorterCSharp)

[C++ Version] (https://github.com/aldo-gutierrez/bitmasksorterCpp)

[Javascript Version] (https://github.com/aldo-gutierrez/bitmasksorterJS)

[Python Version] (https://github.com/aldo-gutierrez/bitmasksorterPython)

# BitMask

A BitMask can be utilized to reduce the computational overhead required for sorting.

The following code demonstrates the calculation of the BitMask:

``` java
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

## Case 1 Simple BitMask

Some properties of this BitMask are:
* This BitMask has only the bit that change
* If the BitMask is 0 it means that all the numbers are equal

First, let's consider an example: suppose we have a list of integers containing numbers from 0 to 127. 
In this case, the bitmask would be 1111111.

|                 | Number |             Bits | 
|-----------------|-------:|-----------------:|
| Min:            |      0 | 0000000000000000 |
| Max:            |    127 | 0000000001111111 |
| Average/Median: |     64 | 0000000001000000 |
| Bits Used (b):  |      7 | 0000000001111111 |

We can readily identify certain optimizations in algorithms.
* QuickSort: 
  - Now is easy to know which pivot use just use the average, in this case 64 (1000000)
  - Each quick sort call is using a bit of information
* Radix sort:
  - A typical LSD Radix sorts for integers (32 bits) uses 4 count sort passes, it could just use one 
* Count sort/ Pingeon count sort
  - We have already de Min/Max values in the BitMask

## Case 2 Complex BitMask

Second, let's consider a more complex example: suppose we have a list of integers containing numbers 85, 84, 21 and 5.
In this case, the bitmask would be 1010001. As only the bits that change are part of the mask, 3 bits in this case.

|        Number |             Bits |
|--------------:|-----------------:|
|            85 | 0000000001010101 | 
|            84 | 0000000001010100 |
|            21 | 0000000000010101 |
|             5 | 0000000000000101 |
|          MASK | 0000000001010001 |

|                      | Number |                   Bits | 
|----------------------|-------:|-----------------------:|
| Mask Min:            |      4 | 000000000(0)0(0)010(0) |
| Mask Max:            |     85 | 000000000(1)0(1)010(1) |
| Mask Average/Median: |     64 | 000000000(1)0(0)000(0) |
| Mask Bits Used (d):  |      3 | 000000000(1)0(1)000(1) |

* QuickSort:
  - Easy to know which pivot use just use the average
  - Each quick sort call is using a bit of information
* Radix sort:
  - We will need a modified Radix Sort that uses only the 3 bits in one pass
* Count sort/ Pingeon count sort
  - We have already de Min/Max values in the BitMask
  - However, we could improve the efficiency by only considering the ones in the mask, 2^3 values, 000(0) to 111(7), 8 values in total 

## Case 3 Bad BitMasks

Third, let's consider some problems with the BitMask 

Example 3A: If a list contains only two numbers, values:  1111111110000000(65408) and 0000000111111111(511). For this case the mask
is 1111111001111111, instead of just 1 bit, so it doesn't help too much. 
After some step in the sort algorithm it could be a good idea to recalculate the BitMask maybe is 0 and there is no need to further process 

Example 3B: If a list contains positive and negative numbers then the mask will probably be -1 which is 0xFFFFFFFF or 0b1111111111111111111111111111111.
Even if the numbers in the list are just 1 and -1, so the mask doesn't help. 
But if we put the positives to one side, negatives to the other we could recalculate the BitMask and get a more useful BitMask.

Example 3C: If the BitMask is 0xFFFFFFFF (0b1111111111111111111111111111111) because the numbers in the list are using the full range of Integer, 
then the BitMask doesn't to optimize a sort algorithm.   


# Hybrid Approach

There are many algorithms that use a hybrid approach for example TimSort.

TimSort is a hybrid sorting algorithm that employs merge sort for large arrays of objects and insertion sort for small arrays, adjusting its strategy based on the array size (N).

I think we can use the BitMask to create a hybrid algorithm that takes into account not only N but also r (the range). 
We could use the BitMask for calculating the range, the range could be reduced if we only use the bits set to one as described in previous paragraphs.

AGSelectorSorterInt is an example of that hybrid algorithm that choose between different algorithms that uses BitMasks


# Notation

Some variables defined in a radix sorter

r = range, for example for unsigned integers is 2^32

d = length of word or digit in bits, typical value is 8 bits (a byte)

k = number of words/digits, for an integer this is 4 (32/8)

with this notation r = 2^(k*d) 

For BitMask sorters 

m = length in bits set to one of the BitMask, for integers numbers it goes from 0 to 32

r = range, that is calculated as 2^m

d = length of word or digit in bits, it can be from 1..16 bits, we are using 11 for modern processors and 8 for a dual-core machine or lower

k = number of word/digits 


## QuickBitSorter

Is similar to QuickSort but for choosing the pivot it uses the BitMask. The first bit set to one of the BitMask is used as pivot
In the next recursion the next bit of the bitmask is used.
This is different to other QuickSort algorithms that normally use the last element as pivot or choose the average of
three pivots.

This is somehow similar to Binary MSD radix sort, but instead of choosing a digit(word) or byte a bit is used
More comparisons and study is need to understand the differences in performance and implementation.

This algorithm is slower than TimSort but faster than a Stack Overflow QuickSort samples in general. 
However, when the number of bits is low is faster than Java TimSort.

If the bitmask is 11_1111_1111_1111_1111 then the first partition uses the mask 10_0000_0000_0000_0000, then the left and right
partitions use the mask for the next bit 01_0000_0000_0000_0000, after some iterations and when there is less than a number of bits 
in the remaining BitMask we switch to a Java Sort or to a Count Sort Algorithm (Pigeonhole Sort or two types of Radix Sorter with bits)  

In Example 3A there are only two different numbers in the list but the BitMask (m) is big (1111111001111111), In the first partition we use the first bit(1000000000000000), 
in the next recursive partition/iteration it will detect that all numbers are going to just one partition, so we recalculate the mask, and in this case it will produce a bitMask with value 0 so the sort is complete.

Optimizations:

- The partition is only done by the bits set to one in the BitMask as described above
- When the remaining bits in the BitMask are small it switches to a Java Sort or to a Count Sort Algorithm (Pigeonhole Sort or two types of Radix Sorter with bits)
- Multithreading support

See an analysis of the performance in the section Speed and Performance

Usage:

```
//Sorting int numbers 
SorterInt sorter = new QuickBitSorterInt();
int[] list = ....
sorter.sort(list);
```

```
//Parallel sorting int numbers 
SorterInt sorter = new QuickBitSorterMTInt();
int[] list = ....
sorter.sort(list);
```

```
//Sorting unsinged int numbers 
SorterInt sorter = new QuickBitSorterInt();
sorter.setUnsigned(true);
int[] list = ....
sorter.sort(list);
```

## RadixBitSorter:

RadixBitSorter is a sorter that uses the BitMask to sort with just the bits that need to be sorted. 
The BitMask is used to reduce the number of count sort iterations.

Some implementations of Radix Sort are very unoptimized stackoverflow, baeldung, hackerearth have a lot of this.
They use sometimes base 10 and sometimes bucket sort, compared to those RadixBitSorter is way faster.

Some optimized implementations use base 256 using bit operations. A typical optimized Radix Sorter for integers (32 bits) could use 8bits (words/digits) and needs 4 steps of Count Sort.  

After a lot of testing I found that in my machine (AMD Ryzen 7 4800H) 11bits is the best word size and I also found that in some older computers dual core  8bits is the best size.
That was found many years ago as I later discovered. So RadixBiSorter change the size of the words according to the number of cores that are somehow correlated with the Cache Sizes.

Some other optimizations like swapping the aux buffer with the array buffer are also implemented but with mixed results. 

A typical optimized Radix Sorter for integers (32 bits) could use 8bits (words/digits) and needs 4 iterations of Count Sort, with the BitMask we can
reduce the iterations to 1 in some cases. 

Ska Sort as I understand is an optimized C++ radix sort but is in place. RadixBitSorter is not an in place sorter as it uses and aux buffer.

RadixBitSorter is tipically faster than SkaSort, but when the bitmask is of size 30-32 bits then it can have similar or a little lower performance than SkaSort.

Radix Bit Sorter Optimizations:

- It does not need to sort by all the bits just the ones in the bitmask
- It sorts by 11 bits at a time as maximum and 4 as minimum (Instead of 8 as SkaSort and RadixByteSorterInt do)

## RadixByteSorter:
Is a RadixBitSorter but that always uses 8bits with byte boundaries, it can or not use the BitMask to optimize the iterations of count sort

RadixByteSorterInt when working an all bytes (not using the bitmask) should have the same performance as SkaSort and tipical optimized radix sorters

RadixBitSorter is faster than RadixByteSorter and SkaSort in most of the cases and machines i tested.

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

For example if the bitmask is 00000000111111111111111111111110 23 bits, and we have a 32 thread processor then with default configuration:

- The first 5 bits are recursively processed by doing QuickBitSorter in multiple threads
- The next 2 bits are recursively processed by doing RadixBitSort in each thread
- The last 16 bits are done using a Count Sort Algorithm (Pigeonhole Sort or two types of Radix Sorter with bits)
- 
# Stability

int[] sort in java uses a not stable algorithm, we use same philosophy.
Object[] sort in java is a stable algorithm, we use same philosophy.

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

<a href="https://raw.githack.com/aldo-gutierrez/bitmasksorter/main/test-results/MT_Int_AMD4800H_10M_randomRange_0_100000_HISTORY.html" target="_blank">Multi thread results.</a>

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

![Graph2](test-results/MT_Int_AMD4800H_40M_randomRange_0_1000M.html?raw=true "Graph2")

Object Sort using the Interface Intmapper

```
        public class Entity1 {
            int id;
            String name;
        }

        Intmapper<Entity1> mapper = new Intmapper<Entity1>() {
            @Override
            public int intValue(Entity1 o) {
                return o.getId();
            }

            @Override
            public int compare(Entity1 entity1, Entity1 t1) {
                return Integer.compare(entity1.getId(), t1.getId());
            }
        };

        EntityInt1[] list = ...;
        ObjectSorter sorter = new RadixBitSorterObjectInt();
        sorter.sort(list, mapper);
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
- d = length of a digit/word in bits (typically 8 bits)
- t = number of hardware threads

| Algorithm        |   CPU worst   |         CPU average |       CPU best | MEM worst | MEM average | MEM best |
|------------------|:-------------:|--------------------:|---------------:|----------:|------------:|---------:|

The table above needs to be recalculated

## Comparing to Ska Sort

See the repository (https://github.com/aldo-gutierrez/bitmasksorterCpp) where a comparison has been done.
In summary RadixBitSorterInt is faster than SkaSort when the mask has 30 bits or lower, m<=30, r<=2^30, 
with 31, 32 bits it could be slower, of similar performance or faster (faster when using a digit/word of 9 bits or larger when detected a modern processor)


## TODO

- Rename code that uses K as range replace it with R
- Evaluation on complexity
- More Testing, have 100% code coverage
- Compare with [Wolf Sort] (https://github.com/scandum/wolfsort)
- Find the best values for constants for single and multi-thread and for different machines
- Have comparisons and documentation of speed in powers of two instead of some powers of ten as it will be easier to compare with other sorters
- Optimize for almost sorted list or for list that have few sorted parts as JavaSorter
- Create a object sorter algorithm ease to use and that sort for multiple columns
- Create a code generator because most Float, Double, etc sorters share the same code but for performance reasons and java limitations the Generic implementation is slower

## DREAMS
- Test Sorters that use one half, one third, or sqrt() of memory and still stable
- Stable sorter with no additional memory

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
