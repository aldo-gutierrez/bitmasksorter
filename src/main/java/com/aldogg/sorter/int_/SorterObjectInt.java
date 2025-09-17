package com.aldogg.sorter.int_;

import com.aldogg.sorter.FieldSortOptions;
import com.aldogg.sorter.int_.object.IntegerMapper;
import com.aldogg.sorter.int_.object.IntMapper;
import com.aldogg.sorter.shared.NullHandling;
import com.aldogg.sorter.shared.SorterObjectType;

import java.util.*;

import static com.aldogg.sorter.shared.NullHandling.*;
import static com.aldogg.sorter.shared.SorterObjectUtils.nullFirst;
import static com.aldogg.sorter.shared.SorterObjectUtils.nullLast;

public interface SorterObjectInt<T> extends SorterObjectType<T, Integer> {

    void sortNNA(T[] array, IntMapper<T> mapper, int start, int endP1, FieldSortOptions fieldSortOptions);

    default void sort(List<T> list, IntMapper<T> mapper) {
        sort(list, mapper, 0, list.size(), getDefaultFieldSortOptions());
    }

    default void sort(List<T> list, IntegerMapper<T> mapper) {
        sort(list, mapper, 0, list.size(), getDefaultFieldSortOptions());
    }

    default void sort(T[] array, IntMapper<T> mapper) {
        sort(array, mapper, 0, array.length, getDefaultFieldSortOptions());
    }

    default void sort(List<T> list, IntMapper<T> mapper, int start, int endP1, FieldSortOptions fieldSortOptions) {
        NullHandling collectionNullHandling = getCollectionOptions().getNullHandling() != null ? getCollectionOptions().getNullHandling() : fieldSortOptions.getNullHandling();
        int nulls = 0;
        List<T> subList = start == 0 && endP1 == list.size() ? list : list.subList(start, endP1);
        for (T value : subList) {
            if (value == null) {
                nulls++;
            }
        }
        if (UNKNOWN.equals(collectionNullHandling) && nulls > 0) {
            throw new NullPointerException("Null found in Collection please specify option NullHandling.NULLS_FIRST or NullHandling.NULLS_LAST");
        }
        int n = endP1 - start - nulls;
        T[] array = (T[]) new Object[n];
        int j = 0;
        if (nulls > 0) {
            for (T value : subList) {
                if (value != null) {
                    array[j] = value;
                    j++;
                }
            }
        } else {
            for (T value : subList) {
                array[j] = value;
                j++;
            }
        }
        sortNNA(array, mapper, 0, n, fieldSortOptions);
        j = 0;
        ListIterator<T> iterator = subList.listIterator();
        if (nulls > 0) {
            if (NULLS_FIRST.equals(collectionNullHandling)) {
                while (nulls > 0) {
                    iterator.next();
                    iterator.set(null);
                    nulls--;
                }
            }
            while (j < n) {
                iterator.next();
                iterator.set(array[j]);
                j++;
            }
            if (NULLS_LAST.equals(collectionNullHandling)) {
                while (nulls > 0) {
                    iterator.next();
                    iterator.set(null);
                    nulls--;
                }
            }
        } else {
            while (iterator.hasNext()) {
                iterator.next();
                iterator.set(array[j]);
                j++;
            }
        }
    }

