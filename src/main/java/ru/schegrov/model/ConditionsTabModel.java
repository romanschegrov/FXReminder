package ru.schegrov.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.ChoiceBoxListCell;
import org.apache.log4j.Logger;
import ru.schegrov.dao.ConditionDao;
import ru.schegrov.dao.ObjectDao;
import ru.schegrov.entity.Group;
import ru.schegrov.entity.Job;
import ru.schegrov.entity.JobCondition;
import ru.schegrov.entity.User;
import ru.schegrov.util.AlertHelper;

import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by ramon on 14.09.2016.
 */
public class ConditionsTabModel {

    private static final Logger logger = Logger.getLogger(ConditionsTabModel.class);

    private Job job;
    //private ListView<String> availableListView;
    //private ListView<String> notifyListView;
    private ResourceBundle resources;
    private AlertHelper alertError;

    public ConditionsTabModel(ResourceBundle resources) {
//        this.job = job;
//        this.availableListView = availableListView;
//        this.notifyListView = notifyListView;
        this.resources = resources;
        this.alertError = new AlertHelper(Alert.AlertType.ERROR);
        this.alertError.setTitle(resources.getString("app.alert.title"));
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public void editCondition(String code, ListView.EditEvent<String> event) {
        try {
            delCondition(code, event.getSource());

            String newValue = event.getNewValue();
            event.getSource().getItems().add(newValue);
            event.getSource().getSelectionModel().selectLast();

            JobCondition condition = new JobCondition(code,newValue);
            condition.setJob(job);
            ObjectDao<JobCondition> dao = new ObjectDao<>(JobCondition.class);
            dao.add(condition);
            job.getConditions().add(condition);

            ObservableList<String> list = allowedUsersGroups(code);
            event.getSource().setCellFactory(ChoiceBoxListCell.forListView(list));
        } catch (Exception e) {
            logger.error("setOnEditCommit error", e);
            alertError.setContentText(resources.getString("app.alert.conditions.update"));
            alertError.setException(e);
            alertError.show();
        }
    }

    public void updateCondition(JobCondition condition, String code, String value) throws Exception {
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

    public ObservableList<String> allowedUsersGroups(String code){

        ObservableList<String> allowedList = FXCollections.observableArrayList();

        ObjectDao<User> daoUser = new ObjectDao<>(User.class);
        List<User> users = daoUser.getAll();
        users.forEach(user -> allowedList.add(user.getCode()));

        ObjectDao<Group> daoGroup = new ObjectDao<>(Group.class);
        List<Group> groups = daoGroup.getAll();
        groups.forEach(group -> allowedList.add(group.getCode()));

        ObservableList<String> jobConditionString = FXCollections.observableArrayList();
        List<JobCondition> conditions = job.getConditions(code);
        if (conditions != null) {
            conditions.forEach(condition -> jobConditionString.add(condition.getValue()));
        }
        allowedList.removeAll(jobConditionString);
        return allowedList;
    }

    public void addCondition(String code, ListView<String> view) {
        ObservableList<String> list = allowedUsersGroups(code);
        if (!list.isEmpty()) {
            try {
                String value = list.iterator().next();

                JobCondition condition = new JobCondition(code, value);
                condition.setJob(job);

                ObjectDao<JobCondition> dao = new ObjectDao<>(JobCondition.class);
                dao.add(condition);

                job.getConditions().add(condition);
                view.getItems().add(value);

                list = allowedUsersGroups(code);
                if (!list.isEmpty()) {
                    view.setEditable(true);
                    view.setCellFactory(ChoiceBoxListCell.forListView(list));
                } else {
                    view.setEditable(false);
                }
            } catch (Exception e) {
                logger.error("addCondition error", e);
                alertError.setContentText(resources.getString("app.alert.conditions.add"));
                alertError.setException(e);
                alertError.show();
            }
        }
    }

    public void delCondition(String code, ListView<String> view) {
        String selectedItem = view.getSelectionModel().getSelectedItem();
        int index = view.getSelectionModel().getSelectedIndex();
        if (selectedItem != null) {
            try {
                ConditionDao dao = new ConditionDao();
                JobCondition condition = dao.getConditionByCodeAndValue(job, code, selectedItem);

                ObjectDao<JobCondition> objectDao = new ObjectDao<>(JobCondition.class);
                objectDao.delete(condition);

                job.getConditions().remove(condition);

                ObservableList<String> list = allowedUsersGroups(code);
                if (!list.isEmpty()) {
                    view.setEditable(true);
                    view.setCellFactory(ChoiceBoxListCell.forListView(list));
                } else {
                    view.setEditable(false);
                }

                view.getItems().remove(index);
                if (index == 0) {
                    view.getSelectionModel().selectFirst();
                } else {
                    view.getSelectionModel().select(index - 1);
                }
            } catch (Exception e) {
                logger.error("delCondition error", e);
                alertError.setContentText(resources.getString("app.alert.conditions.del"));
                alertError.setException(e);
                alertError.show();
            }
        }
    }
}
