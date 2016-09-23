package ru.schegrov.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.schegrov.entity.Job;
import ru.schegrov.entity.JobCondition;
import ru.schegrov.util.HibernateHelper;

/**
 * Created by ramon on 13.09.2016.
 */
public class ConditionDao {

    private static final Logger logger = Logger.getLogger(ConditionDao.class);

    public JobCondition getConditionByCodeAndValue(Job job, String code, String value) {
        Session session = null;
        JobCondition condition = null;
        try {
            session = HibernateHelper.getSessionFactory().openSession();
            session.beginTransaction();
            Criteria criteria = session.createCriteria(JobCondition.class);
            criteria.createAlias("job","job");
            criteria.add(Restrictions.eq("job.id",job.getId()));
            criteria.add(Restrictions.eq("code",code));
            criteria.add(Restrictions.eq("value",value));
            condition = (JobCondition) criteria.uniqueResult();
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
        return condition;
    }
}
