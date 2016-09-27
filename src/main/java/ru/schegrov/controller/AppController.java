package ru.schegrov.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.apache.log4j.Logger;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.hibernate.HibernateException;
import ru.schegrov.dao.ObjectDao;
import ru.schegrov.listener.Disconnected;
import ru.schegrov.listener.JobSelected;
import ru.schegrov.model.AppModel;
import ru.schegrov.entity.Job;
import ru.schegrov.entity.JobTableRow;
import ru.schegrov.util.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    private static final Logger logger = Logger.getLogger(AppController.class);

    private FXMLLoader loader;
    private AppModel model;
    private ResourceBundle resources;
    private AlertHelper alertError;
    private List<JobSelected> jobSelectedListeners;
    private List<Disconnected> disconnectedListeners;
    private JobSchedulerHelper scheduler;

    @FXML private Accordion accordion;
    @FXML private TitledPane sign;
    @FXML private Text url;
    @FXML private Text error;
    @FXML private Button signin;
    @FXML private Button signout;
    @FXML private CustomTextField username;
    @FXML private CustomPasswordField password;

    @FXML private TitledPane jobs;
    @FXML private TreeView<Job> tree;
    @FXML private MenuItem refresh;
    @FXML private MenuItem add;
    @FXML private MenuItem del;

    @FXML private TabPane tabPane;
    @FXML private Tab tabDetail;
    @FXML private TableView<JobTableRow> table;
    @FXML private Tab tabConditions;
    @FXML private Tab tabUsers;
    @FXML private Tab tabGroups;

    public AppController() {

        alertError = new AlertHelper(Alert.AlertType.ERROR);
        jobSelectedListeners = new ArrayList<>();
        disconnectedListeners = new ArrayList<>();
        logger.info("init");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        alertError.setTitle(resources.getString("app.alert.title"));
        scheduler = JobSchedulerHelper.getInstance(resources);
        model = new AppModel(tree, table, resources);

        accordion.setExpandedPane(sign);

        url.setText(HibernateHelper.getUrl());
        model.setupClearButtonField(username, username.rightProperty());
username.setText(HibernateHelper.getUsername());

        model.setupClearButtonField(password, password.rightProperty());
password.setText(HibernateHelper.getPassword());
        signout.setDisable(true);

        refresh.setGraphic(ImageHelper.loadImage("/pic/refresh.png"));
        add.setGraphic(ImageHelper.loadImage("/pic/add.png"));
        del.setGraphic(ImageHelper.loadImage("/pic/del.png"));

         tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
             if (newValue != null) {
                 Job job = newValue.getValue();
                 if (job != null) {
                     for (JobSelected jobSelected: jobSelectedListeners) {
                         jobSelected.selectJob(newValue);
                     }
                 }
             }
         });

        logger.info("initialized");
    }

    private void initController(String fxml, Callback<Class<?>, Object> controllerFactory, Tab pane) {
        try {
            loader = new FXMLLoader(getClass().getResource(fxml));
            loader.setResources(resources);
            loader.setControllerFactory(controllerFactory);
            Node load = loader.load();
            pane.setContent(load);
        } catch (IOException e) {
            alertError.setContentText("");
            alertError.setException(e);
            alertError.show();
            logger.error("initController error", e);
        }
    }

    public void signin(ActionEvent actionEvent) {
        signAction(true);
    }

    public void signout(ActionEvent actionEvent) {
        signAction(false);
    }

    private void signAction(boolean bool){
        signin.setDisable(bool);
        signout.setDisable(!bool);
        if (bool){
            try {
                HibernateHelper.getSessionFactory(username.getText(), password.getText(), resources);
                error.setText(null);
                accordion.setExpandedPane(jobs);
                model.fillTreeView();
                if (HibernateHelper.getConnectedUser().getAdmin()) {
                    tabUsers.setDisable(false);
                    tabGroups.setDisable(false);
                    tabConditions.setDisable(false);
                    initController("/fxml/users.fxml", t -> new UsersTabController(this), tabUsers);
                    initController("/fxml/groups.fxml", t -> new GroupsTabController(this), tabGroups);
                    initController("/fxml/conditions.fxml", t -> new ConditionsTabController(this), tabConditions);
                }
                logger.info("Connected");
            } catch (Exception e) {
                while (e.getCause()!=null) e = (Exception) e.getCause();
                error.setText(e.getMessage());
                logger.error("Button signin error: ", e);
                username.requestFocus();
            }
        } else {
            try {
                model.getScheduler().cancelAll();
                table.getColumns().clear();
                table.getItems().clear();
                if (tree.getRoot() != null) {
                    tree.getRoot().getChildren().clear();
                    tree.setRoot(null);
                }
                disconnectedListeners.forEach(listener -> listener.disconnect());
                tabPane.getSelectionModel().select(tabDetail);
                tabUsers.setDisable(true);
                tabGroups.setDisable(true);
                tabConditions.setDisable(true);
                HibernateHelper.closeSessionFactory();
                logger.info("Disconnected");
            } catch (HibernateException e) {
                error.setText(e.getMessage());
                logger.error("Button signout error: ", e);
            }
        }
    }

    public void closeMenu(ActionEvent actionEvent) {
        model.exit();
    }

    public void refreshContextMenu(ActionEvent actionEvent) {
        TreeItem<Job> selectedItem = tree.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getValue().getId() != 0) {
            scheduler.restart(selectedItem);
        }
    }

    public void addContextMenu(ActionEvent actionEvent) {
        TreeItem<Job> selectedItem = tree.getSelectionModel().getSelectedItem();
        if (selectedItem != null && !selectedItem.getValue().isJob()) {
            Job newJob = new Job();
            newJob.setName(resources.getString("app.accordion.titledpane.jobs.new"));
            newJob.setJob(false);
            newJob.setParent_id(selectedItem.getValue().getId());
            try {
                ObjectDao<Job> job = new ObjectDao<>(Job.class);
                job.add(newJob);
                selectedItem.getChildren().add(new TreeItem(newJob, ImageHelper.loadImage("/pic/folder.png")));
                selectedItem.setExpanded(true);
            } catch (Exception e) {
                logger.error("addUserContextMenu error: ", e);

                alertError.setContentText(resources.getString("app.alert.jobs.add"));
                alertError.setException(e);
                alertError.show();
            }
        }
    }

    public void delContextMenu(ActionEvent actionEvent) {
        TreeItem<Job> selectedItem = tree.getSelectionModel().getSelectedItem();
        int selectedIndex = tree.getSelectionModel().getSelectedIndex();
        if (selectedItem != null && selectedItem.getChildren().isEmpty()) {
            try {
                Job job = selectedItem.getValue();

                if (job.isJob()) scheduler.cancel(job);

                ObjectDao<Job> dao = new ObjectDao<>(Job.class);
                dao.delete(job);
                selectedItem.getParent().getChildren().remove(selectedItem);
                if (selectedIndex == 0)
                    tree.getSelectionModel().selectFirst();
                else
                    tree.getSelectionModel().select(--selectedIndex);
            } catch (Exception e) {
                logger.error("delUserContextMenu error: ", e);

                alertError.setContentText(resources.getString("app.alert.jobs.del"));
                alertError.setException(e);
                alertError.show();
            }
        }
    }

    public AppModel getModel() {
        return model;
    }

    public List<JobSelected> getJobSelectedListeners() {
        return jobSelectedListeners;
    }

    public List<Disconnected> getDisconnectedListeners() {
        return disconnectedListeners;
    }
}
