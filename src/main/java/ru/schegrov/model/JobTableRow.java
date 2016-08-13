package ru.schegrov.model;

import javafx.beans.property.SimpleStringProperty;

import javax.persistence.Entity;

/**
 * Created by ramon on 08.08.2016.
 */
@Entity
public class JobTableRow {
    private  SimpleStringProperty column1= new SimpleStringProperty();
    private  SimpleStringProperty column2= new SimpleStringProperty();
    private  SimpleStringProperty column3= new SimpleStringProperty();
    private  SimpleStringProperty column4= new SimpleStringProperty();
    private  SimpleStringProperty column5= new SimpleStringProperty();
    private  SimpleStringProperty column6= new SimpleStringProperty();
    private  SimpleStringProperty column7= new SimpleStringProperty();
    private  SimpleStringProperty column8= new SimpleStringProperty();
    private  SimpleStringProperty column9= new SimpleStringProperty();

    public JobTableRow(){}
//        column1 = new SimpleStringProperty();
//        column2 = new SimpleStringProperty();
//        column3 = new SimpleStringProperty();
//        column4 = new SimpleStringProperty();
//        column5 = new SimpleStringProperty();
//        column6 = new SimpleStringProperty();
//        column7 = new SimpleStringProperty();
//        column8 = new SimpleStringProperty();
//        column9 = new SimpleStringProperty();
//    }

    public String getColumn1() {
        return column1.get();
    }

    public SimpleStringProperty column1Property() {
        return column1;
    }

    public void setColumn1(String column1) {
        this.column1.set(column1);
    }

    public String getColumn2() {
        return column2.get();
    }

    public SimpleStringProperty column2Property() {
        return column2;
    }

    public void setColumn2(String column2) {
        this.column2.set(column2);
    }

    public String getColumn3() {
        return column3.get();
    }

    public SimpleStringProperty column3Property() {
        return column3;
    }

    public void setColumn3(String column3) {
        this.column3.set(column3);
    }

    public String getColumn4() {
        return column4.get();
    }

    public SimpleStringProperty column4Property() {
        return column4;
    }

    public void setColumn4(String column4) {
        this.column4.set(column4);
    }

    public String getColumn5() {
        return column5.get();
    }

    public SimpleStringProperty column5Property() {
        return column5;
    }

    public void setColumn5(String column5) {
        this.column5.set(column5);
    }

    public String getColumn6() {
        return column6.get();
    }

    public SimpleStringProperty column6Property() {
        return column6;
    }

    public void setColumn6(String column6) {
        this.column6.set(column6);
    }

    public String getColumn7() {
        return column7.get();
    }

    public SimpleStringProperty column7Property() {
        return column7;
    }

    public void setColumn7(String column7) {
        this.column7.set(column7);
    }

    public String getColumn8() {
        return column8.get();
    }

    public SimpleStringProperty column8Property() {
        return column8;
    }

    public void setColumn8(String column8) {
        this.column8.set(column8);
    }

    public String getColumn9() {
        return column9.get();
    }

    public SimpleStringProperty column9Property() {
        return column9;
    }

    public void setColumn9(String column9) {
        this.column9.set(column9);
    }
}
