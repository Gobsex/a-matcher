package comparator;

public interface Comparator<R, E> {
    R compare(E source, E target);
}
