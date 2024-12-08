package sondow.social;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

public class Time {

    private final static Logger log = LogManager.getLogger(Time.class);

    public ZonedDateTime nowUtc() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }

    /**
     * Similar to DateTimeFormatter.ISO_ZONED_DATE_TIME with ZoneOffset.UTC but this version has
     * consistent 3-digit milliseconds so the timestamps all line up vertically.
     */
    public static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
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

    public static ZonedDateTime parse(String zuluTime) {
        return ZonedDateTime.parse(zuluTime, FORMATTER);
    }

    public static String format(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(FORMATTER);
    }

    public void waitASec() {
        log.info("Waiting a second");
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
