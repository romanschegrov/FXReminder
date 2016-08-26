package ru.schegrov.controller;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import ru.schegrov.dao.ObjectDao;
import ru.schegrov.entity.Group;
import ru.schegrov.entity.User;
import ru.schegrov.util.AlertHelper;
import ru.schegrov.util.BooleanStringConverter;
import ru.schegrov.util.HibernateHelper;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by ramon on 24.08.2016.
 */
public class GroupsTabController implements Initializable {

    private AlertHelper alertError;
    private ResourceBundle resources;
    @FXML private TableView<Group> groupsTableView;
    @FXML private TableView<User> usersTableView;
    @FXML private TableColumn<Group,String> code;
    @FXML private TableColumn<Group,String> descr;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        alertError = new AlertHelper(Alert.AlertType.ERROR);
        alertError.setTitle(resources.getString("app.alert.title"));

        HibernateHelper.getAllGroups().addListener((ListChangeListener<Group>) c -> {
            groupsTableView.getItems().clear();
            groupsTableView.getItems().addAll(c.getList());
        });

        groupsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldGroup, newGroup) -> {
            if (newGroup != null) {
                usersTableView.getItems().clear();
                usersTableView.getItems().addAll(newGroup.getUsers());
            }
        });

        code.setOnEditCommit(event -> {
            event.getRowValue().setCode(event.getNewValue());
            edit(event.getRowValue());
        });

        descr.setOnEditCommit(event -> {
            event.getRowValue().setDescr(event.getNewValue());
            edit(event.getRowValue());
        });
    }

    private void edit(Group group){
        try {
            ObjectDao<Group> dao = new ObjectDao<>(Group.class);
            dao.update(group);
        } catch (Exception e) {
            alertError.setContentText(resources.getString("app.alert.groups.update"));
            alertError.setException(e);
            alertError.show();
        }
    }

    public void addContextMenu(ActionEvent actionEvent) {
        try {
            Group group = new Group();
            group.setCode(resources.getString("app.tabpane.tab.groups.new"));
            ObjectDao<Group> dao = new ObjectDao<>(Group.class);
            dao.add(group);
            HibernateHelper.getAllGroups().add(group);
            groupsTableView.getSelectionModel().selectLast();
        } catch (Exception e) {
            alertError.setContentText(resources.getString("app.alert.groups.add"));
            alertError.setException(e);
            alertError.show();
        }
    }

    public void delContextMenu(ActionEvent actionEvent) {
        Group group = groupsTableView.getSelectionModel().getSelectedItem();
        int index = groupsTableView.getSelectionModel().getSelectedIndex();
        if (group != null) {
            try {
                ObjectDao<Group> dao = new ObjectDao<>(Group.class);
                dao.delete(group);
                HibernateHelper.getAllGroups().remove(group);
                if (index == 0) {
                    groupsTableView.getSelectionModel().selectFirst();
                } else {
                    groupsTableView.getSelectionModel().select(index-1);
                }
            } catch (Exception e) {
                alertError.setContentText(resources.getString("app.alert.groups.del"));
                alertError.setException(e);
                alertError.show();
            }
        }
    }

    public void refreshContextMenu(ActionEvent actionEvent) {
        HibernateHelper.getAllGroups().clear();
        ObjectDao<Group> dao = new ObjectDao<>(Group.class);
        List<Group> list = dao.getAll();
        list.forEach(group -> HibernateHelper.getAllGroups().add(group));
        groupsTableView.getSelectionModel().selectFirst();
    }
}
