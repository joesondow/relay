package twitter4j;

import java.time.Instant;

/**
 * A Twitter card is the implementation mechanism for a Twitter poll, among other uses.
 *
 * @author @JoeSondow
 */
public interface Card {

    /**
     * @return the name
     */
    public String getName();

    /**
     * @return the cardUrl
     */
    public String getCardUrl();

    /**
     * @return the api
     */
    public String getApi();

    /**
     * @return the durationMinutes
     */
    public int getDurationMinutes();

    /**
     * @return the countsAreFinal
     */
    public boolean isCountsAreFinal();

    /**
     * @return the lastUpdatedDateTimeUtc
     */
    public Instant getLastUpdatedDateTimeUtc();

    /**
     * @return the endDatetimeUtc
     */
    public Instant getEndDatetimeUtc();

    /**
     * @return the poll choices
     */
    public Choice[] getChoices();

    /**
     * @return the unique identifier of the card
     */
    public String getUrl();

}
