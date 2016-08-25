package ru.schegrov.util;

import javafx.util.StringConverter;

import java.util.ResourceBundle;

/**
 * Created by ramon on 25.08.2016.
 */
public class BooleanStringConverter extends StringConverter<Boolean> {

    private String yes;
    private String no;

    public BooleanStringConverter(ResourceBundle resources) {
        yes = resources.getString("app.tabpane.tab.users.admin.yes");
        no = resources.getString("app.tabpane.tab.users.admin.no");
    }

    @Override
    public String toString(Boolean object) {
        return object ? yes : no;
    }

    @Override
    public Boolean fromString(String string) {
        return string.equals(yes) ? true : false;
    }
}
