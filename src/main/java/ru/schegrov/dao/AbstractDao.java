package ru.schegrov.dao;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import ru.schegrov.util.HibernateHelper;

public abstract class AbstractDao<T> implements GenericDao<T>  {

    private static final Logger logger = Logger.getLogger(AbstractDao.class);

    @Override
    public void add(T obj) throws Exception {
        Session session = null;
        try {
            session = HibernateHelper.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(obj);
            session.getTransaction().commit();
            logger.info("commit");
        } catch (Exception e){
            session.getTransaction().rollback();
            logger.info("rollback", e);
        } finally {
            if (session != null && session.isOpen()){
                session.close();
                logger.info("session closed");
            }
        }
    }
}
