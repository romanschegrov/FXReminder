package ru.schegrov.entity;

import javafx.beans.property.*;
import javafx.collections.FXCollections;

import javax.persistence.*;
import java.util.List;

/**
 * Created by ramon on 22.08.2016.
 */
@Entity
@Table(name = "T_FXR_USERS", uniqueConstraints = {@UniqueConstraint(name = "T_FXR_USERS_UK1", columnNames = {"CODE"})})
public class User {

    private SimpleIntegerProperty id = new SimpleIntegerProperty();
    private SimpleStringProperty code = new SimpleStringProperty("");
    private SimpleStringProperty descr = new SimpleStringProperty("");
    private SimpleBooleanProperty admin = new SimpleBooleanProperty(false);
    private List<Group> groups = FXCollections.observableArrayList();

    public User() {}


    public User(String code) {
        this.code.set(code);
    }

    @Id
    @TableGenerator(
            name = "GEN",
            table = "T_FXR_SEQ",
            pkColumnName = "NAME",
            pkColumnValue = "USERS",
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

    public boolean getAdmin() {
        return admin.get();
    }

    public SimpleBooleanProperty adminProperty() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin.set(admin);
    }

    @ManyToMany(fetch = FetchType.EAGER/*, cascade = CascadeType.ALL*/)
    @JoinTable(name = "T_FXR_UG",
            joinColumns = {@JoinColumn(name = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "GROUP_ID")},
            foreignKey = @ForeignKey(name = "T_FXR_UG_FKU"),
            inverseForeignKey = @ForeignKey(name = "T_FXR_UG_FKG"),
            uniqueConstraints = @UniqueConstraint(name = "T_FXR_UG_UK1",
                                                  columnNames = {"USER_ID","GROUP_ID"}))
    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return code.equals(user.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
