# Mask Bit Sorters
This project tests different ideas for sorting algorithms. 

We use a bitmask as a way to get statistical information about the numbers to be sorted

All the algorithms use this bitmask, For example suppose the list contains numbers from 0 to 127 
then the bitmask is 1111111. Another example if the list contains the numbers 85, 84, 21 and 5 the mask is 1010001, 
only the bits that change are part of the mask in this case 3 bits

| Number        | Bits |
| ---------------- |:-------------:|
|85  |0000000001010101|
|84  |0000000001010100|
|21  |0000000000010101|
|5   |0000000000000100|
|MASK|0000000001010001|


## QuickBitSorter
Is similar to QuickSort but for choosing the pivot we use the bit mask. 
Having the bit mask also helps when doing a CountSort or Radix sort for the last bits.

This is different to other QuickSort algorithms that normally use the last element as pivot or choose the average of three
pivots.

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

### How the selection of the pivot works:

For example suppose the list contains all the numbers from 0 to 127  the mask is 1111111 and to choose the middle 1000000 (64) 
that is probably a good pivot.

In the worst case (32 bit mask) the last recursion level is 32, but we use CountSort on the last 16 bits (countingSortBits default value) so
 the max recursion level is 16
 
The application of count sort in the last bits of the mask works even if the mask doesn't have consecutive ones (1). 
For example in the mask 1110011, only 5 bits change, we could use 5 bits 2^5 32 slots for Count Sort

Optimizations:
- The partition is only done by the bits in the mask as described above
- Optimization for small lists and or for the last bits, to choose between count sort and two different radix sort implementations
- Multithreading support

## RadixBitSorter:

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
### How it works:

Is similar to the traditional Radix Sorter but instead of using a 10 Base it uses a 2 Base.
As is binary the Count Sort is a different implementation but similar in intention. Also, it doesn't need to sort by all bits
just by the bits that are in the bit mask.

If you test the traditional stackoverflow, baeldung, hackerearth or any other site, you will see
a radix sort that is 10Base and that normally is slower than TimSort (JavaSort)

On the last week of february 2022 i hear about the project called Ska Sort, that is a radix sort implementation
that works on any type of data but it works at byte level. I still need to compare with a java version
of Ska Sort. Using some of the ideas of Ska Sort (not all) and many forums a RadixByteSorterInt sorter has been
developed to. The difference between RadixByteSorterInt and RadixBitSorterInt is that the later use 
only the bits in the bitmask to do the sorting and not sorting with all the bytes.
Comparing the performance of RadixBitSorterInt is faster when the range of the numbers is 30-32 bits but similar
to RadixByteSorter otherwise.


Optimizations:
- It does not need to sort by all the bits just the ones in the bitmask
- It sorts by 11 bits at a time as maximum (Instead of 8 as suggested in other sites, more faster in my machine but more test in different machines needs to be done)

## MixedBitSorter:
It is a multi thread sorter, it combines previous QuickBitSorter until having the same threads as the hardware threads in the processor,
then it uses RadixBitSort for the middle bits and lastly it uses CountSort for the last bits
For example if the bitmask is 00000000111111111111111111111110  
Then there are 23 bits, if we have a 32 thread hardware processor then:

For example:

- The first 5 bits are recursively processed by doing QuickBitSorter in multiple threads
- The next 2 bits are recursively processed by doing RadixBitSort in each thread
- The last 16 bits are done using CountSort while doing RadixBitSort in each thread
- In total 23 bits are processed only the ones in the bitmask

# Stability
int[] sort in java use a not stable algorithm, it doesn't make sense to have a stable algorithm. 
Object[] sort in java is a stable algorithm, so ObjectSorter has the parameter setStable but that uses more memory offcourse

# Speed
Most of the algorithms are faster than the Java default and Parallel sorters in most of the cases

### Example 1: 

Comparison for sorting 10 Million int elements with range from 0 to 10 Million in an AMD Ryzen 7 4800H processor, Java 11

