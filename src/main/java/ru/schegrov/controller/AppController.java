package ru.schegrov.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.util.Callback;
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
    private Accordion accordion;
    @FXML
    private TitledPane sign;
    @FXML
    private TitledPane jobs;

    public AppController(AppModel model) {
        this.model = model;
        logger.info("init");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        initController("/fxml/sign.fxml", t -> new SignAccordionController(this), sign);
        initController("/fxml/jobs.fxml", t -> new JobsAccordionController(), jobs);

        accordion.setExpandedPane(sign);
        logger.info("initialized");
    }

    private void initController(String fxml, Callback<Class<?>, Object> controllerFactory, TitledPane pane) {
        try {
            loader = new FXMLLoader(getClass().getResource(fxml));
            loader.setResources(resources);
            loader.setControllerFactory(controllerFactory);
            pane.setContent(loader.load());
        } catch (IOException e) {
            logger.error("initController error", e);
        }
    }

    public void expandedJobsPane(){
        accordion.setExpandedPane(jobs);
    }

//    private void initJobsController() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/jobs.fxml"));
//            loader.setResources(resources);
//            loader.setControllerFactory(t -> new SignAccordionController());
//            jobs.setContent(loader.load());
//        } catch (IOException e) {
//            logger.error("initSignController error", e);
//        }
//    }

    public void closeMenu(ActionEvent actionEvent) {
        System.exit(0);
    }
}
