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
public class UsersTabModel {

    public ObservableList<String> allowedGroups(User user){
        ObservableList<String> allowedList = FXCollections.observableArrayList();
        ObjectDao<Group> dao = new ObjectDao<>(Group.class);
        List<Group> groups = dao.getAll();
        groups.forEach(group -> allowedList.add(group.getCode()));

        ObservableList<String> userGroupsString = FXCollections.observableArrayList();
        List<Group> userGroups = user.getGroups();
        userGroups.forEach(group -> userGroupsString.add(group.getCode()));

        allowedList.removeAll(userGroupsString);
        return allowedList;
    }
}
