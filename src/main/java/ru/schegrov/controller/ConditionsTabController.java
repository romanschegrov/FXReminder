package ru.schegrov.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import ru.schegrov.dao.ConditionDao;
import ru.schegrov.dao.ObjectDao;
import ru.schegrov.entity.Group;
import ru.schegrov.entity.Job;
import ru.schegrov.entity.JobCondition;
import ru.schegrov.entity.User;
import ru.schegrov.model.ConditionsTabModel;
import ru.schegrov.util.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by ramon on 01.09.2016.
 */
public class ConditionsTabController implements Initializable {

    private static final Logger logger = Logger.getLogger(ConditionsTabController.class);

    private AppController parent;
    private ResourceBundle resources;
    private AlertHelper alertError;
    private AlertHelper alertWarning;
    private AlertHelper alertInfo;
    private Job job;
    private TreeItem<Job> jobTreeItem;
    private JobSchedulerHelper scheduler;
    private ConditionsTabModel model;

    @FXML private TextField identifier;
    @FXML private TextField parentIdentifier;
    @FXML private TextField name;
    @FXML private ToggleButton yes;
    @FXML private ToggleButton no;
    @FXML private ToggleButton scheduleYes;
    @FXML private ToggleButton scheduleNo;
    @FXML private ChoiceBox min;
    @FXML private TextArea sql;

    @FXML private ListView<String> availableListView;
    @FXML private MenuItem aadd;
    @FXML private MenuItem adel;

    @FXML private ListView<String> notifyListView;
    @FXML private MenuItem nadd;
    @FXML private MenuItem ndel;

    public ConditionsTabController(AppController parent) {
        this.parent = parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        scheduler = JobSchedulerHelper.getInstance(resources);
        model = new ConditionsTabModel(resources);

        alertError = new AlertHelper(Alert.AlertType.ERROR);
        alertError.setTitle(resources.getString("app.alert.title"));
        alertWarning = new AlertHelper(Alert.AlertType.WARNING);
        alertWarning.setTitle(resources.getString("app.alert.title"));
        alertInfo = new AlertHelper(Alert.AlertType.INFORMATION);
        alertInfo.setTitle(resources.getString("app.alert.title"));

        for (int i = 5; i < 91; i=i+5) min.getItems().add(String.valueOf(i));

        aadd.setGraphic(ImageHelper.loadImage("/pic/add.png"));
        adel.setGraphic(ImageHelper.loadImage("/pic/del.png"));
        nadd.setGraphic(ImageHelper.loadImage("/pic/add.png"));
        ndel.setGraphic(ImageHelper.loadImage("/pic/del.png"));

        scheduleYes.setDisable(true);
        scheduleNo.setDisable(true);
        min.setDisable(true);
        sql.setDisable(true);
        availableListView.setDisable(false);
        notifyListView.setDisable(true);

        availableListView.setOnEditCommit(event -> {
            model.editCondition("AVAILABLE", event);
        });

        notifyListView.setOnEditCommit(event -> {
            model.editCondition("NOTIFY", event);
        });

        yes.selectedProperty().addListener((observable, oldValue, newValue) -> {
            sql.setDisable(oldValue);
            if (oldValue) scheduleNo.setSelected(oldValue);
            scheduleYes.setDisable(oldValue);
            scheduleNo.setDisable(oldValue);
//            availableListView.setDisable(oldValue);
            notifyListView.setDisable(oldValue);
        });

        scheduleYes.selectedProperty().addListener((observable, oldValue, newValue) -> {
            min.setDisable(oldValue);
        });

        parent.getJobSelectedListeners().add(jobTreeItem -> {
            this.jobTreeItem = jobTreeItem;
            this.job = jobTreeItem.getValue();
            model.setJob(job);

            if (job.getId()!= 0) {
                identifier.setText(String.valueOf(job.getId()));
                parentIdentifier.setText(String.valueOf(job.getParent_id()));
                name.setText(job.getName());

                JobCondition timerCondition = job.getCondition("TIMER");
                if (timerCondition == null) min.getSelectionModel().selectFirst();
                else min.getSelectionModel().select(timerCondition.getValue());

                JobCondition sqlCondition = job.getCondition("SQL");
                if (sqlCondition == null) sql.setText("");
                else sql.setText(sqlCondition.getValue());

                JobCondition scheduleCondition = job.getCondition("SCHEDULE");
                if (scheduleCondition == null) {
                    scheduleYes.setSelected(false);
                    scheduleNo.setSelected(true);
                } else {
                    scheduleYes.setSelected(scheduleCondition.getValue().equals("1") ? true : false);
                    scheduleNo.setSelected(scheduleCondition.getValue().equals("0") ? true : false);
                }

                yes.setSelected(job.isJob() ? true : false);
                no.setSelected(!job.isJob() ? true : false);

                ObservableList<String> listAvailable = model.allowedUsersGroups("AVAILABLE");
                if (!listAvailable.isEmpty()) availableListView.setCellFactory(ChoiceBoxListCell.forListView(listAvailable));

                List<JobCondition> available = job.getConditions("AVAILABLE");
                availableListView.getItems().clear();
                if (available != null) {
                    available.forEach( a-> availableListView.getItems().add(a.getValue()));
                }

                ObservableList<String> listNotify = model.allowedUsersGroups("NOTIFY");
                if (!listNotify.isEmpty()) notifyListView.setCellFactory(ChoiceBoxListCell.forListView(listNotify));

                List<JobCondition> notify = job.getConditions("NOTIFY");
                notifyListView.getItems().clear();
                if (notify != null) {
                    notify.forEach( n-> notifyListView.getItems().add(n.getValue()));
                }
            } else {
                identifier.setText("");
                parentIdentifier.setText("");
                name.setText("");
                yes.setSelected(false);
                scheduleYes.setSelected(false);
                no.setSelected(false);
                scheduleNo.setSelected(false);
                min.setValue(null);
                sql.setText("");
            }
        });
    }

