package com.mavl.im.util;

import android.text.TextUtils;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class TimeUtil {

    public static final double ONE_DAY = 24 * 60 * 60;
    private static final String YMDHMS_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String search_DateFormat = "MM/dd/yyyy HH:mm:ss";
    private static final String TIME_ZERO = "00:00";
    private static final String TIME_MAX = "23:59:59";

    public static Date stringConvertDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        Date data = null;
        try {
            data = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static Date stringConvertDate(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date data = null;
        try {
            data = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String getUSDateTimeFormat(long timeStamp) {
        SimpleDateFormat usSdf = new SimpleDateFormat("HH:mm, MMMM dd, yyyy", Locale.US);
        return usSdf.format(new Date(timeStamp));
    }

    public static String getDateTimeFormat(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm aa");
        return sdf.format(new Date(timeStamp));
    }

    public static String getCurrentTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 以秒为单位的时间戳。
     */
    public static long timeStampWithSecondUnit() {
        return System.currentTimeMillis() / 1000;
    }

    public static String getDataTimeFormat(long timeStamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(timeStamp));
    }

    /**
     * 将timeStamp转化为12小时制时分格式。
     *
     * @param timeStamp 时间戳。
     * @return 12小时制时分。
     */
    public static String getHourMinFormat(long timeStamp) {
        timeStamp *= 1000;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        Date date = new Date(timeStamp);
        return sdf.format(date);
    }

    /**
     * 获取过去几天，默认不包含今天。
     *
     * @param dayCount 天数。
     * @see #getSeveralPastDays(int, boolean)
     */
    public static List<String> getSeveralPastDays(int dayCount) {
        return getSeveralPastDays(dayCount, false);
    }

    /**
     * 获取过去几天。
     *
     * @param dayCount      天数。
     * @param todayIncluded 是否包含今天。
     */
    public static List<String> getSeveralPastDays(int dayCount, boolean todayIncluded) {
        List<String> days = new ArrayList<>();
        long timeStamp = System.currentTimeMillis();
        long day = 24 * 60 * 60 * 1000;
        int dayOffset = todayIncluded ? 0 : 1;
        for (int i = dayOffset; i < dayCount + dayOffset; i++) {
            days.add(getDataTimeFormat(timeStamp - i * day, "MM/dd"));
        }
        return days;
    }

    /**
     * 转换为以天为单位的时间戳。
     * 注意东八区的时间偏差。
     *
     * @param timestamp 以秒为单位的时间戳。
     */
    public static int getDaysPast(long timestamp) {
        return (int) (timestamp + 8 * 60 * 60) / (60 * 60 * 24);
    }

    /**
     * local ---> UTC
     *
     * @return
     */
    public static String LocalToUTC() {
        SimpleDateFormat sdf = new SimpleDateFormat(YMDHMS_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String gmtTime = sdf.format(new Date());
        return gmtTime;
    }

    /**
     * UTC --->local
     *
     * @param utcTime UTC
     * @return
     */
    public static String utcToLocal(String utcTime) {
        try {
            if (TextUtils.isEmpty(utcTime)) {
                return "";
            }
            SimpleDateFormat utcFormater = new SimpleDateFormat(YMDHMS_FORMAT);
            utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date gpsUTCDate = null;
            try {
                gpsUTCDate = utcFormater.parse(utcTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat localFormater = new SimpleDateFormat(YMDHMS_FORMAT);
            localFormater.setTimeZone(TimeZone.getDefault());
            String localTime = localFormater.format(gpsUTCDate.getTime());
            return localTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @return
     */
    public static String timeStamp2Date(long seconds, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(seconds));
    }

    public static String longToString(long longNum, String dateFormat) {
        if (TextUtils.isEmpty(dateFormat)) {
            dateFormat = YMDHMS_FORMAT;
        }
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        Date date = new Date(longNum);
        return format.format(date);
    }

    public static String unitFormat(int i) {
        String retStr = null;
        try {
            if (i >= 0 && i < 10)
                retStr = "0" + Integer.toString(i);
            else
                retStr = "" + i;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retStr;
    }

    public static long searchTimeToLong(String time) {
        if (TextUtils.isEmpty(time)) {
            return 0L;
        }
        try {
            String[] split = time.split(" ");
            String tempTime = split[0] + " " + split[1];
            int diff = 0;
            if ("pm".equals(split[2])) {
                diff = 1000 * 12 * 60 * 60;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(search_DateFormat);
            sdf.setTimeZone(TimeZone.getDefault());
            Date startTime = null;
            startTime = sdf.parse(tempTime);
            return (startTime.getTime() + diff);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static String searchTimeFormat(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        try {
            String date = (String) DateFormat.format("yyyy-MM-dd HH:mm:ss", searchTimeToLong(time));
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