| Algorithm        | AVG CPU time [ms] |
| ---------------- |:-------------:|
|JavaIntSorter|721|
|QuickBitSorterInt|355|
|RadixBitSorterInt|106|
|JavaParallelSorterInt|85|
|RadixByteSorterInt|186|
|QuickBitSorterMTInt|104|
|MixedBitSorterMTInt|97|
|RadixBitSorterMTInt|105|


![Graph2](plot-S10000000-Range0-10000000-random.png?raw=true "Graph2")

### Example 2:

Comparison for sorting 10 Million int elements with range from 0 to 100000 in an AMD Ryzen 7 4800H processor, Java 11

| Algorithm        | AVG CPU time  [ms]|
| ---------------- |:-------------:|
|JavaSorterInt|533|
|QuickBitSorterInt|53|
|RadixBitSorterInt|101|
|RadixByteSorterInt|188|
|JavaParallelSorterInt|85|
|QuickBitSorterMTInt|51|
|MixedBitSorterMTInt|50|
|RadixBitSorterMTInt|82|

![Graph2](plot-S10000000-Range0-100000-random.png?raw=true "Graph2")

### Example 3:

Comparison for sorting 40 Million int elements with range from 0 to 1000000000 in an AMD Ryzen 7 4800H processor, Java 11

| Algorithm        | AVG CPU time  [ms]|
| ---------------- |:-------------:|
|JavaSorterInt|3160|
|QuickBitSorterInt|2868|
|RadixBitSorterInt|681|
|RadixByteSorterInt|673|
|JavaParallelSorterInt|382|
|QuickBitSorterMTInt|558|
|MixedBitSorterMTInt|601|
|RadixBitSorterMTInt|466|

![Graph2](plot-S40000000-Range0-1000000000-random.png?raw=true "Graph2")

### Table of 1st and 2nd algorithm by speed AMD Ryzen 7 4800H processor, Java 11, pluged in

| N / range | 10                                    | 1,000                                   | 100,000                               | 10,000,000                                | 1,000,000,000                            |
|---------------|---------------------------------------|-----------------------------------------|---------------------------------------|-------------------------------------------|-------------------------------------------|
| 10,000        | RadixBitSorterInt RadixBitSorterMTInt | MixedBitSorterMTInt RadixBitSorterInt   | RadixBitSorterInt RadixBitSorterMTInt | RadixBitSorterInt RadixBitSorterMTInt     | RadixBitSorterInt RadixBitSorterMTInt     |
| 100,000       | QuickBitSorterInt RadixBitSorterMTInt | RadixBitSorterMTInt MixedBitSorterMTInt | RadixBitSorterInt QuickBitSorterInt   | RadixBitSorterInt RadixByteSorterInt      | RadixBitSorterInt RadixByteSorterInt      |
| 1,000,000     | QuickBitSorterInt MixedBitSorterMTInt | RadixBitSorterMTInt MixedBitSorterMTInt | QuickBitSorterMTInt MixedBitSorterMTInt | RadixBitSorterMTInt JavaParallelSorterInt | JavaParallelSorterInt RadixBitSorterMTInt |
| 10,000,000    | MixedBitSorterMTInt QuickBitSorterMTInt | MixedBitSorterMTInt QuickBitSorterMTInt | QuickBitSorterMTInt MixedBitSorterMTInt | RadixBitSorterMTInt JavaParallelSorterInt | RadixBitSorterMTInt JavaParallelSorterInt |
| 40,000,000    | QuickBitSorterMTInt MixedBitSorterMTInt | MixedBitSorterMTInt RadixBitSorterMTInt | MixedBitSorterMTInt QuickBitSorterMTInt | RadixBitSorterMTInt MixedBitSorterMTInt | RadixBitSorterMTInt JavaParallelSorterInt |

### Table of 1st and 2nd algorithm by speed AMD Ryzen 7 4800H processor, Java 11, with battery


