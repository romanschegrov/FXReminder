package ru.schegrov.dao;

import ru.schegrov.model.Job;

/**
 * Created by ramon on 20.07.2016.
 */
public class JobDaoImpl extends AbstractDao<Job> {
    public JobDaoImpl() {
        super(Job.class);
    }
}
