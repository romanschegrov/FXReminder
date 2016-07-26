package ru.schegrov.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.log4j.Logger;
import ru.schegrov.dao.GenericDao;
import ru.schegrov.dao.JobDaoImpl;
import ru.schegrov.model.Job;
import ru.schegrov.util.AlertHelper;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ramon on 13.07.2016.
 */
public class JobsAccordionController implements Initializable {

    private static final Logger logger = Logger.getLogger(JobsAccordionController.class);
    private ResourceBundle resources;

    public JobsAccordionController() {
        logger.info("init");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("start initialize");
        this.resources = resources;
        logger.info("initialized");
    }

    private ImageView loadImage(String path) {
        return null;
    }

    public void addContextMenu(ActionEvent actionEvent) {
    }

    public void delContextMenu(ActionEvent actionEvent) {
    }
}
