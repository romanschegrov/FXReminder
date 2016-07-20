package ru.schegrov.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;

@Entity
@Table(name = "T_FXR_JOBS")
public class Job {

    private IntegerProperty id = new SimpleIntegerProperty();
    private IntegerProperty parent_id = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();

    public Job() {}

    @Override
    public String toString() {
        return name.get();
    }

    @Id
    @TableGenerator(
            name = "GEN",
            table = "T_FXR_SEQ",
            pkColumnName = "NAME",
            pkColumnValue = "JOBS",
            valueColumnName = "NUM",
            allocationSize = 1)
    @GeneratedValue(generator = "GEN")
    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getParent_id() {
        return parent_id.get();
    }

    public IntegerProperty parent_idProperty() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id.set(parent_id);
    }
}
