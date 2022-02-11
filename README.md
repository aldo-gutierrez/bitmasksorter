# bitsorter
This project tests different ideas for sorting algorithms
All of them use the notion of a bitmask as a way to get statistical information of the numbers

## QuickBitSorter
Is similar to a quicksort but for choosing the pivot we choose it using a mask of bits. 
And as we have a bit mask we can also use a flexible count sort for the last bits

This is different to other quicksort algorithms use other approaches as choosing the average of three pivots or to getting the average number as pivot

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
000000000000 -> 000001111111  ==>  the mask is 1111111 and to choosse a pivot we could choose 1000000 (64) that is probably a good pivot

For the counting sort part if the number of bits of the mask that remain to be evaluated are less than a constant we could do a count sort
even if the mask is for example: 1110011 we could use 5 bits 2^5 32 slots for count sort

Optimizations:
- Count sort if in the recursion the number of bits is small with flexible mask (it can contain 000 at any place)
- Optimization for small lists
- Multithreading support
- Doesn't need to sort all the bits just the ones used


## RadixBitSorter:

Usage:
```
//Sorting uint numbers 
IntSorter sorter = RadixBitSorter2UInt();
int[] list = ....
sorter.sort(list);
```
### How it works:

Is similar to the RadixBitSorter described at https://www.youtube.com/watch?v=_KhZ7F-jOlI
However it doesn't need to sort by all bits, just by the bits that are in the bit mask

Optimizations:
  Doesn't need to sort all the bits just the ones used
  It sorts by 8 bits at a time as maximum

## MixedBitSorter:
It combines in multithread first Bit Quicksort  then RadixBitSort and lastly Count Sort  

# Speed
See file Comparison.xlsx
Most of the algorithms are faster than the Java default Tim Sort and Parallel sort under some conditions

Example comparison for sorting 10 Million elements with range from 0 to 10 Million

- Elapsed JavaIntSorter AVG: 792
- Elapsed QuickBitSorter3UInt AVG: 347
- Elapsed RadixBitSorter2UInt AVG: 125
- Elapsed JavaParallelSorter AVG: 88
- Elapsed QuickBitSorterMTUInt AVG: 107
- Elapsed MixedBitSorterMTUInt AVG: 99

Other example:

- Elapsed JavaIntSorter AVG: 303
- Elapsed QuickBitSorter3UInt AVG: 21
- Elapsed RadixBitSorter2UInt AVG: 98
- Elapsed JavaParallelSorter AVG: 55
- Elapsed QuickBitSorterMTUInt AVG: 21
- Elapsed MixedBitSorterMTUInt AVG: 20

# O(N) Complexity. Needs to be evaluated


## Things to DO
- Add Object sorting by key
- Add Int, Long and ULong sorters
- Evaluate Complexity
- More testing
- MixedBitSort its in Alpha state, there are problems when countingSortBits is 4
