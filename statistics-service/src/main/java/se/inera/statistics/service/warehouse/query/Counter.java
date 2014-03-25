package se.inera.statistics.service.warehouse.query;

public class Counter<T> implements Comparable<Counter> {

    private final T key;
    private int count;

    public Counter(T key) {
        this.key = key;
    }

    public void increase() {
        count++;
    }

    @Override
    public int compareTo(Counter other) {
        if (count > other.count) {
            return -1;
        } else if (count == other.count) {
            return 0;
        } else {
            return 1;
        }
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return key + ":" + count;
    }

    public T getKey() {
        return key;
    }
}
