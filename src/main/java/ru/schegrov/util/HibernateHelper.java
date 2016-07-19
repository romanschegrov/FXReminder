package ru.schegrov.util;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

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
            instance = new HibernateHelper();
        }
        return instance;
    }

    public SessionFactory getSessionFactory () throws Exception{
        if (sessionFactory == null || sessionFactory.isClosed()) {

            if (username == null || username.isEmpty()) throw new Exception("username is empty");
            if (password == null || password.isEmpty()) throw new Exception("password is empty");

            try {
                configure.setProperty("hibernate.connection.username", username);
                configure.setProperty("hibernate.connection.password", password);

                sessionFactory = configure.buildSessionFactory();
            } catch (HibernateException e){
                logger.error("buildSessionFactory error: ", e);
                throw new HibernateException(e);
            }
        }
        return sessionFactory;
    }

    public void closeSessionFactory(){
        try {
            sessionFactory.close();
        } catch (Exception e) {
            logger.error("closeSessionFactory error: ", e);
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrl(){
        return configure.getProperty("connection.url");
    }
}
