package sondow.social;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;

public class Retweeter {

    private static Logger log = LogManager.getLogger(Retweeter.class);

    private final Time time;
    private final Configuration config;
    private final Twitter twitter;

    /**
     * The unit test constructor.
     *
     * @param twitter the twitter interface
     * @param time    source of current time
     */
    public Retweeter(Time time, Configuration config, Twitter twitter) {
        this.time = time;
        this.config = config;
        this.twitter = twitter;
    }

    /**
     * Production constructor.
     *
     * @param config the twitter configuration
     */
    public Retweeter(Configuration config) {
        this(new Time(), config, new TwitterFactory(config).getInstance());
    }

    public Status retweet(long tweetId) {
        Status status;
        try {
            status = twitter.retweetStatus(tweetId);
            log.info(config.getUser() + " retweeted " + tweetId + " from " + status.getRetweetedStatus().getUser().getScreenName());
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    /**
     * Attempts to delete all the recent retweets from the logged in account's user timeline.
     */
    public void unretweet() {

        List<Status> retweets = new ArrayList<>();
        ZonedDateTime now = time.nowUtc();
        long daysAgoCutOff = 60;
        ZonedDateTime longAgo = now.minus(daysAgoCutOff, ChronoUnit.DAYS);
        Date cutoffDate = Date.from(longAgo.toInstant());

        Long maxId = null;
        Paging paging = new Paging();

        boolean checkingRecentTweets = true;
        // EmojiAquarium tweets 8x/day, 480x in 60 days. 20 tweets per page. 480/20 = 24
        int maxPages = 24;
        for (int i = 0; i < maxPages && checkingRecentTweets; i++) {
            try {
                ResponseList<Status> userTimeline = twitter.getUserTimeline(paging);
                int tweetCountInPage = userTimeline.size();
                if (tweetCountInPage <= 0) {
                    checkingRecentTweets = false; // End of timeline, stop checking.
                }
                for (int u = 0; u < tweetCountInPage && checkingRecentTweets; u++) {
                    Status tweet = userTimeline.get(u);
                    if (tweet.isRetweet()) {
                        retweets.add(tweet);
                    }
                    if (maxId == null) {
                        maxId = tweet.getId();
                    } else {
                        maxId = Math.min(maxId, tweet.getId());
                    }
                    paging = new Paging();
                    paging.setMaxId(maxId - 1);

                    Date createdAt = tweet.getCreatedAt();
                    if (createdAt.before(cutoffDate)) {
                        checkingRecentTweets = false;
                    }
                }
            } catch (TwitterException e) {
                throw new RuntimeException(e);
            }
        }
        for (Status retweet : retweets) {
            Status retweetedStatus = retweet.getRetweetedStatus();
            String text = retweetedStatus.getText();
            String screenName = retweetedStatus.getUser().getScreenName();
            try {
                long id = retweet.getId();
                log.info("Deleting " + config.getUser() + " retweet " + id + " of " + screenName +
                        " " +
                        retweet.getRetweetedStatus().getId() + " with text '" + text + "'");
                twitter.destroyStatus(id);
            } catch (TwitterException e) {
                throw new RuntimeException(e);
            }
        }
    }

    Status findTargetPopularTweet() {

        ResponseList<Status> userTimeline;
        try {
            userTimeline = twitter.getUserTimeline();
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }
        int highestRetweetCount = 0;
        int highestFavoriteCountForMostRetweeted = 0;
        Status mostPopularTweet = null;
        for (Status tweet : userTimeline) {
            if (!tweet.isRetweet()) {
                int retweetCount = tweet.getRetweetCount();
                int favoriteCount = tweet.getFavoriteCount();
                if (retweetCount > highestRetweetCount) {
                    mostPopularTweet = tweet;
                    highestRetweetCount = retweetCount;
                    highestFavoriteCountForMostRetweeted = favoriteCount;
                } else if (retweetCount == highestRetweetCount) {
                    if (favoriteCount > highestFavoriteCountForMostRetweeted) {
                        highestFavoriteCountForMostRetweeted = favoriteCount;
                        mostPopularTweet = tweet;
                    }
                }
            }
        }
        log.info(mostPopularTweet.getId() + " rt:" + mostPopularTweet.getRetweetCount() + " " +
                mostPopularTweet.getText());

        return mostPopularTweet;
    }
}
