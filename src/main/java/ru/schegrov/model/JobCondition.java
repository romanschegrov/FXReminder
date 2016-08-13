package ru.schegrov.model;

/**
 * Created by ramon on 10.08.2016.
 */
public class JobCondition {
    private String code;
    private String value;

    public JobCondition(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
