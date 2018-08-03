package com.varian.oiscn.base.util;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by gbt1220 on 3/2/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class DateUtilTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void givenADateStringWhenParseThenReturnDate() throws ParseException {
        String dateString = "2017-03-02";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        Assert.assertEquals(date, DateUtil.parse(dateString));
    }

    @Test
    public void givenADateTimeStringWhenParseThenReturnDate() throws ParseException {
        String dateString = "2017-03-02 10:00:00";
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
        Assert.assertEquals(date, DateUtil.parse(dateString));
    }

    @Test
    public void givenAShortDateTimeStringWhenParseThenReturnDate() throws ParseException {
        String dateString = "2017-03-02 10:00";
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString);
        Assert.assertEquals(date, DateUtil.parse(dateString));
    }

    @Test
    public void givenOnlyTimeStringWhenParseThenReturnDate() throws ParseException {
        String dateString = "10:00";
        Date date = new SimpleDateFormat("HH:mm").parse(dateString);
        Assert.assertEquals(date, DateUtil.parse(dateString));
    }

    @Test
    public void givenCannotParseStringWhenParseThenThrowParseException() throws ParseException {
        String dateString = "CannotParseDate";
        thrown.expect(ParseException.class);
        thrown.expectMessage("datetime cannot be parsed! dateString is " + dateString);
        DateUtil.parse(dateString);
    }

    @Test
    public void givenADateWhenFormatThenReturnFormatString() {
        String dateTimeFormat = DateUtil.formatDate(Calendar.getInstance().getTime(), DateUtil.DATE_TIME_FORMAT);
        Assert.assertTrue(dateTimeFormat.length() > 0);
    }

    @Test
    public void givenAnEmptyTimeWhenThenReturnFalse() {
        Assert.assertFalse(DateUtil.isAtInterval("", givenBeginDateTimeString(), givenEndDateTimeString()));
        Assert.assertFalse(DateUtil.isAtInterval(givenBeginDateTimeString(), "", givenEndDateTimeString()));
        Assert.assertFalse(DateUtil.isAtInterval(givenBeginDateTimeString(), givenEndDateTimeString(), ""));
    }

    @Test
    public void givenAnErrorFormatTimeWhenThenReturnFalse() {
        Assert.assertFalse(DateUtil.isAtInterval("2016-11-10", givenBeginDateTimeString(), givenEndDateTimeString()));
    }

    @Test
    public void givenAnIntervalTimeWhenThenReturnTrue() {
        String dateTimeStr = "2017-03-07 11:00";
        Assert.assertTrue(DateUtil.isAtInterval(dateTimeStr, givenBeginDateTimeString(), givenEndDateTimeString()));
    }

    @Test
    public void givenAnBeforeTimeWhenThenReturnFalse() {
        String dateTimeStr = "2017-03-07 09:00";
        Assert.assertFalse(DateUtil.isAtInterval(dateTimeStr, givenBeginDateTimeString(), givenEndDateTimeString()));
    }

    @Test
    public void givenAnStartTimeWhenThenReturnTrue() {
        String dateTimeStr = "2017-03-07 10:00";
        Assert.assertTrue(DateUtil.isAtInterval(dateTimeStr, givenBeginDateTimeString(), givenEndDateTimeString()));
    }

    @Test
    public void givenAnEndTimeWhenThenReturnTrue() {
        String dateTimeStr = "2017-03-07 12:00";
        Assert.assertTrue(DateUtil.isAtInterval(dateTimeStr, givenBeginDateTimeString(), givenEndDateTimeString()));
    }

    @Test
    public void givenAnAfterTimeWhenThenReturnFalse() {
        String dateTimeStr = "2017-03-07 13:00";
        Assert.assertFalse(DateUtil.isAtInterval(dateTimeStr, givenBeginDateTimeString(), givenEndDateTimeString()));
    }

    @Test
    public void givenWhenGetCurrentDateThenReturnCurrentDate() {
        Assert.assertNotNull(DateUtil.getCurrentDate());
    }
    
    @Test
    public void testTransferDateFromUtilToSql() {
    	Date now = new Date();
    	java.sql.Date sqlNow = DateUtil.transferDateFromUtilToSql(now);
    	Assert.assertEquals(now.getTime(), sqlNow.getTime());
    }

    @Test
    public void testTransferTimestampFromUtilToSql() {
    	Date now = new Date();
    	java.sql.Timestamp sqlNow = DateUtil.transferTimestampFromUtilToSql(now);
    	Assert.assertEquals(now.getTime(), sqlNow.getTime());
    }
    
    @Test
    public void testGetToday() {
        Date today = DateUtil.getToday();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        Assert.assertEquals(0, c.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, c.get(Calendar.MINUTE));
        Assert.assertEquals(0, c.get(Calendar.SECOND));
    }

    @Test
    public void testAddDay() {
        int addDay = 88;
        Date today = DateUtil.getToday();
        Date tomorrow = DateUtil.addDay(today, addDay);

        Assert.assertEquals(86400 * addDay, (tomorrow.getTime() - today.getTime()) / 1000);
    }
    
    @Test
    public void testAddMillSecond() {
        int add = 12;
        Date today = DateUtil.getToday();
        Date next = DateUtil.addMillSecond(today, add);

        Assert.assertEquals(add, (next.getTime() - today.getTime()));
    }
    
    @Test
    public void testSplitDateRange() {
    	List<String> dateList = DateUtil.splitDateRange("2018-03-12", "2018-04-12");
        Assert.assertNotNull(dateList);
        Assert.assertEquals(32, dateList.size());
    }
    
    private String givenBeginDateTimeString() {
        return "2017-03-07 10:00";
    }

    private String givenEndDateTimeString() {
        return "2017-03-07 12:00";
    }
}
