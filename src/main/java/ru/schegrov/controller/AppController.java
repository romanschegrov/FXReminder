package ru.schegrov.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import ru.schegrov.dao.ObjectDao;
import ru.schegrov.model.AppModel;
import ru.schegrov.entity.Job;
import ru.schegrov.entity.JobTableRow;
import ru.schegrov.util.AlertHelper;
import ru.schegrov.util.HibernateHelper;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    private static final Logger logger = Logger.getLogger(AppController.class);

    private FXMLLoader loader;
    private AppModel model;
    private ResourceBundle resources;
    private AlertHelper alertError;

    @FXML private Accordion accordion;
    @FXML private TitledPane sign;
    @FXML private Text url;
    @FXML private Text error;
    @FXML private Button signin;
    @FXML private Button signout;
    @FXML private TextField username;
    @FXML private TextField password;

    @FXML private TitledPane jobs;
    @FXML private TreeView<Job> tree;
    @FXML private MenuItem refresh;
    @FXML private MenuItem add;
    @FXML private MenuItem del;

    @FXML private TabPane tabPane;
    @FXML private Tab tabDetail;
    @FXML private TableView<JobTableRow> table;
    @FXML private Tab tabProperties;
    @FXML private Tab tabUsers;
    @FXML private Tab tabGroups;

    public AppController() {

        alertError = new AlertHelper(Alert.AlertType.ERROR);
        logger.info("init");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        alertError.setTitle(resources.getString("app.alert.title"));

        model = new AppModel(tree, table);
        model.setResources(resources);

        accordion.setExpandedPane(sign);

        url.setText(HibernateHelper.getUrl());
        username.setText(HibernateHelper.getUsername());
        password.setText(HibernateHelper.getPassword());
        signout.setDisable(true);

        refresh.setGraphic(model.loadImage("/pic/refresh.png"));
        add.setGraphic(model.loadImage("/pic/add.png"));
        del.setGraphic(model.loadImage("/pic/del.png"));

        logger.info("initialized");
    }

    private void initController(String fxml, Callback<Class<?>, Object> controllerFactory, Tab pane) {
        try {
            loader = new FXMLLoader(getClass().getResource(fxml));
            loader.setResources(resources);
            loader.setControllerFactory(controllerFactory);
            pane.setContent(loader.load());
        } catch (IOException e) {
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
                    tabProperties.setDisable(false);
                    tabUsers.setDisable(false);
                    tabGroups.setDisable(false);
                    initController("/fxml/users.fxml", t -> new UsersTabController(this), tabUsers);
                    initController("/fxml/groups.fxml", t -> new GroupsTabController(this), tabGroups);
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
                HibernateHelper.closeSessionFactory();
                model.cancelSchedulers();
                table.getColumns().clear();
                table.getItems().clear();
                if (tree.getRoot() != null) {
                    tree.getRoot().getChildren().clear();
                    tree.setRoot(null);
                }
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
                selectedItem.getChildren().add(new TreeItem(newJob, model.loadImage("/pic/folder.png")));
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
        alertError.setContentText(resources.getString("app.alert.jobs.del"));
//        alertError.setException(e);
        alertError.show();
    }

    public AppModel getModel() {
        return model;
    }
}
