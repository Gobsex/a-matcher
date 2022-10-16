package io.github.gobsex.comparators;

public interface Comparator<R, E> {
    R compare(E source, E target);
}
