package ru.schegrov.model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import javax.persistence.Entity;

/**
 * Created by ramon on 08.08.2016.
 */
@Entity
public class JobTableRow {
    private SimpleObjectProperty column1;
    private SimpleObjectProperty column2;
    private SimpleObjectProperty column3;

    public JobTableRow(){}

    public void setColumn(Object column) {
        if (column1 == null){
            column1 = new SimpleObjectProperty();
            this.column1.set(column);
        }

        if (column2 == null){
            column2 = new SimpleObjectProperty();
            this.column2.set(column);
        }

        if (column3 == null){
            column3 = new SimpleObjectProperty();
            this.column3.set(column);
        }
    }
}
