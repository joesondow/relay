package sondow.twitter;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.apache.log4j.Logger;

public class Time {

    private final static Logger log = Logger.getLogger(Time.class);

    public ZonedDateTime nowUtc() {
        return ZonedDateTime.now(ZoneOffset.UTC);
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
