package ru.schegrov.dao;

import ru.schegrov.model.Job;

/**
 * Created by ramon on 20.07.2016.
 */
public interface GenericDao<T>  {
    void add(T obj) throws Exception;
}
