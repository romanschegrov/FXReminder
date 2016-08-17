package ru.schegrov.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by ramon on 15.08.2016.
 */
public class DateFormatHelper {

    public static String format(Timestamp date){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        String pattern;
        if (calendar.get(Calendar.HOUR_OF_DAY) == 0){
            pattern = "dd-MM-yyyy";
        } else {
            pattern = "dd-MM-yyyy HH:mm:ss";
        }
        return new SimpleDateFormat(pattern).format(date);
    }
}