| N / range | 10                                    | 1,000                                 | 100,000                             | 10,000,000                           | 1,000,000,000                        |
|---------------|---------------------------------------|---------------------------------------|-------------------------------------|--------------------------------------|--------------------------------------|
| 10,000        | RadixBitSorterInt RadixBitSorterMTInt | RadixBitSorterInt RadixBitSorterMTInt | RadixBitSorterInt RadixBitSorterMTInt | RadixBitSorterInt RadixBitSorterMTInt | RadixBitSorterInt RadixBitSorterMTInt |
| 100,000       | QuickBitSorterInt MixedBitSorterMTInt | RadixBitSorterMTInt MixedBitSorterMTInt | RadixBitSorterInt QuickBitSorterInt | RadixBitSorterInt RadixByteSorterInt | RadixBitSorterInt RadixByteSorterInt |
| 1,000,000     | RadixBitSorterMTInt MixedBitSorterMTInt | QuickBitSorterMTInt QuickBitSorterInt | QuickBitSorterInt MixedBitSorterMTInt | RadixBitSorterInt RadixBitSorterMTInt | RadixBitSorterMTInt RadixBitSorterInt |
| 10,000,000    | MixedBitSorterMTInt QuickBitSorterMTInt | MixedBitSorterMTInt QuickBitSorterMTInt | QuickBitSorterMTInt MixedBitSorterMTInt | QuickBitSorterMTInt MixedBitSorterMTInt | RadixBitSorterMTInt JavaParallelSorterInt |
| 40,000,000    | QuickBitSorterMTInt QuickBitSorterInt | QuickBitSorterMTInt MixedBitSorterMTInt | QuickBitSorterMTInt MixedBitSorterMTInt | MixedBitSorterMTInt JavaParallelSorterInt | JavaParallelSorterInt RadixBitSorterMTInt |


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

Comparison for sorting 10 Million objects with int key  with range from 0 to 10 Million in an AMD Ryzen 7 4800H processor, Java 11

| Algorithm        | AVG CPU time [ms] |
| ---------------- |:-------------:|
|JavaSorterObject|7715|
|JavaParallelSorterObjectInt|1510|
|RadixBitSorterObjectInt|1274|

![Graph2](plot-S10000000-Range0-10000000-random-object.png?raw=true "Graph2")

###Example 5:

Comparison for sorting 10 Million elements with int key range from 0 to 100000 in an AMD Ryzen 7 4800H processor, Java 11

| Algorithm        | AVG CPU time  [ms]|
| ---------------- |:-------------:|
|JavaSorterObject|6149|
|JavaParallelSorterObjectInt|1355|
|RadixBitSorterObjectInt|1250|

![Graph2](plot-S10000000-Range0-100000-random-object.png?raw=true "Graph2")


# O(N) Complexity. Needs to be evaluated

- n = number of elements
- k = number of bits on mask, 2^k is the range of numbers
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
I Still didn't implement the same algorithm but RadixByteSorterInt uses some ideas of Ska Sort
Ska Sort is faster sometimes when the range of the number is 2^30 (30 bits) or more, and RadixBitSorter is faster otherwise

More comparison is needed.

## TODO
- Add More Object sorters in addition to RadixBitSorter
- Add Long, Short, Byte sorters (Long is the priority)
- Add C#, C++, Python, Javascript Implementations
- More Detailed evaluation on complexity
- More Testing
- Compare with [Ska Sort] (https://github.com/skarupke/ska_sort)
- Compare with [Wolf Sort] (https://github.com/scandum/wolfsort) 
- Merge partition with bitmask extraction or isSorted with partition and bitmask and compare speed
- Test different algorithms for stable partition with less memory and compare speed
- Find the number N where Parallelization  of bitmask is faster than serial
- Find the number N where Parallelization  of isOrdered is faster than serial
- Find the number N where Parallelization  of partition is faster than serial
- Tests with repeatable random seeds
- Have comparations and documentation of speed in powers of two instead of powers of ten

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
