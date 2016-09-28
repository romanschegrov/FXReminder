package ru.schegrov.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import javax.persistence.*;

/**
 * Created by ramon on 10.08.2016.
 */
@Entity
@Table(name = "T_FXR_CONDITIONS" /*, uniqueConstraints = {@UniqueConstraint(name = "T_FXR_CONDITIONS", columnNames = {"CODE"})}*/)
public class JobCondition {
    private SimpleIntegerProperty id = new SimpleIntegerProperty();
    private SimpleStringProperty code = new SimpleStringProperty("");
    private SimpleStringProperty value = new SimpleStringProperty("");
    private Job job;

    public JobCondition() {
    }

    public JobCondition(String code, String value) {
        this.code.set(code);
        this.value.set(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobCondition that = (JobCondition) o;
        return that.getId() == getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Id
    @TableGenerator(
            name = "GEN",
            table = "T_FXR_SEQ",
            pkColumnName = "NAME",
            pkColumnValue = "CONDITIONS",
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

    @Column(length = 4000)
    public String getValue() {
        return value.get();
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    @ManyToOne
    @JoinColumn(name = "JOB_ID", foreignKey = @ForeignKey(name = "T_FXR_CONDITIONS_FKJ"))
    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
