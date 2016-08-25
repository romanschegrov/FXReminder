package ru.schegrov.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.schegrov.entity.User;
import ru.schegrov.util.HibernateHelper;

import java.util.List;

/**
 * Created by ramon on 25.08.2016.
 */
public class UserDao {

    private static final Logger logger = Logger.getLogger(UserDao.class);

    public User getUserByUsername(String username) {
        Session session = null;
        User user = null;
        try {
            session = HibernateHelper.getSessionFactory().openSession();
            session.beginTransaction();
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("code",username));
            user = (User) criteria.uniqueResult();
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
        return user;
    }
}
