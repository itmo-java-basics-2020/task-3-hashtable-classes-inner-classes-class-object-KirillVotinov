package ru.itmo.java;

import java.util.Arrays;

public class HashTable {

    private static final int DEFAULT_CAPACITY = 1000;

    private static final double DEFAULT_LOAD_FACTOR = 0.5;

    private static final int DEFAULT_GAP = 113;

    private final double loadFactor;

    private int threshold;

    private int size = 0;

    private Entry[] array;

    private boolean[] deleted;

    public HashTable() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(double initialLoadFactor) {
        this(DEFAULT_CAPACITY, initialLoadFactor);
    }

    public HashTable(int initialCapacity, double initialLoadFactor) {
        this.array = new Entry[initialCapacity];
        this.deleted = new boolean[initialCapacity];

        if (initialLoadFactor > 1 || initialLoadFactor <= 0) {
            initialLoadFactor = DEFAULT_LOAD_FACTOR;
        }
        this.loadFactor = initialLoadFactor;
        this.threshold = (int) (this.loadFactor * initialCapacity);
    }

    private int getIndexInTable(Object key) {
        int hash = (key.hashCode() % array.length + array.length) % array.length;

        while (deleted[hash] || array[hash] != null && !key.equals(array[hash].key)) {
            hash = (hash + DEFAULT_GAP) % array.length;
        }

        return hash;

    }

    private int getIndexToPutToTable(Object key) {
        int arraySize = array.length;
        int hash = (key.hashCode() % arraySize + arraySize) % arraySize;

        while (array[hash] != null) {
            hash = (hash + DEFAULT_GAP) % arraySize;
        }

        return hash;
    }

    public Object put(Object key, Object value) {
        Entry newElement = new Entry(key, value);

        int index = getIndexInTable(key);

        if (array[index] == null) {

            index = getIndexToPutToTable(key);

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

    public Object get(Object key) {
        int index = getIndexInTable(key);

        if (array[index] == null) {
            return null;
        }

        return array[index].value;
    }

    public Object remove(Object key) {
        int index = getIndexInTable(key);

        if (array[index] == null) {
            return null;
        }

        deleted[index] = true;
        Entry deletedElement = array[index];
        size--;
        array[index] = null;

        return deletedElement.value;
    }

    public int size() {
        return this.size;
    }

    private void resize() {
        Entry[] oldArray = array;
        int newArraySize = oldArray.length * 2;
        array = new Entry[newArraySize];
        deleted = new boolean[newArraySize];
        threshold = (int) (loadFactor * newArraySize);
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
        private Object key;

        private Object value;

        public Entry(Object initialKey, Object initialValue) {
            this.key = initialKey;
            this.value = initialValue;
        }

        public Object getKey() {return this.key;}

        public Object getValue() {return this.value;}

        @Override
        public String toString() {
            return String.format("key=%s, value=%s", key.toString(), value.toString());
        }
    }

}