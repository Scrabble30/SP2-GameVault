package app.dao;

import java.util.Set;

public interface IDAO<T, I> {

    T create(T t);

    T getById(I id);

    Set<T> getAll();

    T update(I id, T t);

    void delete(I id);
}
