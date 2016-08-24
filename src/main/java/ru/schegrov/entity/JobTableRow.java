package ru.schegrov.entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by ramon on 08.08.2016.
 */
public class JobTableRow {
    private SimpleStringProperty column1;
    private SimpleStringProperty column2;
    private SimpleStringProperty column3;
    private SimpleStringProperty column4;
    private SimpleStringProperty column5;
    private SimpleStringProperty column6;
    private SimpleStringProperty column7;
    private SimpleStringProperty column8;
    private SimpleStringProperty column9;


    public JobTableRow(){}

    public void setColumn(String column) {
        if (column1 == null){
            column1 = new SimpleStringProperty();
            this.column1.set(column);
        } else if (column2 == null) {
            column2 = new SimpleStringProperty();
            this.column2.set(column);
        } else if (column3 == null){
            column3 = new SimpleStringProperty();
            this.column3.set(column);
        } else if (column4 == null){
            column4 = new SimpleStringProperty();
            this.column4.set(column);
        } else if (column5 == null){
            column5 = new SimpleStringProperty();
            this.column5.set(column);
        } else if (column6 == null){
            column6 = new SimpleStringProperty();
            this.column6.set(column);
        } else if (column7 == null){
            column7 = new SimpleStringProperty();
            this.column7.set(column);
        } else if (column8 == null){
            column8 = new SimpleStringProperty();
            this.column8.set(column);
        } else if (column9 == null){
            column9 = new SimpleStringProperty();
            this.column9.set(column);
        }
    }

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
