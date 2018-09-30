package fusionkey.lowkey.newsfeed.interfaces;

public interface IGenericConsumer<T> {
    void consume(T item);
}
