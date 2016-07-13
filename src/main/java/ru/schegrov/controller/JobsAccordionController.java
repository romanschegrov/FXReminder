package ru.schegrov.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.log4j.Logger;
import ru.schegrov.model.Job;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * Created by ramon on 13.07.2016.
 */
public class JobsAccordionController implements Initializable {

    private static final Logger logger = Logger.getLogger(JobsAccordionController.class);
    private ResourceBundle resources;
    private TreeItem<Job> root;

    @FXML
    private TreeView<Job> treeView;

    @FXML
    private MenuItem refresh;
    @FXML
    private MenuItem add;
    @FXML
    private MenuItem del;

    public JobsAccordionController() {
        logger.info("init");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("start initialize");
        this.resources = resources;


        refresh.setGraphic(loadImage("/pic/refresh.png"));
        add.setGraphic(loadImage("/pic/add.png"));
        del.setGraphic(loadImage("/pic/del.png"));

        root = new TreeItem<>(new Job(resources.getString("app.accordion.titledpane.jobs.root")), loadImage("/pic/title16.png"));
        root.setExpanded(true);
        root.getChildren().add(new TreeItem<>(new Job("Задание №1")));

        treeView.setRoot(root);

        logger.info("initialized");
    }

    private ImageView loadImage(String path) {
        ImageView view = new ImageView(new Image(getClass().getResourceAsStream(path)));
        view.setFitHeight(16);
        view.setFitWidth(16);
        return view;
    }
}
