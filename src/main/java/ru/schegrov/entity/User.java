package ru.schegrov.entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by ramon on 22.08.2016.
 */
public class User {

    private SimpleStringProperty code = new SimpleStringProperty("");
    private SimpleStringProperty descr = new SimpleStringProperty("");

    public User() {}

    public User(SimpleStringProperty code, SimpleStringProperty descr) {
        this.code = code;
        this.descr = descr;
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
}
