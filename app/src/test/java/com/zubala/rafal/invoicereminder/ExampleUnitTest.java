package com.zubala.rafal.invoicereminder;

import android.text.format.Time;
import android.util.Log;

import com.zubala.rafal.invoicereminder.utils.DateUtils;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testAddWeek() {
        Long timestamp = DateUtils.toDate(2018, 1, 31);
        Long timestampTest = DateUtils.addWeeks(timestamp, 1);
        Long timestampExpected = DateUtils.toDate(2018, 2, 7);
        assertEquals(timestampExpected, timestampTest);
    }

    @Test
    public void testAddDay() {
        Long timestamp = DateUtils.toDate(2018, 1, 31);
        Long timestampTest = DateUtils.addDays(timestamp, 1);
        Long timestampExpected = DateUtils.toDate(2018, 2, 1);
        assertEquals(timestampExpected, timestampTest);
    }

    @Test
    public void testAddMonth() {
        Long timestamp = DateUtils.toDate(2018, 1, 31);
        Long timestampTest = DateUtils.addMonths(timestamp, 1);
        Long timestampExpected = DateUtils.toDate(2018, 2, 28);
        assertEquals(timestampExpected, timestampTest);

        timestamp = DateUtils.toDate(2018, 2,10);
        timestampTest = DateUtils.addMonths(timestamp, 2);
        timestampExpected = DateUtils.toDate(2018, 4, 10);
        assertEquals(timestampExpected, timestampTest);

        timestamp = DateUtils.toDate(2018, 2,28);
        timestampTest = DateUtils.addMonths(timestamp, 1);
        timestampExpected = DateUtils.toDate(2018, 3, 28);
        assertEquals(timestampExpected, timestampTest);

        timestamp = DateUtils.toDate(2018, 3,31);
        timestampTest = DateUtils.addMonths(timestamp, 6);
        timestampExpected = DateUtils.toDate(2018, 9, 30);
        assertEquals(timestampExpected, timestampTest);
    }

    private String toDateStr(long timestamp) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(timestamp);
        return calendar.get(Calendar.YEAR)+"-"
                +String.format("%02d", calendar.get(Calendar.MONTH)+1)
                +"-"+String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
                + " " + String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))
                +":"+String.format("%02d", calendar.get(Calendar.MINUTE))
                +":"+String.format("%02d", calendar.get(Calendar.SECOND))
                +"."+String.format("%02d", calendar.get(Calendar.MILLISECOND));
    }
}