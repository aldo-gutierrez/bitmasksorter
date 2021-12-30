# bitsorter
This project adds Bit Sorter and Radix Bit Sorter with some optimizations

## BitSorter

Usage:
```
IntSorter sorter = new BitSorterUIntOptimized();
int[] list = ....
sorter.sort(list);
```
```
IntSorter sorter = new BitSorterUIntMT();
int[] list = ....
sorter.sort(list);
```

### How it works:
Is similar to a quicksort but for choosing the pivot we choose using a mask of bits. And as we have a bit mask we can also use a flexible count sort 

There are other aproaches as choosing the average of three pivots or to getting the average number as pivot but here we are using  a bit mask

For example suppose the list contains numbers from 0 to 127 
000000000000 -> 000001111111  ==>  the mask is 1111111 and to choosse a pivot we could choose 1000000 (64) that is probably a good pivot

For the counting sort part if the numer of bits of the mask that remain to be evaluated are less than a constant we could do a count sort
even if the mask is for example: 1110011 we could use 5 bits 2^5 32 slots for count sort

Optimizations:
- Count sort if in the recursion the number of bits is small with flexible mask (it can contain 000 at any place)
- Optimization for small lists < 5
- Multithreading support
- Doesn't need to sort all the bits just the ones used


## RadixBitSorter:

Usage:
```
IntSorter sorter = RadixBitUIntSorter3();
int[] list = ....
sorter.sort(list);
```
### How it works:

Is similar to the RadixBitSorter described in https://www.youtube.com/watch?v=_KhZ7F-jOlI
However it doesn't need to sort by all bits, just by the bits that are in the bit mask

Optimizations:
  Doesn't need to sort all the bits just the ones used

## Things to DO
- Add Object sorting by key
- Add Int sorters Long sorters and ULong sorters

## Speed
- BitSorterUIntOptimized and BitSorterUIntMT is fastests always than JavaSorter
- RadixBitUIntSorter3 is almost always faster than JavaSorter
- BitSorterUIntMT is most of the time faster than JavaSorter or near the same speed

## O(N) Complexity. Needs to be evaluated


