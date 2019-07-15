package sondow.twitter;

import java.time.ZonedDateTime;
import org.apache.log4j.Logger;

public class Time {

    private final static Logger log = Logger.getLogger(Time.class);

    public ZonedDateTime nowZonedDateTime() {
        return ZonedDateTime.now();
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
