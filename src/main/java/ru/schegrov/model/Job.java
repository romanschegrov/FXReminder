package ru.schegrov.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by ramon on 13.07.2016.
 */
public class Job {

    private StringProperty name;

    public Job(String name) {
        this.name = new SimpleStringProperty(name);
    }

    @Override
    public String toString() {
        return "Job{" +
                "name=" + name +
                '}';
    }
}
