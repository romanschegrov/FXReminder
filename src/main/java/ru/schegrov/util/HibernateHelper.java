package ru.schegrov.util;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

/**
 * Created by ramon on 11.07.2016.
 */
public class HibernateHelper {

    private static final Logger logger = Logger.getLogger(HibernateHelper.class);
    private static HibernateHelper instance;
    private SessionFactory sessionFactory;

    private String username;
    private String password;

    private Configuration configure;

    private HibernateHelper() {
        try {
            configure = new Configuration().configure("config/hibernate.cfg.xml");
        } catch (HibernateException e) {
            logger.error("Init HibernateHelper error", e);
        }
        logger.info("init");
    }

    public static HibernateHelper getInstance() {
        if (instance == null) {
            return new HibernateHelper();
        }
        return instance;
    }

    public SessionFactory getSessionFactory(String username, String password) {
        if (sessionFactory == null) {
            configure.setProperty("hibernate.connection.username", username);
            configure.setProperty("hibernate.connection.password", password);

            this.sessionFactory = configure.buildSessionFactory();
            this.username = username;
            this.password = password;
        }
        return sessionFactory;
    }

    public String getUrl(){
        return configure.getProperty("connection.url");
    }
}
