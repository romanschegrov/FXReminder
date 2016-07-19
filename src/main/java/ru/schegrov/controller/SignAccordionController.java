package ru.schegrov.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.exception.JDBCConnectionException;
import ru.schegrov.util.HibernateHelper;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SignAccordionController implements Initializable {
    private static final Logger logger = Logger.getLogger(SignAccordionController.class);
    private SessionFactory sessionFactory;
    private AppController root;

    @FXML
    private Text url;
    @FXML
    private Text error;
    @FXML
    private Button signin;
    @FXML
    private Button signout;
    @FXML
    private TextField username;
    @FXML
    private TextField password;

    public SignAccordionController(AppController root) {
        this.root = root;
        logger.info("init");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        url.setText(HibernateHelper.getInstance().getUrl());
        signout.setDisable(true);
        username.setText("RAMONHB");
        logger.info("initialized");
    }

    public void signin(ActionEvent actionEvent) {
        try {
            HibernateHelper.getInstance().setUsername(username.getText());
            HibernateHelper.getInstance().setPassword(password.getText());
            HibernateHelper.getInstance().getSessionFactory();

            signin.setDisable(true);
            signout.setDisable(false);
            root.expandedJobsPane();
        } catch (Exception e) {
            logger.error("Button signin error: ", e);
            while (e.getCause()!=null) e = (Exception) e.getCause();
            error.setText(e.getMessage());
            username.requestFocus();
        }
    }

    public void signout(ActionEvent actionEvent) {
        try {
            signin.setDisable(false);
            signout.setDisable(true);
        } catch (HibernateException e) {
            logger.error("Button signout error: ", e);
            error.setText(e.getMessage());
        }
    }
}
