package com.eternalsrv.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    private TimeUtils() {
    }

    public static String getTimeSpan(Date date) {

        long diff = System.currentTimeMillis() - date.getTime();
        double hours = diff / (60 * 60 * 1000);


        if (hours < 1) {
            long minutes = Math.round(diff / (60 * 1000));
            return minutes + " minutes ago";
        } else if(hours < 24) {
            if (Math.round(hours) == 1)
                return "1 hour ago";
            else
                return Math.round(hours) + " hours ago";
        } else {
            long days = Math.round(diff / (24.0 * 60 * 60 * 1000));

            if (days == 0)
                return "today";
            else if (days == 1)
                return "yesterday";
            else if (days < 14)
                return days + " days ago";
            else if (days < 30)
                return ((int) (days / 7)) + " weeks ago";
            else if (days < 365)
                return ((int) (days / 30)) + " months ago";
            else
                return ((int) (days / 365)) + " years ago";
        }
    }

    public static String getTime(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(new Date(milliseconds));
    }

    public static String getDate(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
        return dateFormat.format(new Date(milliseconds));
    }

    public static long getDateAsHeaderId(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(milliseconds)));
    }
}
