package ru.schegrov.entity;

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
    private ObservableList <TableColumn<JobTableRow,String>>
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
    public ObservableList<TableColumn <JobTableRow, String> > getColumns() {
        return columns;
    }

    public JobCondition getCondition(String code) {
        for (Iterator<JobCondition> iterator = conditions.iterator(); iterator.hasNext(); ){
            JobCondition jobCondition = iterator.next();
            if (jobCondition.getCode().equals(code)) {
                return jobCondition;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Job job1 = (Job) o;

        if (!id.equals(job1.id)) return false;
        if (!parent_id.equals(job1.parent_id)) return false;
        if (name != null ? !name.equals(job1.name) : job1.name != null) return false;
        if (!job.equals(job1.job)) return false;
        return conditions != null ? conditions.equals(job1.conditions) : job1.conditions == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + parent_id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + job.hashCode();
        result = 31 * result + (conditions != null ? conditions.hashCode() : 0);
        return result;
    }

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

    public void setConditions(List<JobCondition> conditions) {
        this.conditions = conditions;
    }

    @OneToMany(mappedBy = "job", fetch = FetchType.EAGER)
    public List<JobCondition> getConditions() {
        return conditions;
    }
}
