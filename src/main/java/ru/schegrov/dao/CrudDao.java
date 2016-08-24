package ru.schegrov.dao;

import java.util.List;

/**
 * Created by ramon on 20.07.2016.
 */
public interface CrudDao<T>  {
    void add(T obj) throws Exception;
    List<T> getAll();
    List<T> getAllByParentId(int id);
}
