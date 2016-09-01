package ru.schegrov.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.schegrov.entity.Job;
import ru.schegrov.util.HibernateHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by ramon on 20.07.2016.
 */
public class JobDao {

    private static final Logger logger = Logger.getLogger(JobDao.class);

    public List<Map<String,Object>> getAllRows(String sql) {
        Session session = null;
        List<Map<String,Object>> list = null;
        try {
            session = HibernateHelper.getSessionFactory().openSession();
            session.beginTransaction();
            SQLQuery sqlQuery = session.createSQLQuery(sql);
            list = sqlQuery
                    .setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
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

    public List<Job> getAllByParentId(int id) {
        Session session = null;
        List list = null;
        try {
            session = HibernateHelper.getSessionFactory().openSession();
            session.beginTransaction();
            list  = session.createCriteria(Job.class)
                    .add(Restrictions.eq("parent_id",id))
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
