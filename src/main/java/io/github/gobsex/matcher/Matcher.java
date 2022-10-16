package io.github.gobsex.matcher;

import java.util.Collection;

public interface Matcher<E, RE> {
    RE find(E element, Collection<E> group);
}
