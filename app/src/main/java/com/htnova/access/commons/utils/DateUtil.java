package com.htnova.access.commons.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 日期工具类，对旧版的日期类的功能封装。 */
public class DateUtil {
    private static Logger log = LoggerFactory.getLogger(DateUtil.class);

    public static final String DATE_FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_YMDHM = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_YMDH = "yyyy-MM-dd HH";
    public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_YMD1 = "yyyyMMdd";
    public static final String DATE_FORMAT_YM = "yyyy-MM";
    public static final String DATE_FORMAT_Y = "yyyy";
    public static final String DATE_FORMAT_FILE_YMDHMS = "yyyyMMdd_HHmmss";
    public static final String DATE_FORMAT_FILE_OSS_YMDHMS = "yyyyMMddHHmmss";
    public static final String TIME_ZONE_ID = "+8";

    public static Long converterToLong(LocalDateTime date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return Date.from(date.toInstant(ZoneOffset.of(TIME_ZONE_ID))).getTime();
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of(TIME_ZONE_ID));
    }

    public static Date convertKmsDate(String dateStr, String format) {
        SimpleDateFormat simple = new SimpleDateFormat(format, Locale.ENGLISH);
        try {
            simple.setLenient(false);
            return simple.parse(dateStr);
        } catch (Exception e) {
            log.error("字符[{}]转换为日期[{}]时出错，直接返回null。", dateStr, format, e);
            return null;
        }
    }

    /**
     * 把日期字符串格式化成日期类型。
     *
     * @param dateStr
     *            日期字符串。
     * @param format
     *            转换格式。
     * @return 日期类型。
     */
    public static Date convert2Date(String dateStr, String format) {
        SimpleDateFormat simple = new SimpleDateFormat(format);
        try {
            simple.setLenient(false);
            return simple.parse(dateStr);
        } catch (Exception e) {
            log.error("字符[{}]转换为日期[{}]时出错，直接返回null。", dateStr, format, e);
            return null;
        }
    }

    public static Date convert2Date(long timestamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            sdf.setLenient(false);
            return sdf.parse(sdf.format(new Timestamp(timestamp)));
        } catch (Exception e) {
            log.error("长整型[{}]转换为日期[{}]时出错，直接返回null。", timestamp, format, e);
            return null;
        }
    }

    /**
     * 把日期类型格式化成字符串。
     *
     * @param date
     *            日期类型。
     * @param format
     *            转换格式。
     * @return 正常返回日期字符串，异常返回null。
     */
    public static String convert2String(Date date, String format) {
        SimpleDateFormat formater = new SimpleDateFormat(format);
        try {
            return formater.format(date);
        } catch (Exception e) {
            log.error("日期[{}]转换为字符串[{}]时出错，直接返回null。", date, format, e);
            return null;
        }
    }

    public static String convert2String(long timestamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.format(new Timestamp(timestamp));
        } catch (Exception e) {
            log.error("日期[{}]转换为字符串[{}]时出错，直接返回null。", timestamp, format, e);
            return null;
        }
    }

    /** 把日期类型转换为long型的timestamp */
    public static Long convert2Long(Date date) {
        try {
            Timestamp ts = new Timestamp(date.getTime());
            return ts.getTime();
        } catch (Exception e) {
            log.error("日期[{}]转换为时间戳时出错，直接返回null。", date, e);
            return null;
        }
    }

    /**
     * 转sql的timestamp格式。
     *
     * @param date
     *            日期类型。
     * @return sql的timestamp类型。
     */
    public static java.sql.Timestamp convert2SqlTime(Date date) {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
        return timestamp;
    }

    /**
     * 转sql的日期格式。
     *
     * @param date
     *            日期类型。
     * @return sql的date类型。
     */
    public static java.sql.Date convert2SqlDate(Date date) {
        java.sql.Date Datetamp = new java.sql.Date(date.getTime());
        return Datetamp;
    }

    /**
     * 获取当前日期的字符串表示。
     *
     * @param format
     *            转换格式。
     * @return 当前日期的字符串表示。
     */
    public static String getCurrentDate(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    /**
     * 获取当前系统时间戳。
     *
     * @return 系统时间戳对应的长整型。
     */
    public static long getTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取月份的天数。
     *
     * @param year
     *            年。
     * @param month
     *            月。
     * @return 月份对应的天数。
     */
    public static int getDaysOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取日期的年。
     *
     * @param date
     *            日期类型。
     * @return 日期对应的年。
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取日期的月。
     *
     * @param date
     *            日期类型。
     * @return 日期对应的月份。
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取日期的天。
     *
     * @param date
     *            日期类型。
     * @return 日期对应的天。
     */
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DATE);
    }

    /**
     * 获取日期的时。
     *
     * @param date
     *            日期类型。
     * @return 日期对应的小时。
     */
    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR);
    }

    /**
     * 获取日期的分钟。
     *
     * @param date
     *            日期类型。
     * @return 日期对应的分钟。
     */
    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取日期的秒。
     *
     * @param date
     *            日期类型。
     * @return 日期对应的秒。
     */
    public static int getSecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 获取星期几。
     *
     * @param date
     *            日期类型。
     * @return 日期对应的星期几。
     */
    public static int getWeekDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek - 1;
    }

    /**
     * 获取哪一年共有多少周。
     *
     * @param year
     *            年份。
     * @return 年份包含的周数。
     */
    public static int getMaxWeekNumOfYear(int year) {
        Calendar c = new GregorianCalendar();
        c.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        return getWeekNumOfYear(c.getTime());
    }

    /**
     * 取得某天是一年中的多少周。
     *
     * @param date
     *            日期类型。
     * @return 日期对应的周。
     */
    public static int getWeekNumOfYear(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setMinimalDaysInFirstWeek(7);
        c.setTime(date);
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 取得某天所在周的第一天。
     *
     * @param date
     *            日期类型。
     * @return 日期所在周的第一天。
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return c.getTime();
    }

    /**
     * 取得某天所在周的最后一天。
     *
     * @param date
     *            日期类型。
     * @return 日期所在周的最后一天。
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
        return c.getTime();
    }

    /**
     * 取得某年某周的第一天，对于交叉：2008-12-29到2009-01-04属于2008年的最后一周， 2009-01-05为2009年第一周的第一天。
     *
     * @param year
     *            年份。
     * @param week
     *            第几周。
     * @return 该周对应的第一天。
     */
    public static Date getFirstDayOfWeek(int year, int week) {
        Calendar calFirst = Calendar.getInstance();
        calFirst.set(year, 0, 7);
        Date firstDate = getFirstDayOfWeek(calFirst.getTime());

        Calendar firstDateCal = Calendar.getInstance();
        firstDateCal.setTime(firstDate);

        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, firstDateCal.get(Calendar.DATE));

        Calendar cal = (GregorianCalendar)c.clone();
        cal.add(Calendar.DATE, (week - 1) * 7);
        firstDate = getFirstDayOfWeek(cal.getTime());

        return firstDate;
    }

    /**
     * 取得某年某周的最后一天，对于交叉：2008-12-29到2009-01-04属于2008年的最后一周， 2009-01-04为2008年最后一周的最后一天。
     *
     * @param year
     *            年份。
     * @param week
     *            第几周。
     * @return 该周对应的最后一天。
     */
    public static Date getLastDayOfWeek(int year, int week) {
        Calendar calLast = Calendar.getInstance();
        calLast.set(year, 0, 7);
        Date firstDate = getLastDayOfWeek(calLast.getTime());

        Calendar firstDateCal = Calendar.getInstance();
        firstDateCal.setTime(firstDate);

        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, firstDateCal.get(Calendar.DATE));

        Calendar cal = (GregorianCalendar)c.clone();
        cal.add(Calendar.DATE, (week - 1) * 7);
        Date lastDate = getLastDayOfWeek(cal.getTime());

        return lastDate;
    }

    /**
     * 对日期的增加操作。 1-年份，2-月份，3-星期，5-日期，11-小时，12-分钟，13-秒，14-毫秒。
     *
     * @param date
     *            日期类型。
     * @param calendarField
     *            操作类型。
     * @param amount
     *            增加的数量。
     * @return 增加后的日期类型。
     */
    private static Date add(Date date, int calendarField, int amount) {
        if (date == null) {
            log.error("日期为空，无法进行add操作");
            throw new IllegalArgumentException("The date must not be null");
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(calendarField, amount);
            return c.getTime();
        }
    }

    /**
     * 增加年。
     *
     * @param date
     *            日期类型。
     * @param amount
     *            增加的年数。
     * @return 增加后的日期。
     */
    public static Date addYears(Date date, int amount) {
        return add(date, 1, amount);
    }

    /**
     * 增加月。
     *
     * @param date
     *            日期类型。
     * @param amount
     *            增加的月数。
     * @return 增加后的日期。
     */
    public static Date addMonths(Date date, int amount) {
        return add(date, 2, amount);
    }

    /**
     * 增加周。
     *
     * @param date
     *            日期类型。
     * @param amount
     *            增加的周数。
     * @return 增加后的日期。
     */
    public static Date addWeeks(Date date, int amount) {
        return add(date, 3, amount);
    }

    /**
     * 增加天。
     *
     * @param date
     *            日期类型。
     * @param amount
     *            增加的天数。
     * @return 增加后的日期。
     */
    public static Date addDays(Date date, int amount) {
        return add(date, 5, amount);
    }

    /**
     * 增加时。
     *
     * @param date
     *            日期类型。
     * @param amount
     *            增加的小时数。
     * @return 增加后的日期。
     */
    public static Date addHours(Date date, int amount) {
        return add(date, 11, amount);
    }

    /**
     * 增加分。
     *
     * @param date
     *            日期类型。
     * @param amount
     *            增加的分钟数。
     * @return 增加后的日期。
     */
    public static Date addMinutes(Date date, int amount) {
        return add(date, 12, amount);
    }

    /**
     * 增加秒。
     *
     * @param date
     *            日期类型。
     * @param amount
     *            增加的秒数。
     * @return 增加后的日期。
     */
    public static Date addSeconds(Date date, int amount) {
        return add(date, 13, amount);
    }

    /**
     * 增加毫秒。
     *
     * @param date
     *            日期类型。
     * @param amount
     *            增加的毫秒数。
     * @return 增加后的日期。
     */
    public static Date addMilliseconds(Date date, int amount) {
        return add(date, 14, amount);
    }

    /**
     * 毫秒差计算。
     *
     * @param before
     *            被减数日期。
     * @param after
     *            减数日期。
     * @return after-before（毫秒数长整型）。
     */
    public static long diffTimes(Date before, Date after) {
        return after.getTime() - before.getTime();
    }

    /**
     * 秒差计算。
     *
     * @param before
     *            被减数日期。
     * @param after
     *            减数日期。
     * @return after-before（秒数长整型）。
     */
    public static long diffSecond(Date before, Date after) {
        return (after.getTime() - before.getTime()) / 1000;
    }

    /**
     * 分钟差计算。
     *
     * @param before
     *            被减数日期。
     * @param after
     *            减数日期。
     * @return after-before（分钟数长整型）。
     */
    public static int diffMinute(Date before, Date after) {
        return (int)(after.getTime() - before.getTime()) / 1000 / 60;
    }

    /**
     * 小时差计算。
     *
     * @param before
     *            被减数日期。
     * @param after
     *            减数日期。
     * @return after-before（小时数长整型）。
     */
    public static int diffHour(Date before, Date after) {
        return (int)(after.getTime() - before.getTime()) / 1000 / 60 / 60;
    }

    /**
     * 天数差计算。
     *
     * @param before
     *            被减数日期。
     * @param after
     *            减数日期。
     * @return after-before（天数长整型）。
     */
    public static int diffDay(Date before, Date after) {
        return Integer.parseInt(String.valueOf(((after.getTime() - before.getTime()) / 86400000)));
    }

    /**
     * 月差计算。
     *
     * @param before
     *            被减数日期。
     * @param after
     *            减数日期。
     * @return after-before（月数长整型）。
     */
    public static int diffMonth(Date before, Date after) {
        int monthAll = 0;
        int yearsX = diffYear(before, after);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(before);
        c2.setTime(after);
        int monthsX = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        monthAll = yearsX * 12 + monthsX;
        int daysX = c2.get(Calendar.DATE) - c1.get(Calendar.DATE);
        if (daysX > 0) {
            monthAll = monthAll + 1;
        }
        return monthAll;
    }

    /**
     * 年差计算。
     *
     * @param before
     *            被减数日期。
     * @param after
     *            减数日期。
     * @return after-before（年数长整型）。
     */
    public static int diffYear(Date before, Date after) {
        return getYear(after) - getYear(before);
    }

    /**
     * 每隔一秒取一条数据
     *
     * @param dStart
     * @param dEnd
     * @return
     */
    public static List<Date> findDates(Date dStart, Date dEnd, int timeType, int interval) {
        Calendar cStart = Calendar.getInstance();
        cStart.setTime(dStart);

        List dateList = new ArrayList();
        // 别忘了，把起始日期加上。
        dateList.add(dStart);
        // 此日期是否在指定日期之后。
        while (dEnd.after(cStart.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量。
            if (timeType == 1) {
                cStart.add(Calendar.MINUTE, interval);
            } else if (timeType == 2) {
                cStart.add(Calendar.HOUR, interval);
            } else if (timeType == 4) {
                cStart.add(Calendar.DAY_OF_YEAR, interval);
            } else {
                cStart.add(Calendar.SECOND, interval);
            }
            dateList.add(cStart.getTime());
        }
        return dateList;
    }

    /**
     * 获取上一分钟时间
     *
     * @param date
     * @return
     */
    public static Date upMinutes(Date date) {
        Calendar cStart = Calendar.getInstance();
        cStart.setTime(date);
        cStart.add(Calendar.MINUTE, -1);
        return cStart.getTime();
    }

    /**
     * 获取当天所有分钟
     *
     * @param
     * @return
     */
    public static List<String> getDayMinutes() {
        List<String> list = new ArrayList<>();
        for (int h = 0, m = 0; h < 24; m += 1) {
            if (m >= 60) {
                h++;
                m = 0;
            }
            if (h >= 24) {
                break;
            }
            String hour = String.valueOf(h);
            String minute = String.valueOf(m);
            if (hour.length() < 2) {
                hour = "0" + hour;
            }
            if (minute.length() < 2) {
                minute = "0" + minute;
            }
            list.add(hour + ":" + minute);
        }
        return list;
    }

    /**
     * 获取所有天
     *
     * @return
     */
    public static List<String> getMonthDays(int num) {
        List<String> list = new ArrayList<>();
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        aCalendar.add(Calendar.MONTH, num);
        int year = aCalendar.get(Calendar.YEAR);
        int month = aCalendar.get(Calendar.MONTH);
        int day = aCalendar.getActualMaximum(Calendar.DATE);
        String monthend = null;
        if (month < 9) {
            monthend = "0" + (month + 1);
        } else {
            monthend = (month + 1) + "";
        }
        for (int i = 1; i <= day; i++) {
            if (i < 10) {
                String aDate = year + "-" + monthend + "-0" + i;
                list.add(aDate);
                continue;
            }
            String aDate = year + "-" + monthend + "-" + i;
            list.add(aDate);
        }
        return list;
    }

    /**
     * 天最早时间与最晚时间
     *
     * @param date
     * @return
     */
    public static Date getDayBeginTime(Date date) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(date);
        todayStart.set(Calendar.HOUR_OF_DAY, 00);
        todayStart.set(Calendar.MINUTE, 00);
        todayStart.set(Calendar.SECOND, 00);
        todayStart.set(Calendar.MILLISECOND, 000);
        return todayStart.getTime();
    }

    public static Date getDayEndTime(Date date) {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.setTime(date);
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    /** 月份第一天与最后一天 */
    public static Date getMonthBegin(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        // 设置为1号,当前日期既为本月第一天
        c.set(Calendar.DAY_OF_MONTH, 1);
        // 将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        // 将分钟至0
        c.set(Calendar.MINUTE, 0);
        // 将秒至0
        c.set(Calendar.SECOND, 0);
        // 将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        // 获取本月第一天的时间戳
        return c.getTime();
    }

    public static Date getMonthEnd(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        // 设置为当月最后一天
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        // 将小时至23
        c.set(Calendar.HOUR_OF_DAY, 23);
        // 将分钟至59
        c.set(Calendar.MINUTE, 59);
        // 将秒至59
        c.set(Calendar.SECOND, 59);
        // 将毫秒至999
        c.set(Calendar.MILLISECOND, 999);
        // 获取本月最后一天的时间戳
        return c.getTime();
    }

    public static List<String> getDays() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            if (i < 10) {
                list.add("0" + String.valueOf(i));
            } else {
                list.add(String.valueOf(i));
            }
        }
        return list;
    }

    public static List<String> getMonths() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            if (i < 10) {
                list.add("0" + String.valueOf(i));
            } else {
                list.add(String.valueOf(i));
            }
        }
        return list;
    }

    /**
     * 获取一段时间内一定步长的分钟数
     *
     * @param
     * @return
     */
    public static List<String> getStepMinutes(long beginTime, long endTime, int step) {
        List<String> list = new ArrayList<>();
        while (beginTime < endTime) {
            String axis = DateUtil.convert2String(beginTime, DateUtil.DATE_FORMAT_YMDHM);
            list.add(axis);
            beginTime += step * 60 * 1000;
        }
        return list;
    }

    /**
     * 获取一段时间内一定步长的小时数
     *
     * @param
     * @return
     */
    public static List<String> getStepHours(long beginTime, long endTime, int step) {
        List<String> list = new ArrayList<>();
        while (beginTime < endTime) {
            String axis = DateUtil.convert2String(beginTime, DateUtil.DATE_FORMAT_YMDH);
            list.add(axis);
            beginTime += step * 60 * 60 * 1000;
        }
        return list;
    }

    /**
     * 获取一段时间内一定步长的天数
     *
     * @param
     * @return
     */
    public static List<String> getStepDays(long beginTime, long endTime, int step) {
        List<String> list = new ArrayList<>();
        while (beginTime < endTime) {
            String axis = DateUtil.convert2String(beginTime, DateUtil.DATE_FORMAT_YMD);
            list.add(axis);
            beginTime += step * 24 * 60 * 60 * 1000;
        }
        return list;
    }

    /**
     * 获取指定年月的第一天
     *
     * @param year
     * @param month
     * @return
     */
    public static String getFirstDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置月份
        cal.set(Calendar.MONTH, month - 1);
        // 获取某月最小天数
        int firstDay = cal.getMinimum(Calendar.DATE);
        // 设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        return convert2String(cal.getTime(), DateUtil.DATE_FORMAT_YMD);
    }

    /**
     * 获取指定年月的最后一天
     *
     * @param year
     * @param month
     * @return
     */
    public static String getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置月份
        cal.set(Calendar.MONTH, month - 1);
        // 获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DATE);
        // 设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        return convert2String(cal.getTime(), DateUtil.DATE_FORMAT_YMD);
    }

    // 工具类不需要外部构造。
    private DateUtil() {}
}
