package ru.schegrov.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;

import javax.persistence.*;
import java.util.List;

/**
 * Created by ramon on 24.08.2016.
 */
@Entity
@Table(name = "T_FXR_GROUP", uniqueConstraints = {@UniqueConstraint(name = "T_FXR_GROUP_UK1", columnNames = {"CODE"})})
public class Group {

    private SimpleIntegerProperty id = new SimpleIntegerProperty();
    private SimpleStringProperty code = new SimpleStringProperty("");
    private SimpleStringProperty descr = new SimpleStringProperty("");
    private List<User> users = FXCollections.observableArrayList();

    public Group() {}

    @Id
    @TableGenerator(
            name = "GEN",
            table = "T_FXR_SEQ",
            pkColumnName = "NAME",
            pkColumnValue = "GROUPS",
            valueColumnName = "NUM",
            allocationSize = 1)
    @GeneratedValue(generator = "GEN")
    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getCode() {
        return code.get();
    }

    public SimpleStringProperty codeProperty() {
        return code;
    }

    public void setCode(String code) {
        this.code.set(code);
    }

    public String getDescr() {
        return descr.get();
    }

    public SimpleStringProperty descrProperty() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr.set(descr);
    }

    @ManyToMany(mappedBy = "groups", fetch = FetchType.EAGER)
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return code.equals(group.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
