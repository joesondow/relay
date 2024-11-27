package sondow.common;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import sondow.social.Time;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

/**
 * Custom logger because log4j no longer works consistently in AWS Lambda and honestly this is
 * easier.
 */
public class Logger {

    private final Class<?> clazz;

    private Logger(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }

    public void info(Object obj) {
        ZonedDateTime now = ZonedDateTime.now();
        String dateTimeString = Time.FORMATTER.format(now);
        System.out.println(dateTimeString + " " + clazz.getSimpleName() + ": " + obj);
    }
}
