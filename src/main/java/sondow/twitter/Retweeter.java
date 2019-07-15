package sondow.twitter;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;

public class Retweeter {

    private static Logger log = Logger.getLogger(Retweeter.class);

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
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    /**
     * Attempts to delete all the recent retweets of the specified account, from the logged in
     * account's user timeline.
     *
     * @param twitterHandle the owner of the tweets that are to be unretweeted
     */
    public void unretweet(String twitterHandle) {

        List<Status> retweets = new ArrayList<>();
        ZonedDateTime now = time.nowZonedDateTime();
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
                    if (tweet.isRetweet() && twitterHandle
                            .equals(tweet.getRetweetedStatus().getUser().getScreenName())) {
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
            try {
                log.info("Deleting " + config.getUser() + " retweet of " + twitterHandle + " " +
                        retweet.getRetweetedStatus().getId() + " ");
                twitter.destroyStatus(retweet.getId());
            } catch (TwitterException e) {
                throw new RuntimeException(e);
            }
        }
    }

   Status findTargetTweet(Criteria criteria) {
       try {
           ResponseList<Status> userTimeline = twitter.getUserTimeline(criteria.screenName());
           for (Status tweet : userTimeline){
               if (criteria.matches(tweet)){
                   return tweet;
               }
           }
       } catch (TwitterException e) {
           throw new RuntimeException(e);
       }

       return null;
   }

    public static void main(String[] args) {
        BotConfig botConfig = new BotConfigFactory().configure();
        Configuration configuration = botConfig.getTwitterConfig();

            Retweeter retweeter = new Retweeter(configuration);
            retweeter.unretweet("EmojiSnakeGame");
            //retweeter.retweet(1141508128162246656L);

    }
}
