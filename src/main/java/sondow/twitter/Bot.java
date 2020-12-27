package sondow.twitter;

import twitter4j.Status;
import twitter4j.conf.Configuration;

/**
 * The main application logic.
 */
public class Bot {

    /**
     * The object that does the retweeting and unretweeting.
     */
    private Retweeter retweeter;
    private BotConfig botConfig;
    private Time time;

    /**
     * Constructs a new bot with the specified Retweeter, for unit testing.
     *
     * @param retweeter the object that retweets and unretweets
     */
    Bot(Retweeter retweeter) {
        init();
        this.retweeter = retweeter;
    }

    /**
     * Constructs a new bot for production.
     */
    Bot() {
        init();
        this.retweeter = new Retweeter(botConfig.getPollReadingTwitterConfig());
    }

    private void init() {
        this.botConfig = new BotConfigFactory().configure();
    }

    /**
     * Performs the application logic.
     *
     * @return the posted tweet
     */
    public Status go() {

        // Find this account's retweets dating back two weeks or 60 tweets.
        AccountChooser accountChooser = new AccountChooser(botConfig);
        String targetScreenName = accountChooser.chooseTarget();
        Configuration twitterConfig = botConfig.getConfig(targetScreenName);

        Retweeter targetRetweeter = new Retweeter(twitterConfig);
        targetRetweeter.unretweet();

        Criteria criteria = null;
        if (botConfig.isPollAccount(targetScreenName)) {
            criteria = Criteria.POLL;
//        } else if (botConfig.isPinnedAccount(targetScreenName)) {
//            criteria = Criteria.PINNED;
        } else {
            criteria = Criteria.POPULAR;
        }

        Status retweet = null;
        Status targetTweet = targetRetweeter.findTargetTweet(criteria);
        if (targetTweet != null) {
            long tweetId = targetTweet.getId();
//            retweet = retweeter.retweet(tweetId);
        }
        return retweet;
    }
}
