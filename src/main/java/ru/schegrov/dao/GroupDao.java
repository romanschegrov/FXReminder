package ru.schegrov.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.schegrov.entity.Group;
import ru.schegrov.entity.User;
import ru.schegrov.util.HibernateHelper;

/**
 * Created by ramon on 30.08.2016.
 */
public class GroupDao {

    private static final Logger logger = Logger.getLogger(GroupDao.class);

    public Group getGroupByCode(String code) {
        Session session = null;
        Group group = null;
        try {
            session = HibernateHelper.getSessionFactory().openSession();
            session.beginTransaction();
            Criteria criteria = session.createCriteria(Group.class);
            criteria.add(Restrictions.eq("code",code));
            group = (Group) criteria.uniqueResult();
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
        return group;
    }
}
