package com.magneton.service.core.util;

import java.util.Calendar;
import org.apache.tomcat.jni.Local;

import java.util.Locale;

/**
 * @author zhangmingshuang
 * @since 2019/8/1
 */
public class TimesUtil {

    public static final int nowDistanceTomorrowClockZero() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    public static final String secondsPrettyShow(long seconds) {
        String standardTime;
        if (seconds <= 0) {
            standardTime = "0秒";
        } else if (seconds < 60) {
            standardTime = String.format(
                Locale.getDefault(), "%d秒", seconds % 60);
        } else if (seconds == 60) {
            standardTime = "60秒";
        } else if (seconds < 3600) {
            standardTime = String.format(
                Locale.getDefault(), "%d分%d秒",
                seconds / 60, seconds % 60);
        } else if (seconds == 3600) {
            standardTime = "1小时";
        } else if (seconds < 86400) {
            standardTime = String.format(
                Locale.getDefault(), "%d小时%d分%d秒",
                seconds / 3600, seconds % 3600 / 60, seconds % 60);
        } else if (seconds == 86400) {
            standardTime = "1天";
        } else {
            standardTime = String.format(
                Locale.getDefault(), "%d天%d小时%d分%d秒",
                seconds / 86400, seconds % 86400 / 3600, seconds % 3600 / 60, seconds % 60);
        }
        return standardTime;
    }
}
