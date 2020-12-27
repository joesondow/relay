package sondow.twitter;

import java.util.Date;
import twitter4j.Status;

public class CriteriaForStarTrekHour {

    private Time time;

    CriteriaForStarTrekHour() {
        this(new Time());
    }

    CriteriaForStarTrekHour(Time time) {
        this.time = time;
    }

    // Criteria:
    // not a retweet,
    // a tweet from StarTrekHour,
    // made in the last 7 days,
    // starts with the word "Tues,",
    // contains exactly one link,
    // the link's extended target contains "docs.google.com/forms",
    // has exactly one image
    public boolean matches(Status tweet) {
        Date lastWeek = Date.from(time.now().minusDays(7).toInstant());
        boolean isRetweet = tweet.isRetweet();
        boolean tweetedByStarTrekHour = tweet.getUser().getScreenName().equals("StarTrekHour");
        boolean isRecent = tweet.getCreatedAt().after(lastWeek);
        boolean startsWithTues = tweet.getText().startsWith("Tues,");
        boolean hasOneUrl = tweet.getURLEntities().length == 1;
        boolean urlLinksToForm = tweet.getURLEntities()[0].getDisplayURL()
                .contains("docs.google.com/forms");
        boolean hasOneImage = tweet.getMediaEntities().length == 1;
        return (!isRetweet) &&
                tweetedByStarTrekHour &&
                isRecent &&
                startsWithTues &&
                hasOneUrl &&
                urlLinksToForm &&
                hasOneImage;
    }

    public String screenName() {
        return "StarTrekHour";
    }
}