    default void sort(List<T> list, IntegerMapper<T> mapper, int start, int endP1, FieldSortOptions fieldSortOptions) {
        NullHandling collectionNullHandling = getCollectionOptions().getNullHandling() != null ? getCollectionOptions().getNullHandling() : fieldSortOptions.getNullHandling();
        int nulls = 0;
        int nullFields = 0;
        List<T> subList = start == 0 && endP1 == list.size() ? list : list.subList(start, endP1);
        for (T value : subList) {
            if (value == null) {
                nulls++;
            } else {
                if (mapper.valueOf(value) == null) {
                    nullFields++;
                }
            }
        }
        if (UNKNOWN.equals(collectionNullHandling) && nulls > 0) {
            throw new NullPointerException("Null found in Collection please specify option NullHandling.NULLS_FIRST or NullHandling.NULLS_LAST");
        }
        if (UNKNOWN.equals(fieldSortOptions.getNullHandling()) && nullFields > 0) {
            throw new NullPointerException("Null found in Collection please specify option NullHandling.NULLS_FIRST or NullHandling.NULLS_LAST");
        }
        int n = endP1 - start - nulls;
        T[] array = (T[]) new Object[n];
        int j = 0;
        if (nulls == 0 && nullFields == 0) {
            for (T value : subList) {
                array[j] = value;
                j++;
            }
        } else {
            if (nullFields == 0) {
                for (T value : subList) {
                    if (value != null) {
                        array[j] = value;
                        j++;
                    }
                }
            } else {
                int jn;
                if (NULLS_FIRST.equals(fieldSortOptions.getNullHandling()) && nullFields > 0) {
                    j = nullFields;
                    jn = 0;
                } else { //NULLS_LAST
                    j = 0;
                    jn = n - nullFields;
                }
                for (T value : subList) {
                    if (value != null) {
                        if (mapper.valueOf(value) != null) {
                            array[j] = value;
                            j++;
                        } else {
                            array[jn] = value;
                            jn++;
                        }
                    }
                }
            }
        }
        if (nullFields == 0) {
            sortNNA(array, mapper, 0, n, fieldSortOptions);
        } else {
            if (NULLS_FIRST.equals(fieldSortOptions.getNullHandling()) && nullFields > 0) {
                sortNNA(array, mapper, nullFields, n, fieldSortOptions);
            } else { //NULLS_LAST
                sortNNA(array, mapper, 0, n - nullFields, fieldSortOptions);
            }
        }
        j = 0;
        ListIterator<T> iterator = subList.listIterator();
        if (nulls > 0) {
            if (NULLS_FIRST.equals(collectionNullHandling)) {
                while (nulls > 0) {
                    iterator.next();
                    iterator.set(null);
                    nulls--;
                }
            }
            while (j < n) {
                iterator.next();
                iterator.set(array[j]);
                j++;
            }
            if (NULLS_LAST.equals(collectionNullHandling)) {
                while (nulls > 0) {
                    iterator.next();
                    iterator.set(null);
                    nulls--;
                }
            }
        } else {
            while (iterator.hasNext()) {
                iterator.next();
                iterator.set(array[j]);
                j++;
            }
        }
    }

    default void sort(T[] list, IntMapper<T> mapper, int start, int endP1, FieldSortOptions fieldSortOptions) {
        NullHandling collectionNullHandling = getCollectionOptions().getNullHandling() != null ? getCollectionOptions().getNullHandling() : fieldSortOptions.getNullHandling();
        int nulls;
        if (NULLS_LAST.equals(collectionNullHandling)) {
            nulls = nullLast(list, start, endP1);
            sortNNA(list, mapper, 0, endP1 - nulls, fieldSortOptions);
        } else if (NULLS_FIRST.equals(collectionNullHandling)) {
            nulls = nullFirst(list, start, endP1);
            sortNNA(list, mapper, start + nulls, endP1, fieldSortOptions);
        } else {
            if (UNKNOWN.equals(collectionNullHandling) || collectionNullHandling == null) {
                for (int i = start; i < endP1; i++) {
                    T value = list[i];
                    if (value == null) {
                        throw new NullPointerException("Null found in Collection please specify option NullHandling.NULLS_FIRST or NullHandling.NULLS_LAST");
                    }
                }
            }
            sortNNA(list, mapper, 0, endP1, fieldSortOptions);
        }
    }

    default void sort(T[] list, IntegerMapper<T> mapper, int start, int endP1, FieldSortOptions fieldSortOptions) {
        NullHandling collectionNullHandling = getCollectionOptions().getNullHandling() != null ? getCollectionOptions().getNullHandling() : fieldSortOptions.getNullHandling();
        int nulls;
        if (NULLS_LAST.equals(collectionNullHandling)) {
            nulls = nullLast(list, start, endP1);
            // nullLastFields(list, start, endP1, mapper);
            // nullFirstFields(list, start, endP1, mapper);
            sortNNA(list, mapper, 0, endP1 - nulls, fieldSortOptions);
        } else if (NULLS_FIRST.equals(collectionNullHandling)) {
            nulls = nullFirst(list, start, endP1);
            // nullLastFields(list, start, endP1, mapper);
            // nullFirstFields(list, start, endP1, mapper);
            sortNNA(list, mapper, start + nulls, endP1, fieldSortOptions);
        } else {
            if (NO_NULLS_HANDLING.equals(collectionNullHandling) && NO_NULLS_HANDLING.equals(fieldSortOptions.getNullHandling())) {
                sortNNA(list, mapper, 0, endP1, fieldSortOptions);
            } else {
                for (int i = start; i < endP1; i++) {
                    T value = list[i];
                    if (value == null) {
                        if (UNKNOWN.equals(collectionNullHandling) || collectionNullHandling == null) {
                            throw new NullPointerException("Null found in Collection please specify option NullHandling.NULLS_FIRST or NullHandling.NULLS_LAST");
                        }
                    } else if (mapper.valueOf(value) == null) {
                        if (UNKNOWN.equals(fieldSortOptions.getNullHandling()) || fieldSortOptions.getNullHandling() == null) {
                            throw new NullPointerException("Null found in Collection please specify option NullHandling.NULLS_FIRST or NullHandling.NULLS_LAST");
                        }
                    }
                }
            }
        }
    }

}
