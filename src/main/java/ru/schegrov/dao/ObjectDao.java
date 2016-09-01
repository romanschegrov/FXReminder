package ru.schegrov.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.schegrov.entity.User;
import ru.schegrov.util.HibernateHelper;

import java.util.List;

public class ObjectDao<T> implements CrudDao<T>  {

    private static final Logger logger = Logger.getLogger(ObjectDao.class);
    private Class<T> type;
    private enum Action {ADD, UPDATE, DELETE};

    public ObjectDao(Class<T> type) {
        this.type = type;
    }

    @Override
    public void add(T obj) throws Exception {
        action(obj, Action.ADD);
    }

    @Override
    public void update(T obj) throws Exception {
        action(obj, Action.UPDATE);
    }

    @Override
    public void delete(T obj) throws Exception {
        action(obj, Action.DELETE);
    }

    private void action(T obj, Action action) throws Exception {
        Session session = null;
        try {
            session = HibernateHelper.getSessionFactory().openSession();
            session.beginTransaction();
            switch (action){
                case ADD:
                    session.save(obj);
                    break;
                case UPDATE:
                    session.saveOrUpdate(obj);
                    break;
                case DELETE:
                    session.delete(obj);
                    break;
                default:
                    break;
            }
            session.getTransaction().commit();
            logger.info("commit");
        } catch (Exception e){
            session.getTransaction().rollback();
            logger.error("rollback", e);
            while (e.getCause() != null) e = (Exception) e.getCause();
            throw new Exception(e.getMessage());
        } finally {
            if (session != null && session.isOpen()){
                session.close();
                logger.info("session closed");
            }
        }
    }

    @Override
    public T getById(int id) throws Exception {
        Session session = null;
        T obj = null;
        try {
            session = HibernateHelper.getSessionFactory().openSession();
            session.beginTransaction();
            Criteria criteria = session.createCriteria(type);
            criteria.add(Restrictions.eq("id",id));
            obj = (T) criteria.uniqueResult();
            session.getTransaction().commit();
            logger.info("commit");
        } catch (Exception e){
            session.getTransaction().rollback();
            logger.error("rollback", e);
            while (e.getCause() != null) e = (Exception) e.getCause();
            throw new Exception(e.getMessage());
        } finally {
            if (session != null && session.isOpen()){
                session.close();
                logger.info("session closed");
            }
        }
        return obj;
    }

    @Override
    public List<T> getAll() {
        Session session = null;
        List list = null;
        try {
            session = HibernateHelper.getSessionFactory().openSession();
            session.beginTransaction();
            list = session.createCriteria(type)
                    .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                    .list();
            session.getTransaction().commit();
            logger.info("commit");
        } catch (Exception e){
            session.getTransaction().rollback();
            logger.error("rollback", e);
        } finally {
            if (session != null && session.isOpen()){
                session.close();
                logger.info("session closed");
            }
        }
        return list;
    }
}
