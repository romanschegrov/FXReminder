package ru.schegrov.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.schegrov.dao.JobDao;
import ru.schegrov.dao.ObjectDao;
import ru.schegrov.dao.UserDao;
import ru.schegrov.entity.Group;
import ru.schegrov.entity.User;

import java.util.List;

public class HibernateHelper {

    private static final Logger logger = Logger.getLogger(HibernateHelper.class);
    private static final String url;
    private static final String username;
    private static final String password;
    private static final Configuration startConfigure;
    private static SessionFactory sessionFactory;
    private static User connectedUser;
    private static ObservableList<User> allUsers = FXCollections.observableArrayList();
    private static ObservableList<Group> allGroups = FXCollections.observableArrayList();

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

        if (username == null || username.isEmpty()) throw new Exception("username is empty");            ///!!!!!!!!!!!!!!!!!!!!
        if (password == null || password.isEmpty()) throw new Exception("password is empty");            ///////////////////////

        try {
            Configuration configure = new Configuration().configure("config/hibernate.cfg.xml");
            configure.setProperty("hibernate.connection.username", username);
            configure.setProperty("hibernate.connection.password", password);
            sessionFactory = configure.buildSessionFactory();

            UserDao dao = new UserDao();
            connectedUser = dao.getUserByUsername(username);
            if (connectedUser == null) throw new Exception("Пользователь не зарегистрирован в системе");
            if (connectedUser.getAdmin()){
                fillAllUsers();
                fillAllGroups();
            }
        } catch (/*Hibernate*/ Exception e){
            logger.error("buildSessionFactory error: ", e);
            throw new Exception(e);
        }
        return sessionFactory;
    }

    private static void fillAllUsers() throws Exception {
        ObjectDao<User> dao = new ObjectDao<>(User.class);
        List<User> list = dao.getAll();
        list.forEach(user -> allUsers.add(user));
    }

    private static void fillAllGroups() throws Exception {
        ObjectDao<Group> dao = new ObjectDao<>(Group.class);
        List<Group> list = dao.getAll();
        list.forEach(group -> allGroups.add(group));
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

    public static User getConnectedUser() {
        return connectedUser;
    }

    public static ObservableList<User> getAllUsers() {
        return allUsers;
    }

    public static ObservableList<Group> getAllGroups() {
        return allGroups;
    }
}
