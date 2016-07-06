package ru.schegrov.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TitledPane;
import org.apache.log4j.Logger;
import ru.schegrov.model.AppModel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    private static final Logger logger = Logger.getLogger(AppController.class);

    private FXMLLoader loader;
    private AppModel model;
    private ResourceBundle resources;

    @FXML
    private TitledPane signIn;
    @FXML
    private TitledPane jobs;

    public AppController(AppModel model) {
        this.model = model;
        logger.info("AppController init");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        initSignController();
        logger.info("AppController initialized");
    }

    private void initSignController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signin.fxml"));
            loader.setResources(resources);
            loader.setControllerFactory(t -> new SignAccordionController());
            signIn.setContent(loader.load());
        } catch (IOException e) {
            logger.error("initSignController error", e);
        }
    }
}
