package ru.schegrov.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.apache.log4j.Logger;
import ru.schegrov.model.Job;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ramon on 13.07.2016.
 */
public class JobsAccordionController implements Initializable {

    private static final Logger logger = Logger.getLogger(JobsAccordionController.class);

    private TreeItem<Job> root;

    @FXML
    private TreeView<Job> treeView;

    public JobsAccordionController() {

        TreeItem<Job> child = new TreeItem<>(new Job("Задание №1"));

        root = new TreeItem<>(new Job("ПАО Донкомбанк"));
        root.setExpanded(true);
        root.getChildren().add(child);
        logger.info("init");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        treeView.setRoot(root);
        logger.info("initialized");
    }
}
