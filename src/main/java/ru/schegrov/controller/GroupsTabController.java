package ru.schegrov.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import ru.schegrov.dao.ObjectDao;
import ru.schegrov.entity.Group;
import ru.schegrov.entity.User;
import ru.schegrov.model.GroupsTabModel;
import ru.schegrov.util.AlertHelper;
import ru.schegrov.util.ImageHelper;

import javax.jws.soap.SOAPBinding;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by ramon on 24.08.2016.
 */
public class GroupsTabController implements Initializable {

    private GroupsTabModel model;
    private AppController parent;
    private AlertHelper alertError;
    private ResourceBundle resources;
    @FXML private MenuItem urefresh;
    @FXML private MenuItem grefresh;
    @FXML private MenuItem uadd;
    @FXML private MenuItem gadd;
    @FXML private MenuItem udel;
    @FXML private MenuItem gdel;
    @FXML private TableView<Group> groupsTableView;
    @FXML private TableColumn<Group,String> code;
    @FXML private TableColumn<Group,String> descr;
    @FXML private TableView<User> usersTableView;
    @FXML private TableColumn<User,String> ucode;

    public GroupsTabController(AppController parent) {
        this.parent = parent;
        this.model = new GroupsTabModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        alertError = new AlertHelper(Alert.AlertType.ERROR);
        alertError.setTitle(resources.getString("app.alert.title"));

        urefresh.setGraphic(ImageHelper.loadImage("/pic/refresh.png"));
        grefresh.setGraphic(ImageHelper.loadImage("/pic/refresh.png"));
        uadd.setGraphic(ImageHelper.loadImage("/pic/add.png"));
        gadd.setGraphic(ImageHelper.loadImage("/pic/add.png"));
        udel.setGraphic(ImageHelper.loadImage("/pic/del.png"));
        gdel.setGraphic(ImageHelper.loadImage("/pic/del.png"));

        parent.getDisconnectedListeners().add(() -> {
            groupsTableView.getColumns().clear();
            groupsTableView.getItems().clear();
            usersTableView.getColumns().clear();
            usersTableView.getItems().clear();
        });

        refreshGroupsTable();
        refreshUsersTable();

        groupsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldGroup, newGroup) -> {
            if (newGroup != null) {
                usersTableView.getItems().clear();
                usersTableView.getItems().addAll(newGroup.getUsers());
                ObservableList<String> allowedUsers = model.allowedUsers(newGroup);
                if (!allowedUsers.isEmpty()) {
                    ucode.setCellFactory(ChoiceBoxTableCell.forTableColumn(allowedUsers));
                }
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

        ucode.setOnEditCommit(event -> {
            Group selectedGroup = groupsTableView.getSelectionModel().getSelectedItem();
            if (selectedGroup != null) {
                try {
                    User oldUser = usersTableView.getSelectionModel().getSelectedItem();

                    ObjectDao<User> userDao = new ObjectDao<User>(User.class);
                    String newUsername = event.getNewValue();
                    User newUser = userDao.getByCode(newUsername);

                    ObjectDao<User> userObjectDao = new ObjectDao<>(User.class);
                    oldUser.getGroups().remove(selectedGroup);
                    userObjectDao.update(oldUser);
                    newUser.getGroups().add(selectedGroup);
                    userObjectDao.update(newUser);

                    selectedGroup.getUsers().remove(oldUser);
                    selectedGroup.getUsers().add(newUser);

                    int index = usersTableView.getSelectionModel().getSelectedIndex();
                    refreshUsersTable();
                    usersTableView.getSelectionModel().select(index);
                } catch (Exception e) {
                    alertError.setContentText(resources.getString("app.alert.users.update"));
                    alertError.setException(e);
                    alertError.show();
                }
            }
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

    public void addGroupContextMenu(ActionEvent actionEvent) {
        try {
            Group group = new Group();
            group.setCode(resources.getString("app.tabpane.tab.groups.new"));
            ObjectDao<Group> dao = new ObjectDao<>(Group.class);
            dao.add(group);
            groupsTableView.getItems().add(group);
            groupsTableView.getSelectionModel().selectLast();
        } catch (Exception e) {
            alertError.setContentText(resources.getString("app.alert.groups.add"));
            alertError.setException(e);
            alertError.show();
        }
    }

    public void delGroupContextMenu(ActionEvent actionEvent) {
        Group group = groupsTableView.getSelectionModel().getSelectedItem();
        int index = groupsTableView.getSelectionModel().getSelectedIndex();
        if (group != null) {
            try {
                ObjectDao<Group> dao = new ObjectDao<>(Group.class);
                dao.delete(group);
                groupsTableView.getItems().remove(group);
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

    public void refreshGroupContextMenu(ActionEvent actionEvent) {
        refreshGroupsTable();
    }

    private void refreshGroupsTable(){
        groupsTableView.getItems().clear();
        ObjectDao<Group> dao = new ObjectDao<>(Group.class);
        List<Group> list = dao.getAll();
        list.forEach(group -> groupsTableView.getItems().add(group));
        groupsTableView.getSelectionModel().selectFirst();
    }

    public void addUserContextMenu(ActionEvent actionEvent) {
        Group selectedGroup = groupsTableView.getSelectionModel().getSelectedItem();
        if (selectedGroup != null) {
            ObservableList<String> allowedUsers = model.allowedUsers(selectedGroup);
            if (!allowedUsers.isEmpty()) {
                try {

                    ObjectDao<User> userDao = new ObjectDao<>(User.class);
                    String username = allowedUsers.iterator().next();
                    User user = userDao.getByCode(username);

                    ObjectDao<User> userObjectDao = new ObjectDao<>(User.class);
                    user.getGroups().add(selectedGroup);
                    userObjectDao.update(user);

                    selectedGroup.getUsers().add(user);
                    ObservableList<String> allowedUsersPost = model.allowedUsers(selectedGroup);
                    if (!allowedUsersPost.isEmpty()) {
                        usersTableView.setEditable(true);
                        ucode.setCellFactory(ChoiceBoxTableCell.forTableColumn(allowedUsersPost));
                    } else {
                        usersTableView.setEditable(false);
                    }

                    usersTableView.getItems().add(user);
                    usersTableView.getSelectionModel().selectLast();

                } catch (Exception e) {
                    alertError.setContentText(resources.getString("app.alert.users.add"));
                    alertError.setException(e);
                    alertError.show();
                }
            }
        }
    }

    public void delUserContextMenu(ActionEvent actionEvent) {
        Group selectedGroup = groupsTableView.getSelectionModel().getSelectedItem();
        if (selectedGroup != null) {
            User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                try {

                    ObjectDao<User> userObjectDao = new ObjectDao<>(User.class);
                    selectedUser.getGroups().remove(selectedGroup);
                    userObjectDao.update(selectedUser);

                    selectedGroup.getUsers().remove(selectedUser);
                    ObservableList<String> allowedUsersPost = model.allowedUsers(selectedGroup);
                    if (!allowedUsersPost.isEmpty()) {
                        usersTableView.setEditable(true);
                        ucode.setCellFactory(ChoiceBoxTableCell.forTableColumn(allowedUsersPost));
                    } else {
                        usersTableView.setEditable(false);
                    }

                    usersTableView.getItems().remove(selectedUser);
                    int index = groupsTableView.getSelectionModel().getSelectedIndex();
                    if (index == 0) {
                        usersTableView.getSelectionModel().selectFirst();
                    } else {
                        usersTableView.getSelectionModel().select(index - 1);
                    }
                } catch (Exception e) {
                    alertError.setContentText(resources.getString("app.alert.users.del"));
                    alertError.setException(e);
                    alertError.show();
                }
            }
        }
    }

    public void refreshUserContextMenu(ActionEvent actionEvent) { refreshUsersTable(); }

    private void refreshUsersTable(){
        Group selectedGroup = groupsTableView.getSelectionModel().getSelectedItem();
        if (selectedGroup != null) {
            ObservableList<String> allowedUsers = model.allowedUsers(selectedGroup);
            if (!allowedUsers.isEmpty()) {
                usersTableView.setEditable(true);
                ucode.setCellFactory(ChoiceBoxTableCell.forTableColumn(allowedUsers));
            } else {
                usersTableView.setEditable(false);
            }
            usersTableView.getItems().clear();
            usersTableView.getItems().addAll(selectedGroup.getUsers());
            usersTableView.getSelectionModel().selectFirst();
        }
    }
}
