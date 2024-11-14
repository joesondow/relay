package sondow.common;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
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

    /**
     * Similar to DateTimeFormatter.ISO_ZONED_DATE_TIME with ZoneOffset.UTC but this version has
     * consistent 3-digit milliseconds so the timestamps all line up vertically.
     */
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral('T')
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .optionalStart()
            .appendLiteral('.')
            .appendValue(MILLI_OF_SECOND, 3)
            .optionalStart()
            .appendLiteral('Z')
            .toFormatter()
            .withZone(ZoneOffset.UTC);

    private final Class<?> clazz;

    private Logger(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }

    public void info(Object obj) {
        ZonedDateTime now = ZonedDateTime.now();
        String dateTimeString = FORMATTER.format(now);
        System.out.println(dateTimeString + " " + clazz.getSimpleName() + ": " + obj);
    }
}
