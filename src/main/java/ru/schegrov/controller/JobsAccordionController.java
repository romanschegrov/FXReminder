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
    private TreeItem<Job> rootTreeItem;

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

        Job rootJob = new Job();
        rootJob.setName(resources.getString("app.accordion.titledpane.jobs.root"));
        rootJob.setParent_id(0);

        rootTreeItem = new TreeItem<>(rootJob, loadImage("/pic/title16.png"));
        rootTreeItem.setExpanded(true);
        treeView.setRoot(rootTreeItem);

        logger.info("initialized");
    }

    private ImageView loadImage(String path) {
        ImageView view = new ImageView(new Image(getClass().getResourceAsStream(path)));
        view.setFitHeight(16);
        view.setFitWidth(16);
        return view;
    }

    public void addContextMenu(ActionEvent actionEvent) {
        TreeItem<Job> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Job newJob = new Job();
            newJob.setName("Новое задание"); //Перенести в resources
            newJob.setParent_id(selectedItem.getValue().getId());
            try {
                GenericDao<Job> job = new JobDaoImpl();
                job.add(newJob);
                selectedItem.getChildren().add(new TreeItem(newJob));
            } catch (Exception e) {
                logger.error("addContextMenu error: ", e);
                AlertHelper alert = new AlertHelper(Alert.AlertType.ERROR);
                alert.setTitle("Внимание"); //Перенести в resources
                alert.setContentText("Ошибка при создании задания"); //Перенести в resources
                alert.setException(e);
                alert.show();
            }
        }
    }

    public void delContextMenu(ActionEvent actionEvent) {
    }
}
