package com.overclocked.timeit.common;

import java.util.concurrent.TimeUnit;

/**
 * Created by Thahaseen on 4/21/2016.
 */
public class AppUtil {

    public static String showTimeInTwelveHours(int hour, int min) {
        String format;
        if (hour == 0) {
            hour += 12;
            format = "AM";
        }
        else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        String minutes;
        if(String.valueOf(min).length()==1){
            minutes = "0" + min;
        }else{
            minutes = ""+min;
        }
        return  new StringBuilder().append(hour).append(" : ").append(minutes)
                .append(" ").append(format).toString();
    }

    public static Long convertHoursMinutesToMillis(int hour, int minutes){
        return TimeUnit.MINUTES.toMillis((hour*60) + minutes);
    }

}
