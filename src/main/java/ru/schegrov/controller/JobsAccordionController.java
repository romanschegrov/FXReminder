package ru.schegrov.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.log4j.Logger;
import ru.schegrov.model.Job;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ramon on 13.07.2016.
 */
public class JobsAccordionController implements Initializable {

    private static final Logger logger = Logger.getLogger(JobsAccordionController.class);
    private ResourceBundle resources;
    private TreeItem<Job> root;

    @FXML
    private TreeView<Job> treeView;

    public JobsAccordionController() {

        logger.info("init");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/pic/title16.png")));
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);

        root = new TreeItem<>(new Job(resources.getString("app.accordion.titledpane.jobs.root")), imageView);
        root.setExpanded(true);
        root.getChildren().add(new TreeItem<>(new Job("Задание №1")));

        treeView.setRoot(root);

        logger.info("initialized");
    }
}
