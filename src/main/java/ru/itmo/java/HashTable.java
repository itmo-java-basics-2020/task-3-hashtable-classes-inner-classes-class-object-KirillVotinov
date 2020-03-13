package ru.itmo.java;

import java.util.Arrays;


public class HashTable {

    private static final int DEFAULT_CAPACITY = 1000;

    private static final double DEFAULT_LOAD_FACTOR = 0.5;

    private static final int GAP = 307;

    private final double loadFactor;

    private int threshold;

    private int size = 0;

    private Entry[] array;

    private boolean[] deleted;

    HashTable() {
        this(DEFAULT_CAPACITY);
    }

    HashTable(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    HashTable(double loadFactor) {
        this(DEFAULT_CAPACITY, loadFactor);
    }

    HashTable(int initialCapacity, double initialLoadFactor) {

        this.array = new Entry[initialCapacity];
        this.deleted = new boolean[initialCapacity];

        if (initialLoadFactor > 1 || initialLoadFactor <= 0) {
            initialLoadFactor = DEFAULT_LOAD_FACTOR;
        }
        this.loadFactor = initialLoadFactor;
        this.threshold = (int) (this.loadFactor * initialCapacity);
    }

    int indexInTable(Object key) {

        int hash = (key.hashCode() % array.length + array.length) % array.length;

        while (deleted[hash] || array[hash] != null && !key.equals(array[hash].key)) {
            hash = (hash + GAP) % array.length;
        }

        return hash;
    }

    int indexToPutToTable(Object key) {

        int hash = (key.hashCode() % array.length + array.length) % array.length;

        while (array[hash] != null) {
            hash = (hash + GAP) % array.length;
        }

        return hash;
    }

    Object put(Object key, Object value) {

        Entry newElement = new Entry(key, value);

        int index = indexInTable(key);

        if (array[index] == null) {

            index = indexToPutToTable(key);

            if (deleted[index]) {
                deleted[index] = false;
            }

            array[index] = newElement;
            size++;

            if (size >= threshold) {
                this.resize();
            }

            return null;
        }

        Entry oldElement = array[index];
        array[index] = newElement;

        return oldElement.value;
    }

    Object get(Object key) {

        int index = indexInTable(key);

        if (array[index] == null) {
            return null;
        }

        return array[index].value;
    }

    Object remove(Object key) {

        int index = indexInTable(key);

        if (array[index] == null) {
            return null;
        }

        deleted[index] = true;
        Entry deletedElement = array[index];
        size--;
        array[index] = null;

        return deletedElement.value;
    }

    int size() {
        return this.size;
    }

    void resize() {

        var oldArray = array;

        array = new Entry[oldArray.length * 2];
        deleted = new boolean[array.length];
        threshold = (int) (loadFactor * oldArray.length * 2);
        size = 0;

        for (Entry element : oldArray) {
            if (element != null) {
                this.put(element.key, element.value);
            }
        }


    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }

    private static class Entry {

        Object key;

        Object value;

        Entry(Object initialKey, Object initialValue) {
            this.key = initialKey;
            this.value = initialValue;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("key=");
            sb.append(key).append(", value=").append(value);
            return sb.toString();
        }
    }

}