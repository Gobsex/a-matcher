package io.github.gobsex.filter;

public interface Filter<T> {
    boolean filter(T element);
}
