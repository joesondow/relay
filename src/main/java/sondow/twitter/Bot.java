package sondow.twitter;

import twitter4j.Status;

/**
 * The main application logic.
 */
public class Bot {

    /**
     * The object that does the retweeting and unretweeting.
     */
    private Retweeter retweeter;
    private BotConfig botConfig;

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

        // Find this account's retweets of the target account, dating back two weeks or 60 tweets.
        TargetChooser targetChooser = new TargetChooser();
        String targetScreenName = targetChooser.chooseTarget();


        retweeter.unretweet();
        Status retweet = null;
        Status targetTweet = retweeter.findTargetTweet(new CriteriaForStarTrekHour());
        if (targetTweet != null) {
            long tweetId = targetTweet.getId();
//            retweet = retweeter.retweet(tweetId);
        }
        return retweet;
    }
}
