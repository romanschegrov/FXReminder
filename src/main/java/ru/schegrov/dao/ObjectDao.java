package ru.schegrov.dao;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.schegrov.util.HibernateHelper;

import java.util.List;

public class ObjectDao<T> implements CrudDao<T>  {

    private static final Logger logger = Logger.getLogger(ObjectDao.class);
    private Class<T> type;

    public ObjectDao(Class<T> type) {
        this.type = type;
    }

    @Override
    public void add(T obj) {
        Session session = null;
        try {
            session = HibernateHelper.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(obj);
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
    }

    @Override
    public List<T> getAll() {
        Session session = null;
        List list = null;
        try {
            session = HibernateHelper.getSessionFactory().openSession();
            session.beginTransaction();
            list = session.createCriteria(type)
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

    @Override
    public List<T> getAllByParentId(int id) {
        Session session = null;
        List list = null;
        try {
            session = HibernateHelper.getSessionFactory().openSession();
            session.beginTransaction();
            list  = session.createCriteria(type)
                    .add(Restrictions.eq("parent_id",id))
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
