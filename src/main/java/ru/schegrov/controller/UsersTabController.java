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
import ru.schegrov.dao.GroupDao;
import ru.schegrov.dao.ObjectDao;
import ru.schegrov.entity.Group;
import ru.schegrov.entity.User;
import ru.schegrov.model.UsersTabModel;
import ru.schegrov.util.AlertHelper;
import ru.schegrov.util.BooleanStringConverter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by ramon on 22.08.2016.
 */
public class UsersTabController implements Initializable {

    private UsersTabModel model;
    private AppController parent;
    private AlertHelper alertError;
    private ResourceBundle resources;
    @FXML private TableView<User> usersTableView;
    @FXML private MenuItem urefresh;
    @FXML private MenuItem grefresh;
    @FXML private MenuItem uadd;
    @FXML private MenuItem gadd;
    @FXML private MenuItem udel;
    @FXML private MenuItem gdel;
    @FXML private TableColumn<User,String> code;
    @FXML private TableColumn<User,String> descr;
    @FXML private TableColumn<User,Boolean> admin;
    @FXML private TableView<Group> groupsTableView;
    @FXML private TableColumn<Group,String> gcode;

    public UsersTabController(AppController parent) {
        this.parent = parent;
        this.model = new UsersTabModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        alertError = new AlertHelper(Alert.AlertType.ERROR);
        alertError.setTitle(resources.getString("app.alert.title"));

        urefresh.setGraphic(parent.getModel().loadImage("/pic/refresh.png"));
        grefresh.setGraphic(parent.getModel().loadImage("/pic/refresh.png"));
        uadd.setGraphic(parent.getModel().loadImage("/pic/add.png"));
        gadd.setGraphic(parent.getModel().loadImage("/pic/add.png"));
        udel.setGraphic(parent.getModel().loadImage("/pic/del.png"));
        gdel.setGraphic(parent.getModel().loadImage("/pic/del.png"));

        refreshUsersTable();
        refreshGroupsTable();

        code.setOnEditCommit(event -> {
            event.getRowValue().setCode(event.getNewValue());
            editUser(event.getRowValue());
        });

        descr.setOnEditCommit(event -> {
            event.getRowValue().setDescr(event.getNewValue());
            editUser(event.getRowValue());
        });

        BooleanStringConverter converter  = new BooleanStringConverter(resources);
        admin.setCellFactory(ChoiceBoxTableCell.forTableColumn(converter, true, false));
        admin.setOnEditCommit(event -> {
            event.getRowValue().setAdmin(event.getNewValue());
            editUser(event.getRowValue());
        });

        usersTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldUser, newUser) -> {
            if (newUser != null) {
                groupsTableView.getItems().clear();
                groupsTableView.getItems().addAll(newUser.getGroups());
                ObservableList<String> allowedGroups = model.allowedGroups(newUser);
                if (!allowedGroups.isEmpty()) {
                    gcode.setCellFactory(ChoiceBoxTableCell.forTableColumn(allowedGroups));
                }
            }
        });

        gcode.setOnEditCommit(event -> {
            User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                try {
                    Group oldGroup = groupsTableView.getSelectionModel().getSelectedItem();

                    GroupDao groupDao = new GroupDao();
                    String newGroupCode = event.getNewValue();
                    Group newGroup = groupDao.getGroupByCode(newGroupCode);

                    ObjectDao<User> dao = new ObjectDao<>(User.class);
                    selectedUser.getGroups().remove(oldGroup);
                    selectedUser.getGroups().add(newGroup);
                    dao.update(selectedUser);

                    int index = groupsTableView.getSelectionModel().getSelectedIndex();
                    refreshGroupsTable();
                    groupsTableView.getSelectionModel().select(index);
                } catch (Exception e) {
                    alertError.setContentText(resources.getString("app.alert.groups.update"));
                    alertError.setException(e);
                    alertError.show();
                }
            }
        });
    }

    private void editUser(User user){
        try {
            ObjectDao<User> dao = new ObjectDao<>(User.class);
            dao.update(user);
        } catch (Exception e) {
            alertError.setContentText(resources.getString("app.alert.users.update"));
            alertError.setException(e);
            alertError.show();
        }
    }

    public void addUserContextMenu(ActionEvent actionEvent) {
        try {
            User user = new User();
            user.setCode(resources.getString("app.tabpane.tab.users.new"));
            ObjectDao<User> dao = new ObjectDao<>(User.class);
            dao.add(user);
            usersTableView.getItems().add(user);
            usersTableView.getSelectionModel().selectLast();
        } catch (Exception e) {
            alertError.setContentText(resources.getString("app.alert.users.add"));
            alertError.setException(e);
            alertError.show();
        }
    }

    public void delUserContextMenu(ActionEvent actionEvent) {
        User user = usersTableView.getSelectionModel().getSelectedItem();
        int index = usersTableView.getSelectionModel().getSelectedIndex();
        if (user != null) {
            try {
                ObjectDao<User> dao = new ObjectDao<>(User.class);
                dao.delete(user);
                usersTableView.getItems().remove(user);
                if (index == 0) {
                    usersTableView.getSelectionModel().selectFirst();
                } else {
                    usersTableView.getSelectionModel().select(index-1);
                }
            } catch (Exception e) {
                alertError.setContentText(resources.getString("app.alert.users.del"));
                alertError.setException(e);
                alertError.show();
            }
        }
    }

    public void refreshUserContextMenu(ActionEvent actionEvent) {
        refreshUsersTable();
    }

    private void refreshUsersTable(){
        usersTableView.getItems().clear();
        ObjectDao<User> dao = new ObjectDao<>(User.class);
        List<User> list = dao.getAll();
        list.forEach(user -> usersTableView.getItems().add(user));
        usersTableView.getSelectionModel().selectFirst();
    }

    public void addGroupContextMenu(ActionEvent actionEvent) {
        User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            ObservableList<String> allowedGroups = model.allowedGroups(selectedUser);
            if (!allowedGroups.isEmpty()) {
                try {
                    GroupDao groupDao = new GroupDao();
                    String groupCode = allowedGroups.iterator().next();
                    Group group = groupDao.getGroupByCode(groupCode);

                    ObjectDao<User> daoUser = new ObjectDao<>(User.class);
                    selectedUser.getGroups().add(group);
                    daoUser.update(selectedUser);

                    ObservableList<String> allowedGroupsPost = model.allowedGroups(selectedUser);
                    if (!allowedGroupsPost.isEmpty()) {
                        groupsTableView.setEditable(true);
                        gcode.setCellFactory(ChoiceBoxTableCell.forTableColumn(allowedGroupsPost));
                    }
                    else {
                        groupsTableView.setEditable(false);
                    }

                    groupsTableView.getItems().add(group);
                    groupsTableView.getSelectionModel().selectLast();
                } catch (Exception e) {
                    alertError.setContentText(resources.getString("app.alert.groups.add"));
                    alertError.setException(e);
                    alertError.show();
                }
            }
        }
    }

    public void delGroupContextMenu(ActionEvent actionEvent) {
        User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Group selectedGroup = groupsTableView.getSelectionModel().getSelectedItem();
            if (selectedGroup != null) {
                try {
                    ObjectDao<User> daoUser = new ObjectDao<>(User.class);
                    selectedUser.getGroups().remove(selectedGroup);
                    daoUser.update(selectedUser);

                    ObservableList<String> allowedGroupsPost = model.allowedGroups(selectedUser);
                    if (!allowedGroupsPost.isEmpty()) {
                        groupsTableView.setEditable(true);
                        gcode.setCellFactory(ChoiceBoxTableCell.forTableColumn(allowedGroupsPost));
                    }
                    else {
                        groupsTableView.setEditable(false);
                    }

                    groupsTableView.getItems().remove(selectedGroup);
                    int index = groupsTableView.getSelectionModel().getSelectedIndex();
                    if (index == 0) {
                        groupsTableView.getSelectionModel().selectFirst();
                    } else {
                        groupsTableView.getSelectionModel().select(index - 1);
                    }
                } catch (Exception e) {
                    alertError.setContentText(resources.getString("app.alert.groups.del"));
                    alertError.setException(e);
                    alertError.show();
                }
            }
        }
    }

    public void refreshGroupContextMenu(ActionEvent actionEvent) {
        refreshGroupsTable();
    }

    private void refreshGroupsTable(){
        User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            ObservableList<String> allowedGroups = model.allowedGroups(selectedUser);
            if (!allowedGroups.isEmpty()) {
                groupsTableView.setEditable(true);
                gcode.setCellFactory(ChoiceBoxTableCell.forTableColumn(allowedGroups));
            } else {
                groupsTableView.setEditable(false);
            }
            groupsTableView.getItems().clear();
            groupsTableView.getItems().addAll(selectedUser.getGroups());
            groupsTableView.getSelectionModel().selectFirst();
        }
    }
}
