package ru.schegrov.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    @FXML private ChoiceBox min;
    @FXML private TextArea sql;

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

        yes.selectedProperty().addListener((observable, oldValue, newValue) -> {
            min.setDisable(oldValue);
            sql.setDisable(oldValue);
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

                if (job.isJob()) {
                    yes.setSelected(true);
                } else {
                    no.setSelected(true);
                }
            } else {
                identifier.setText("");
                parentIdentifier.setText("");
                name.setText("");
                yes.setSelected(false);
                no.setSelected(false);
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
                    if (min.getValue() == null || min.getValue().toString().isEmpty()) {
                        alertWarning.setContentText(resources.getString("app.alert.conditions.min.isempty"));
                        alertWarning.show();
                        return;
                    }

                    if (sql.getText() == null || sql.getText().isEmpty()) {
                        alertWarning.setContentText(resources.getString("app.alert.conditions.sql.isempty"));
                        alertWarning.show();
                        return;
                    }

                    updateCondition(job.getCondition("TIMER"),"TIMER",min.getValue().toString());
                    updateCondition(job.getCondition("SQL"),"SQL",sql.getText());

                    parent.getModel().schedulerAction(SchedulerAction.RESTART, job);
                } else {
                    parent.getModel().schedulerAction(SchedulerAction.CANCEL, job);
                }

                job.setName(name.getText());
                job.setParent_id(Integer.valueOf(parentIdentifier.getText()));

                ObjectDao<Job> dao = new ObjectDao<>(Job.class);
                dao.update(job);

                alertInfo.setContentText(resources.getString("app.alert.conditions.saved"));
                alertInfo.show();
            } catch (Exception e) {
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
}
