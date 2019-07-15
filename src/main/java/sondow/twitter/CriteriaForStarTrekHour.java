package sondow.twitter;

import java.util.Date;
import twitter4j.Status;

public class CriteriaForStarTrekHour implements Criteria {

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
    @Override
    public boolean matches(Status tweet) {
        Date lastWeek = Date.from(time.nowZonedDateTime().minusDays(7).toInstant());
        return (!tweet.isRetweet()) &&
                tweet.getUser().getScreenName().equals("StarTrekHour") &&
                tweet.getCreatedAt().after(lastWeek) &&
                tweet.getText().startsWith("Tues,") &&
                tweet.getURLEntities().length == 1 &&
                tweet.getURLEntities()[0].getExpandedURL().contains("docs.google.com/forms") &&
                tweet.getMediaEntities().length == 1;
    }

    public String screenName() {
        return "StarTrekHour";
    }
}
