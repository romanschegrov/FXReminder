package ru.schegrov.entity;

import javafx.beans.property.SimpleStringProperty;

import javax.persistence.*;

/**
 * Created by ramon on 24.08.2016.
 */
@Entity
@Table(name = "T_FXR_GROUP")
public class Group {
    private SimpleStringProperty code = new SimpleStringProperty("");
    private SimpleStringProperty descr = new SimpleStringProperty("");

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
}
