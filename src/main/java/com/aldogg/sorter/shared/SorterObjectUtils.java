package com.aldogg.sorter.shared;

import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

public class SorterObjectUtils {

    public static <T> int nullLast(T[] list, int start, int endP1) {
        int nulls = 0;
        int j = start;
        for (int i = start; i < endP1; i++) {
            T value = list[i];
            if (value == null) {
                nulls++;
            } else {
                list[j] = value;
                j++;
            }
        }
        for (; j < endP1; j++) {
            list[j] = null;
        }
        return nulls;
    }

    public static <T> int nullLast(List<T> list, int start, int endP1) {
        //RandomAccess list
        if (list instanceof RandomAccess) {
            int nulls = 0;
            int j = start;
            for (int i = start; i < endP1; i++) {
                T value = list.get(i);
                if (value == null) {
                    nulls++;
                } else {
                    list.set(j, value);
                    j++;
                }
            }
            for (; j < endP1; j++) {
                list.set(j, null);
            }
            return nulls;
        } else {
            List<T> subList = start == 0 && endP1 == list.size() ? list : list.subList(start, endP1);
            //Linked List
            int nulls = 0;
            ListIterator<T> listIterator1 = subList.listIterator();
            ListIterator<T> listIteratorJ = subList.listIterator();
            listIteratorJ.next();
            while (listIterator1.hasNext()) {
                T value = listIterator1.next();
                if (value == null) {
                    nulls++;
                } else {
                    listIteratorJ.set(value);
                    listIteratorJ.next();
                }
            }
            while (listIteratorJ.hasNext()) {
                listIteratorJ.set(null);
                listIteratorJ.next();
            }
            return nulls;
        }
    }

    public static <T> int nullFirst(T[] list, int start, int endP1) {
        int nulls = 0;
        int j = endP1 - 1;
        for (int i = endP1 - 1; i >= start; i--) {
            T value = list[i];
            if (value == null) {
                nulls++;
            } else {
                list[j] = value;
                j--;
            }
        }
        for (; j >= start; j--) {
            list[j] = null;
        }
        return nulls;
    }

    public static <T> int nullFirst(List<T> list, int start, int endP1) {
        //RandomAccess list
        if (list instanceof RandomAccess) {
            int nulls = 0;
            int j = endP1 - 1;
            for (int i = endP1 - 1; i >= start; i--) {
                T value = list.get(i);
                if (value == null) {
                    nulls++;
                } else {
                    list.set(j, value);
                    j--;
                }
            }
            for (; j >= start; j--) {
                list.set(j, null);
            }
            return nulls;
        } else {
            List<T> subList = start == 0 && endP1 == list.size() ? list : list.subList(start, endP1);
            //Linked List
            int nulls = 0;
            int n = endP1 - start;
            ListIterator<T> listIterator = subList.listIterator(n);
            ListIterator<T> listIteratorJ = subList.listIterator(n);
            listIteratorJ.next();
            while (listIterator.hasPrevious()) {
                T value = listIterator.previous();
                if (value == null) {
                    nulls++;
                } else {
                    listIteratorJ.set(value);
                    listIteratorJ.previous();
                }
            }
            while (listIteratorJ.hasPrevious()) {
                listIteratorJ.set(null);
                listIteratorJ.previous();
            }
            return nulls;
        }
    }


}
