package ru.schegrov.util;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateHelper {

    private static final Logger logger = Logger.getLogger(HibernateHelper.class);
    private static final String url;
    private static final String username;
    private static final String password;
    private static final Configuration startConfigure;
    private static SessionFactory sessionFactory;

    static {
        try {
            logger.info("start static block");
            startConfigure = new Configuration().configure("config/hibernate.cfg.xml");
            url = startConfigure.getProperty("connection.url");
            username = startConfigure.getProperty("hibernate.connection.username");
            password = startConfigure.getProperty("hibernate.connection.password");
            logger.info("init static block");
        } catch (Exception e) {
            logger.error("Init HibernateHelper error", e);
            throw new HibernateException(e);
        }
    }

    private HibernateHelper() {}

    public static SessionFactory getSessionFactory (String username, String password) throws Exception{

        logger.info("start getSessionFactory");

        if (username == null || username.isEmpty()) throw new Exception("username is empty");
        if (password == null || password.isEmpty()) throw new Exception("password is empty");

        try {
            Configuration configure = new Configuration().configure("config/hibernate.cfg.xml");
            configure.setProperty("hibernate.connection.username", username);
            configure.setProperty("hibernate.connection.password", password);
            sessionFactory = configure.buildSessionFactory();
        } catch (HibernateException e){
            logger.error("buildSessionFactory error: ", e);
            throw new Exception(e);
        }
        return sessionFactory;
    }

    public static SessionFactory getSessionFactory () throws Exception{
        logger.info("start getSessionFactory");
        if (sessionFactory == null || sessionFactory.isClosed()) {
            Exception exception = new Exception("sessionFactory is null or closed");
            logger.error("getSessionFactory error: ", exception);
            throw exception;
        }
        return sessionFactory;
    }

    public static void closeSessionFactory(){
        if (sessionFactory != null) {
            try {
                sessionFactory.close();
                logger.info("closed SessionFactory");
            } catch (Exception e) {
                logger.error("closeSessionFactory error: ", e);
            }
        }
    }

    public static String getUrl(){
        return url;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }
}
