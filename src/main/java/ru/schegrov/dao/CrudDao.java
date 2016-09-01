package ru.schegrov.dao;

import ru.schegrov.entity.User;

import java.util.List;

/**
 * Created by ramon on 20.07.2016.
 */
public interface CrudDao<T>  {
    void add(T obj) throws Exception;
    void update(T obj) throws Exception;
    void delete(T obj) throws Exception;
    List<T> getAll() throws Exception;
    T getById(int id) throws Exception;
}
