package app.dao;

import java.util.Set;

public interface IDAO<T> {

    T create(T t);

    T getById(Integer id);

    Set<T> getAll();

    T update(T t);

    void delete(Integer id);
}
