package ru.schegrov.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.log4j.Logger;
import ru.schegrov.dao.ObjectDao;
import ru.schegrov.entity.Job;
import ru.schegrov.entity.JobCondition;
import ru.schegrov.util.AlertHelper;
import ru.schegrov.util.SchedulerAction;

import java.net.URL;
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
        alertError = new AlertHelper(Alert.AlertType.ERROR);
        alertError.setTitle(resources.getString("app.alert.title"));
        alertWarning = new AlertHelper(Alert.AlertType.WARNING);
        alertWarning.setTitle(resources.getString("app.alert.title"));
        alertInfo = new AlertHelper(Alert.AlertType.INFORMATION);
        alertInfo.setTitle(resources.getString("app.alert.title"));
        for (int i = 5; i < 91; i=i+5) min.getItems().add(String.valueOf(i));

        aadd.setGraphic(parent.getModel().loadImage("/pic/add.png"));
        adel.setGraphic(parent.getModel().loadImage("/pic/del.png"));
        nadd.setGraphic(parent.getModel().loadImage("/pic/add.png"));
        ndel.setGraphic(parent.getModel().loadImage("/pic/del.png"));

        scheduleYes.setDisable(true);
        scheduleNo.setDisable(true);
        min.setDisable(true);
        sql.setDisable(true);
        availableListView.setDisable(true);
        notifyListView.setDisable(true);

        yes.selectedProperty().addListener((observable, oldValue, newValue) -> {
            sql.setDisable(oldValue);
            scheduleYes.setDisable(oldValue);
            scheduleNo.setDisable(oldValue);
            availableListView.setDisable(oldValue);
            notifyListView.setDisable(oldValue);
        });

        scheduleYes.selectedProperty().addListener((observable, oldValue, newValue) -> {
            min.setDisable(oldValue);
        });

        parent.getJobSelectedListeners().add(job -> {
            this.job = job;
            if (job.getId()!= 0) {
                identifier.setText(String.valueOf(job.getId()));
                parentIdentifier.setText(String.valueOf(job.getParent_id()));
                name.setText(job.getName());

                JobCondition timerCondition = job.getCondition("TIMER");
                if (timerCondition != null) min.getSelectionModel().select(timerCondition.getValue());

                JobCondition sqlCondition = job.getCondition("SQL");
                if (sqlCondition != null) sql.setText(sqlCondition.getValue());

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
        if (job.getId() == 0) {
            //root item
        } else {
            try {
                if (job.isJob()) {

                    if (sql.getText() == null || sql.getText().isEmpty()) {
                        alertWarning.setContentText(resources.getString("app.alert.conditions.sql.isempty"));
                        alertWarning.show();
                        return;
                    }

                    updateCondition(job.getCondition("SCHEDULE"), "SCHEDULE", scheduleYes.isSelected() ? "1" : "0");
                    if (scheduleYes.isSelected()) updateCondition(job.getCondition("TIMER"), "TIMER", min.getValue().toString());
                    updateCondition(job.getCondition("SQL"), "SQL", sql.getText());
                }

                job.setName(name.getText());
                job.setParent_id(Integer.valueOf(parentIdentifier.getText()));

                ObjectDao<Job> dao = new ObjectDao<>(Job.class);
                dao.update(job);

                parent.getModel().schedulerAction(SchedulerAction.CANCEL_ALL, null);
                parent.getModel().fillTreeView();

                alertInfo.setContentText(resources.getString("app.alert.conditions.saved"));
                alertInfo.show();
            } catch (Exception e) {
                logger.error("applyAction error", e);
                alertError.setContentText(resources.getString("app.alert.conditions.update"));
                alertError.setException(e);
                alertError.show();
            }
        }
    }

    private void updateCondition(JobCondition condition, String code, String value) throws Exception {
        ObjectDao<JobCondition> dao = new ObjectDao<>(JobCondition.class);
        if (condition == null) {
            JobCondition newCondition = new JobCondition(code, value);
            job.getConditions().add(newCondition);
            newCondition.setJob(job);
            dao.update(newCondition);
        } else {
            condition.setValue(value);
            dao.update(condition);
        }
    }

    public void addAlignmentContextMenu(ActionEvent actionEvent) {
    }

    public void delAlignmentContextMenu(ActionEvent actionEvent) {
    }

    public void addNotifyContextMenu(ActionEvent actionEvent) {
    }

    public void delNotifyContextMenu(ActionEvent actionEvent) {
    }
}
