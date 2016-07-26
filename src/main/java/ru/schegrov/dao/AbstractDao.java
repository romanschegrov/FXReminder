package ru.schegrov.dao;

import com.sun.javafx.fxml.expression.Expression;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.schegrov.model.Job;
import ru.schegrov.util.HibernateHelper;

import java.util.List;

public abstract class AbstractDao<T> implements GenericDao<T>  {

    private static final Logger logger = Logger.getLogger(AbstractDao.class);
    private Class<T> type;

    public AbstractDao(Class<T> type) {
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
