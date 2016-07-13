package ru.schegrov.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;
import ru.schegrov.util.HibernateHelper;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ramon on 06.07.2016.
 */
public class SignAccordionController implements Initializable {
    private static final Logger logger = Logger.getLogger(SignAccordionController.class);

    @FXML
    private Text url;
    @FXML
    private Button signin;
    @FXML
    private Button signout;


    public SignAccordionController() {
        logger.info("init");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        url.setText(HibernateHelper.getInstance().getUrl());
        logger.info("initialized");
    }

    public void signin(ActionEvent actionEvent) {
    }

    public void signout(ActionEvent actionEvent) {
    }
}
