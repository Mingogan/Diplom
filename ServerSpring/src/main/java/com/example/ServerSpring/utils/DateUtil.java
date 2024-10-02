package com.example.ServerSpring.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static String formatToISO(Date date) {
        return isoDateFormat.format(date);
    }

    public static Date trimMilliseconds(Date timestamp) {
        long time = timestamp.getTime();
        return new Timestamp((time / 1000) * 1000);
    }
}
