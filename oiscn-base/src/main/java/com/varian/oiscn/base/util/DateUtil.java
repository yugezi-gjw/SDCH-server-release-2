package com.varian.oiscn.base.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by gbt1220 on 3/2/2017.
 */
@Slf4j
public final class DateUtil {
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String SHORT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public static final String HOUR_MINUTE_TIME_FORMAT = "HH:mm";

    public static final String HOUR_MINUTE_AM_FORMAT = "h:mm a";

    private static final String DATE_PATTERN = "\\d{4}-\\d{1,2}-\\d{1,2}";

    private static final String DATE_TIME_PATTERN = "\\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2}:\\d{2}";

    private static final String SHORT_DATE_TIME_PATTERN = "\\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2}";

    private static final String ONLY_TIME_PATTERN = "\\d{1,2}:\\d{1,2}";

    private static final String HOUR_MINUTE_AM_PATTERN = "\\d{1,2}:\\d{1,2} [\\u4e00-\\u9fa5]+";

    private DateUtil(){

    }

    public static Date parse(String input) throws ParseException {
        if (input == null || input.trim().length() == 0) {
            return null;
        }
        String dateString = input.trim();
        if (Pattern.matches(DATE_PATTERN, dateString)) {
            return new SimpleDateFormat(DATE_FORMAT).parse(dateString);
        } else if (Pattern.matches(DATE_TIME_PATTERN, dateString)) {
            return new SimpleDateFormat(DATE_TIME_FORMAT).parse(dateString);
        } else if (Pattern.matches(SHORT_DATE_TIME_PATTERN, dateString)) {
            return new SimpleDateFormat(SHORT_DATE_TIME_FORMAT).parse(dateString);
        } else if (Pattern.matches(ONLY_TIME_PATTERN, dateString)) {
            return new SimpleDateFormat(HOUR_MINUTE_TIME_FORMAT).parse(dateString);
        } else if (Pattern.matches(HOUR_MINUTE_AM_PATTERN,dateString)){
            return new SimpleDateFormat(HOUR_MINUTE_AM_FORMAT).parse(dateString);
        }
        throw new ParseException("datetime cannot be parsed! dateString is " + dateString, 1);
    }

    public static String formatDate(Date date, String formatter) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatter);
        return dateFormat.format(date);
    }

    public static boolean isAtInterval(String dateTimeStr, String beginDateTimeStr, String endDateTimeStr) {
        boolean result;
        if (StringUtils.isBlank(dateTimeStr) || StringUtils.isBlank(beginDateTimeStr) || StringUtils.isBlank(endDateTimeStr)) {
            result = false;
        } else {
            try {
                Date date = parse(dateTimeStr);
                Date beginDate = parse(beginDateTimeStr);
                Date endDate = parse(endDateTimeStr);
                result = !(date.before(beginDate) || date.after(endDate));
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
                result = false;
            }
        }
        return result;
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateFormat.format(new Date());
    }

    public static Date getToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(dateString, pos);
    }
    
    public static Date addMillSecond(Date date, int millisecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, millisecond);
        return calendar.getTime();
    }

    public static Date addDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }
    
    /**
     * Transfer the date from java.util.Date to java.sql.date.<br>
     * @param date A Date of java.util.date
     * @return
     */
    public static java.sql.Date transferDateFromUtilToSql(Date date) {
    	return new java.sql.Date(date.getTime());
    }

    /**
     * Transfer the date from java.util.Date to java.sql.Timestamp.<br>
     *
     * @param date A Date of java.util.date
     * @return
     */
    public static java.sql.Timestamp transferTimestampFromUtilToSql(Date date) {
        return new java.sql.Timestamp(date.getTime());
    }

    /**
     * 将开始日期至结束日期分解成单个日期
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<String> splitDateRange(String startDate, String endDate){
        List<String> dateList = new ArrayList<>();
        try {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(DateUtil.parse(startDate));

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(DateUtil.parse(endDate));
            for (; startCalendar.compareTo(endCalendar) <= 0; startCalendar.add(Calendar.DAY_OF_MONTH, 1)) {
                dateList.add(DateUtil.formatDate(startCalendar.getTime(), DateUtil.DATE_FORMAT));
            }
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }

        return dateList;
    }

}
