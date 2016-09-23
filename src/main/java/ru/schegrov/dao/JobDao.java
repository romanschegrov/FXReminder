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
        List<Job> list = null;
        String sql =
                "SELECT DISTINCT j.*\n" +
                "  FROM T_FXR_JOBS j, T_FXR_CONDITIONS c\n" +
                " WHERE 1=1\n" +
                "   AND j.PARENT_ID = :parent_id\n" +
                "   AND c.JOB_ID=j.ID\n" +
                "   AND c.CODE='AVAILABLE'\n" +
                "   AND c.VALUE =:username\n" +
                " UNION --доступ к заданиям через группу\n" +
                "SELECT j.*\n" +
                "  FROM T_FXR_JOBS j, T_FXR_CONDITIONS c, T_FXR_GROUP g, T_FXR_UG ug, T_FXR_USERS u\n" +
                " WHERE 1=1\n" +
                "   AND j.PARENT_ID = :parent_id\n" +
                "   AND c.JOB_ID=j.id\n" +
                "   AND c.code='AVAILABLE'\n" +
                "   AND g.CODE=c.value\n" +
                "   AND ug.GROUP_ID=g.ID\n" +
                "   AND u.ID = ug.USER_ID\n" +
                "   AND u.CODE = :username\n" +
                " UNION --доступ администраторам ко всем заданиям\n" +
                "SELECT j.*\n" +
                "  FROM T_FXR_JOBS j, T_FXR_USERS u\n" +
                " WHERE 1=1\n" +
                "   AND j.PARENT_ID = :parent_id\n" +
                "   AND u.CODE = :username\n" +
                "   AND u.ADMIN='1'";
        try {
            session = HibernateHelper.getSessionFactory().openSession();
            session.beginTransaction();
            SQLQuery sqlQuery = session.createSQLQuery(sql);
            sqlQuery.addEntity(Job.class);
            sqlQuery.setParameter("username", HibernateHelper.getConnectedUser().getCode());
            sqlQuery.setParameter("parent_id", id);
            list  = sqlQuery.list();
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
