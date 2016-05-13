package com.overclocked.timeit.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        return TimeUnit.MINUTES.toMillis((hour * 60) + minutes);
    }

    public static String getDateAsText(Calendar calendar){
        DateFormat df = new SimpleDateFormat(AppConstants.SWIPE_DATE_FORMAT);
        String date = df.format(calendar.getTime());
        return date;
    }

    public static int getWeekNumberOfTodayDate(){
        Calendar cal = Calendar.getInstance();
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        return week;
    }

    public static String convertMillisToHoursMinutes(Long timeInMillis){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        Date resultDate = new Date(timeInMillis);
        return sdf.format(resultDate);
    }

    public static int convertSwipeTimeToHours(Long timeInMillis){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static int convertSwipeTimeToMinutes(Long timeInMillis){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        return cal.get(Calendar.MINUTE);
    }

    public static String convertMillisToHoursMinutesSeconds(Long millis){
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    public static String convertMillisToHours(Long timeInMillis){
        return String.format("%02d", TimeUnit.MILLISECONDS.toHours(timeInMillis));
    }

    public static String convertMillisToMinutes(Long timeInMillis){
        return String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % TimeUnit.HOURS.toMinutes(1));
    }

    public static boolean isTodayOutOfSelectedDays(int today, int startDay, int endDay){
        boolean result = false;
        if(startDay < endDay){
            if(today >= startDay && today<=endDay){
                result = false;
            }else{
                result = true;
            }
        }else if(startDay > endDay){
            if((7-today) >= endDay && (7-today)<=startDay){
                result = false;
            }else{
                result = true;
            }
        }
        return result;
    }

    public static int getDaysDifference(int start, int end){
        if(start < end){
            return (end - start) + 1;
        }else{
            return  (7 - start) + 1 + end;
        }
    }

}
