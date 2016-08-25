package ru.schegrov.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import ru.schegrov.dao.ObjectDao;
import ru.schegrov.entity.User;
import ru.schegrov.util.AlertHelper;
import ru.schegrov.util.BooleanStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ramon on 22.08.2016.
 */
public class UsersTabController implements Initializable {

    private AlertHelper alertError;
    private ResourceBundle resources;
    @FXML private TableView<User> usersTableView;
    @FXML private TableColumn<User,String> code;
    @FXML private TableColumn<User,String> descr;
    @FXML private TableColumn<User,Boolean> admin;

    public void addContextMenu(ActionEvent actionEvent) {
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

    public void delContextMenu(ActionEvent actionEvent) {
        User user = usersTableView.getSelectionModel().getSelectedItem();
        if (user != null) {
            try {
                ObjectDao<User> dao = new ObjectDao<>(User.class);
                dao.delete(user);
                usersTableView.getItems().remove(user);
            } catch (Exception e) {
                alertError.setContentText(resources.getString("app.alert.users.del"));
                alertError.setException(e);
                alertError.show();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        alertError = new AlertHelper(Alert.AlertType.ERROR);
        alertError.setTitle(resources.getString("app.alert.title"));
        code.setOnEditCommit(event -> {
            event.getRowValue().setCode(event.getNewValue());
            edit(event.getRowValue());
        });
        descr.setOnEditCommit(event -> {
            event.getRowValue().setDescr(event.getNewValue());
            edit(event.getRowValue());
        });
        BooleanStringConverter converter  = new BooleanStringConverter(resources);
        admin.setCellFactory(ChoiceBoxTableCell.forTableColumn(converter, true, false));
        admin.setOnEditCommit(event -> {
            event.getRowValue().setAdmin(event.getNewValue());
            edit(event.getRowValue());
        });
    }

    private void edit(User user){
        try {
            ObjectDao<User> dao = new ObjectDao<>(User.class);
            dao.update(user);
        } catch (Exception e) {
            alertError.setContentText(resources.getString("app.alert.users.update"));
            alertError.setException(e);
            alertError.show();
        }
    }
}
