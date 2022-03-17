# Mask Bit Sorter
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
Having the bit mask also helps when doing a flexible CountSort for the last bits.

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
//Parallel sorting uint numbers 
IntSorter sorter = new QuickBitSorterMTInt();
int[] list = ....
sorter.sort(list);
```
```
//Sorting uint numbers 
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
 
The application of count sort in the last bits of the mask works even if the mask doesn't have consecutive 1s. 
For example for example: 1110011, only 5 bits change, we could use 5 bits 2^5 32 slots for Count Sort

Optimizations:
- The partition is only done by the bits in the mask as described above
- Optimization for small lists and or for the last bits, to choose between count sort and two different radix sorts
- Multithreading support

## RadixBitSorter:

Usage:
```
//Sorting uint numbers 
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
As is binary the Count Sort is a little different. Also, it doesn't need to sort by all bits
just by the bits that are in the bit mask.

If you test the traditional stackoverflow, baeldung, hackerearth or any other site, you will see
a radix sort that is 10Base and that normally is slower than TimSort (JavaSort)

On the last week of february i hear about the project called Ska Sort, that is a radix sort implementation
that works on any type of data but it works at byte level. The difference with this implementation is
that we use a bitmask and do the radix sort only with those bits but not with all bytes.


Optimizations:
- It does not need to sort by all the bits just the ones in the bitmask
- It sorts by 11 bits at a time as maximum (Instead of 8 as suggested in other sites, more faster in my machine but more test in different machines needs to be done)

## MixedBitSorter:
It is a multi thread sorter, it combines previous Bit QuickSort until having enough threads, then RadixBitSort and lastly CountSort for the last bits
For example if the bitmask is 00000000111111111111111111111110  
Then there are 23 bits, if we have a 32 thread processor then:

For example:

- The first 5 bits are recursively processed by doing Bit QuickSort in multiple threads, supposing your machine has 32 threads
- The next 2 bits are recursively processed by doing RadixBitSort
- The last 16 bits are done using CountSort while doing RadixBitSort
- In total 23 bits are processed, the last bit 0 is not used

# Speed
Most of the algorithms are faster than the Java default and Parallel sort
In the test from 10000 to 40000000 in list size and from range 10 to 1000000000 RadixBitSorterMTInt
wins the majority.

###Example 1: 

Comparison for sorting 10 Million int elements with range from 0 to 10 Million in an AMD Ryzen 7 4800H processor

| Algorithm        | AVG CPU time [ms] |
| ---------------- |:-------------:|
|JavaIntSorter|748|
|QuickBitSorter3UInt|423|
|RadixBitSorterUInt|115|
|JavaParallelSorter|144|
|QuickBitSorterMTUInt|118|
|MixedBitSorterMTUInt|98|
|RadixBitSorterMTUInt|96|


![Graph2](plot-S10000000-Range0-10000000-random.png?raw=true "Graph2")

###Example 2:

Comparison for sorting 10 Million int elements with range from 0 to 100000 in an AMD Ryzen 7 4800H processor

| Algorithm        | AVG CPU time  [ms]|
| ---------------- |:-------------:|
|JavaIntSorter|327|
|QuickBitSorter3UInt|25|
|RadixBitSorterUInt|52|
|JavaParallelSorter|94|
|QuickBitSorterMTUInt|23|
|MixedBitSorterMTUInt|23|
|RadixBitSorterMTUInt|23|

![Graph2](plot-S10000000-Range0-100000-random.png?raw=true "Graph2")

###Example 3:

Comparison for sorting 40 Million int elements with range from 0 to 1000000000 in an AMD Ryzen 7 4800H processor

| Algorithm        | AVG CPU time  [ms]|
| ---------------- |:-------------:|
|JavaIntSorter|3542|
|QuickBitSorterUInt|2960|
|RadixBitSorterUInt|706|
|JavaParallelSorter|410|
|QuickBitSorterMTUInt|657|
|MixedBitSorterMTUInt|664|
|RadixBitSorterMTUInt|397|

![Graph2](plot-S40000000-Range0-1000000000-random.png?raw=true "Graph2")

Object Sort using the Interface IntComparator

```
public class Entity1 {
    int id;
    String name;
}

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

Object[] list = ...;
Arrays.sort(list, comparator);
```

###Example 4: 

Comparison for sorting 10 Million objects with int key  with range from 0 to 10 Million in an AMD Ryzen 7 4800H processor

| Algorithm        | AVG CPU time [ms] |
| ---------------- |:-------------:|
|JavaObjectSorter|8229|
|JavaObjectParallelSorter|1464|
|RadixBitSorterObject|1333|

![Graph2](plot-S10000000-Range0-10000000-random-object.png?raw=true "Graph2")

###Example 5:

Comparison for sorting 10 Million elements with int key range from 0 to 100000 in an AMD Ryzen 7 4800H processor

| Algorithm        | AVG CPU time  [ms]|
| ---------------- |:-------------:|
|JavaObjectSorter|6745|
|JavaObjectParallelSorter|1366|
|RadixBitSorterObject|1315|

![Graph2](plot-S10000000-Range0-100000-random-object.png?raw=true "Graph2")


# O(N) Complexity. Needs to be evaluated

- n = number of elements
- k = number of bits on mask
- t = number of threads
- c = number of bits for counting sort
- q = number of bits for quick sort


| Algorithm        | CPU worst     | CPU average         | CPU best       | MEM worst | MEM average | MEM best |
| ---------------- |:-------------:| -------------------:| --------------:| ---------:| -----------:|---------:|
| QuickBitSorter   | O(n * log(n)) | O(n * log(n))?      | O(n+k), k <= c | O(2^c)    | O(2^c)      |    1     |
| RadixBitSorter   | O(n * log(n)) | O(n * k), k<log2(n) | O(n), k = 1    | O(n)      | O(n)        |    1     |
| QuickBitSorterMT | O(n * log(n)) | O(n * log(n))?      | O(n+k), k <= c | O(t*2^c)  | O(2^c)      |    1     |
| MixedBitSorterMT | O(n * log(n)) | O(n * log(n))?      | O(n), k <= t   | O(n*t)    | O(n*t)      |  O(2^c)  |
| RadixBitSorterMT | O(n * log(n)) | O(n * k), k<log2(n) | O(n), k = 1    | O(n)      | O(n)        |    1     |

## Things to DO
- Add More Object sorters besides RadixBitSorter
- Add Long, Short, Byte sorters
- Add C#, C++, Python, Javascript Implementations
- More evaluation on complexity
- More testing
- Compare with [Ska Sort] (https://github.com/skarupke/ska_sort)
- Compare with [Wolf Sort] (https://github.com/scandum/wolfsort) 

## Algorithms Ready for Prod
- RadixBitSorterInt 
- RadixBitSorterMTInt