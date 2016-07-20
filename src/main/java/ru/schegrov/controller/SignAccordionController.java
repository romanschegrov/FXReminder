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
        logger.info("initialize");
        url.setText(HibernateHelper.getUrl());
        username.setText(HibernateHelper.getUsername());
        password.setText(HibernateHelper.getPassword());
        signout.setDisable(true);
        logger.info("initialized");
    }

    public void signin(ActionEvent actionEvent) {
        try {
            HibernateHelper.getSessionFactory(username.getText(), password.getText());
            signin.setDisable(true);
            signout.setDisable(false);
            error.setText(null);
            root.expandedJobsPane();
            logger.info("Connected");
        } catch (Exception e) {
            while (e.getCause()!=null) e = (Exception) e.getCause();
            error.setText(e.getMessage());
            username.requestFocus();
            logger.error("Button signin error: ", e);
        }
    }

    public void signout(ActionEvent actionEvent) {
        try {
            HibernateHelper.closeSessionFactory();
            signin.setDisable(false);
            signout.setDisable(true);
            logger.info("Disconnected");
        } catch (HibernateException e) {
            error.setText(e.getMessage());
            logger.error("Button signout error: ", e);
        }
    }
}
