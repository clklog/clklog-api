package com.zcunsoft.clklog.api.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimeUtils {
    public static long[] getCurrentWeekTimeFrame(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        //start of the week
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK) - 2));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        //end of the week
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        long endTime = calendar.getTimeInMillis();
        return new long[]{startTime, endTime};
    }

    public static long[] getCurrentMonthTimeFrame(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        //start of the month
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        //end of the month
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        long endTime = calendar.getTimeInMillis();

        return new long[]{startTime, endTime};
    }

    public static long[] getCurrentYearTimeFrame(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        //start of the year
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        //end of the year
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.DATE, -1);
        long endTime = calendar.getTimeInMillis();

        return new long[]{startTime, endTime};
    }
    
    /**
     * 把时间戳转换为：时分秒
     * @param second
     * @return
     */
    public static String getTimeString(long second) {
        if (second < 1) {
            return "00:00:00";
        }
//        long millisecond = second*1000;
//        long second = millisecond / 1000;
        long seconds = second % 60;
        long minutes = second / 60;
        long hours = 0;
        if (minutes >= 60) {
            hours = minutes / 60;
            minutes = minutes % 60;
        }
        String timeString = "";
        String secondString = "";
        String minuteString = "";
        String hourString = "";
        if (seconds < 10) {
            secondString = "0" + seconds;
        } else {
            secondString = seconds + "";
        }
        if (minutes < 10 && hours < 1) {
            minuteString = "0"+minutes + "";
        } else if (minutes < 10){
            minuteString =  "0" + minutes + "";
        } else {
            minuteString = minutes + "";
        }
        if (hours < 10) {
            hourString = "0"+hours + "";
        } else {
            hourString = hours + "" + "";
        }
        if (hours != 0) {
            timeString = hourString +":"+ minuteString +":"+ secondString;
        } else {
            timeString = "00:"+minuteString +":"+ secondString;
        }
        return timeString;
    }

    
    public static long getCurrentYesterday(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.add(Calendar.DAY_OF_WEEK, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long yesterdayTime = calendar.getTimeInMillis();

        return yesterdayTime;
    }
    
    public static long getGetOneWeekAgoDateByTimestamp(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.add(Calendar.DAY_OF_WEEK, -7);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long yesterdayTime = calendar.getTimeInMillis();

        return yesterdayTime;
    }
    
    /**
    public static long getGetOneMonthAgoDateByTimestamp(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.add(Calendar.WEEK_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long yesterdayTime = calendar.getTimeInMillis();

        return yesterdayTime;
    }
    */
    public static long getCurrentYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, -1);
        long yesterdayTime = calendar.getTimeInMillis();
        return yesterdayTime;
    }
    
    public static long getCurrent() {
    	return Calendar.getInstance().getTimeInMillis();
    }
    
    public static List<Calendar> getPreviousHoursTimes(Integer previousHour){
    	List<Calendar> previousHoursTimes = new ArrayList<>();  
        Calendar now = Calendar.getInstance();  
        for (int i = 0; i < previousHour; i++) {  
            Calendar hourBefore = (Calendar) now.clone();  
            hourBefore.add(Calendar.HOUR_OF_DAY, -(previousHour-i));  
            previousHoursTimes.add(hourBefore);  
        } 
        previousHoursTimes.add(now);
        return previousHoursTimes;  
    }
    
    public static long getCurrentPreviouseMinTime(Integer previousMin) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -previousMin); 
        return calendar.getTimeInMillis();
    }
    
    public static long getCurrentPreviousMonthFirstDay(Timestamp timestamp) {
    	Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.add(Calendar.MONTH, -1);
        
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
    
    public static long getCurrentPreviousMonthLasttDay(Timestamp timestamp) {
    	Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.add(Calendar.MONTH, -1);
        
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
}