    public void toggleButtonAction(ActionEvent actionEvent) {
        ToggleButton selectedButton = (ToggleButton) actionEvent.getSource();
        job.setJob(selectedButton.equals(yes) ? true : false);
    }

    public void applyAction(ActionEvent actionEvent) {
        try {
            if (job.getId() != 0) { //not root item
                if (job.isJob()) {

                    if (sql.getText() == null || sql.getText().isEmpty()) {
                        alertWarning.setContentText(resources.getString("app.alert.conditions.sql.isempty"));
                        alertWarning.show();
                        return;
                    }
                    model.updateCondition(job.getCondition("SQL"), "SQL", sql.getText());
                    model.updateCondition(job.getCondition("SCHEDULE"), "SCHEDULE", scheduleYes.isSelected() ? "1" : "0");
                    if (scheduleYes.isSelected()) {
                        model.updateCondition(job.getCondition("TIMER"), "TIMER", min.getValue().toString());
                        scheduler.restart(jobTreeItem);
                    } else {
                        scheduler.cancel(job);
                    }
                } else {
                    scheduler.cancel(job);
                }

                job.setName(name.getText());
                job.setParent_id(Integer.valueOf(parentIdentifier.getText()));

                ObjectDao<Job> dao = new ObjectDao<>(Job.class);
                dao.update(job);

                alertInfo.setContentText(resources.getString("app.alert.conditions.saved"));
                alertInfo.show();
            }
        } catch (Exception e) {
            logger.error("applyAction error", e);
            alertError.setContentText(resources.getString("app.alert.conditions.update"));
            alertError.setException(e);
            alertError.show();
        }
    }

    public void addAvailableContextMenu(ActionEvent actionEvent) {
        model.addCondition("AVAILABLE", availableListView);
    }

    public void addNotifyContextMenu(ActionEvent actionEvent) {
        model.addCondition("NOTIFY", notifyListView);
    }

    public void delAvailableContextMenu(ActionEvent actionEvent) {
        model.delCondition("AVAILABLE", availableListView);
    }

    public void delNotifyContextMenu(ActionEvent actionEvent) { model.delCondition("NOTIFY", availableListView); }
}
