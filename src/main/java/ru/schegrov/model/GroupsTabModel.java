package ru.schegrov.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.schegrov.dao.ObjectDao;
import ru.schegrov.entity.Group;
import ru.schegrov.entity.User;

import java.util.List;

/**
 * Created by ramon on 30.08.2016.
 */
public class GroupsTabModel {

    public ObservableList<String> allowedUsers(Group group){
        ObservableList<String> allowedList = FXCollections.observableArrayList();
        ObjectDao<User> dao = new ObjectDao<>(User.class);
        List<User> users = dao.getAll();
        users.forEach(user -> allowedList.add(user.getCode()));

        ObservableList<String> groupUsersString = FXCollections.observableArrayList();
        List<User> groupUsers = group.getUsers();
        groupUsers.forEach(user -> groupUsersString.add(user.getCode()));

        allowedList.removeAll(groupUsersString);
        return allowedList;
    }
}
