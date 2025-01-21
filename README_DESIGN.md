

List<Example> exampleList = ....

SorterBuilder sorterBuilder = new SorterBuilder(exampleList);
sorterBuilder.range(5, 20);



defaults
--------
range  (default 0..N-1)
unsigned (default false)
stable (default true for objects and false for primitives and boxed primitives)
direction (default asc)
cpuMEM (default FAST), ZERO_MEM, LOW_MEM 
typeOfData (default UNKNOWN), RANDOM, ALMOST_SORTED, SORTED_IN_BLOCKS
  DISTRIBUTED -> skip detection and skip detection of blocks
  SORTED_IN_BLOCKS -> 

sorter = new RSorter<T>(list, start, end)

sorter.builder()
   .nullFirst(),
   .sortByInt(x->x.id).asc()
   .sortByInteger(x -> x.id2).nullFirst().asc()
   .sortByInt(x->x.id3).unsigned().desc().
   .sortByLong(x->x.longId).asc()
   .sortByString(x->name).nullFirst().asc()
   .unstable()
   .cpuMem(ZERO_MEM)
   .algorithm(RADIX_SORT) 
   .sort();


sorter.builder()
   .nullFirst(),
   .unsigned()
   .asc()
   .stable() //optional maybe remove
   .cpuMEM()
   .sort()



sorterX.setStable(false);
sorterX.setNull(NULL_FIRST);
sorterX.sortAsc(list, x-> x.id)
sorterX.sortAsc(list, x-> x.id, start, end)
fieldParameters.setUnsigned(true);
fieldParameters.setStable(true)
fieldParameters.setAsc(false);
sorterX.sortAsc(list, x-> x.id, start, end, fieldParameters)




sorter.sortAsc(list, start, end)


sorter.sortAsc(list, start, end)

sorter.sortDesc(list, start, end)

options.setUnsigned(true)
options.setStable(false) //maybe removew

sorter.sortAsc(list, start, end, options)

