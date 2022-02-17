# Mask Bit Sorter
This project tests different ideas for sorting algorithms. 

We use a bitmask as a way to get statistical information about the numbers to be sorted

## QuickBitSorter
Is similar to QuickSort but for choosing the pivot we choose it using a bit mask. 
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
IntSorter sorter = new QuickBitSorterMTUInt();
int[] list = ....
sorter.sort(list);
```
```
//Sorting uint numbers 
IntSorter sorter = new QuickBitSorter3UInt();
int[] list = ....
sorter.sort(list);
```

### How the selection of the pivot works:

For example suppose the list contains numbers from 0 to 127 
000000000000 -> 000001111111  ==>  the mask is 1111111 and to choose the middle 1000000 (64) that is probably a good pivot.
In the worst case (32 bit mask) the last recursion level is 32, even better we could do Count Sort in the last bits

We apply Count Sort if the number of bits of the mask that remain to be evaluated are less than a constant countingSortBits.
The bits of the mask don't need to be adjacent for example for example: 1110011 we could use 5 bits 2^5 32 slots for Count Sort

Optimizations:
- Count sort for last bits adjacent or not
- Optimization for small lists
- Multithreading support
- Doesn't need to partition by all the bits just the ones used


## RadixBitSorter:

Usage:
```
//Sorting uint numbers 
IntSorter sorter = RadixBitSorter2UInt();
int[] list = ....
sorter.sort(list);
```
### How it works:

Is similar to the traditional RadixSorter but instead of using a 10 Base it uses a 2 Base.
As is binary the Count Sort is a little different. Also, it doesn't need to sort by all bits
, just by the bits that are in the bit mask.

Optimizations:
- It does not need to sort all the bits just the ones used
- It sorts by 8 bits at a time as maximum

## MixedBitSorter:
It is a multi thread sorter, it combines Bit QuickSort  then RadixBitSort and lastly CountSort
For example if the bitmask is 00000000111111111111111111111110  
Then there are 23 bits, if we have a 32 thread processor then:

- The first 5 bits are recursively processed by doing Bit QuickSort in multi thread
- The next 2 bits are recursively processed by doing RadixBitSort
- The last 16 bits are done using CountSort while doing RadixBitSort
- In total 23 bits are processed, the last bit 0 is not used

# Speed
See file Comparison.xlsx
Most of the algorithms are faster than the Java default (Tim Sort) and Parallel sort under some conditions

Example comparison for sorting 10 Million elements with range from 0 to 10 Million in an AMD Ryzen 7 4800H processor

- Elapsed JavaIntSorter AVG: 792
- Elapsed QuickBitSorter3UInt AVG: 347
- Elapsed RadixBitSorter2UInt AVG: 125
- Elapsed JavaParallelSorter AVG: 88
- Elapsed QuickBitSorterMTUInt AVG: 107
- Elapsed MixedBitSorterMTUInt AVG: 99


Example comparison for sorting 10 Million elements with range from 0 to 100000 in an AMD Ryzen 7 4800H processor

- Elapsed JavaIntSorter AVG: 597
- Elapsed QuickBitSorter3UInt AVG: 53
- Elapsed RadixBitSorter2UInt AVG: 122
- Elapsed JavaParallelSorter AVG: 97
- Elapsed QuickBitSorterMTUInt AVG: 50
- Elapsed MixedBitSorterMTUInt AVG: 50

# O(N) Complexity. Needs to be evaluated

n = number of elements
k = number of bits on mask
t = number of threads
c = number of bits for counting sort
q = number of bits for quick sort


| Algorithm        | CPU worst     | CPU average         | CPU best       | MEM worst | MEM average | MEM best |
| ---------------- |:-------------:| -------------------:| --------------:| ---------:| -----------:|---------:|
| QuickBitSorter   | O(n * log(n)) | O(n * log(n))?      | O(n+k), k <= c | O(2^c)    | O(2^c)      |    1     |
| RadixBitSorter   | O(n * log(n)) | O(n * k), k<log2(n) | O(n), k = 1    | O(n)      | O(n)        |    1     |
| QuickBitSorterMT | O(n * log(n)) | O(n * log(n))?      | O(n+k), k <= c | O(t*2^c)  | O(2^c)      |    1     |
| MixedBitSorterMT | O(n * log(n)) | O(n * log(n))?      | O(n), k <= t   | O(n*t)    | O(n*t)      |  O(2^c)  |

## Things to DO
- Add Object sorting by key
- Add Int, Long and ULong sorters
- Evaluate Complexity
- More testing
