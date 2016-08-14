package ru.schegrov.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
@Table(name = "T_FXR_JOBS")
public class Job {

    private IntegerProperty id = new SimpleIntegerProperty();
    private IntegerProperty parent_id = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();
    private BooleanProperty job = new SimpleBooleanProperty();
    private IntegerProperty count = new SimpleIntegerProperty();
    private List<JobCondition> conditions = new ArrayList<>();
    private ObservableList<JobTableRow>
            rows = FXCollections.observableArrayList();
    private ObservableList<TableColumn<JobTableRow,Object>>
            columns = FXCollections.observableArrayList();

    @Transient
    public int getCount() {
        return count.get();
    }

    public void setCount(int count) {
        this.count.set(count);
    }

    @Transient
    public ObservableList<JobTableRow> getRows() {
        return rows;
    }

    @Transient
    public ObservableList<TableColumn<JobTableRow, Object>> getColumns() {
        return columns;
    }

    @Transient
    public List<JobCondition> getConditions() {
        return conditions;
    }

    public String getCondition(String code) {
        for (Iterator<JobCondition> iterator = conditions.iterator(); iterator.hasNext(); ){
            JobCondition jobCondition = iterator.next();
            if (jobCondition.getCode().equals(code)) {
                return jobCondition.getValue();
            }
        }
        return null;
    }

    public Job() {}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(name.get());
        if (isJob()){
            builder.append(" (");
            builder.append(count.get());
            builder.append(")");
        }
        return  builder.toString();
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

    public boolean isJob() {
        return job.get();
    }

    public BooleanProperty jobProperty() {
        return job;
    }

    public void setJob(boolean job) {
        this.job.set(job);
    }
}
