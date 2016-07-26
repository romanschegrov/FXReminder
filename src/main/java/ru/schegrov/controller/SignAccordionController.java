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

    public SignAccordionController(AppController root) {
        this.root = root;
        logger.info("init");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("initialize");
        logger.info("initialized");
    }

    public void signin(ActionEvent actionEvent) {
    }

    public void signout(ActionEvent actionEvent) {
    }
}
